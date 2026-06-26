package com.example.autoproject.controller;

import com.example.autoproject.common.Result;
import com.example.autoproject.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/benchmark")
@RequiredArgsConstructor
public class VirtualThreadBenchController {

    private final RegistrationService registrationService;
    private final DataSource dataSource;

    /**
     * 模拟报名: 150 人并发抢 100 名额 (Redis ZSET + MySQL)
     * POST /api/v1/benchmark/register?count=150
     */
    @PostMapping("/register")
    public Result<String> benchmarkRegister(@RequestParam(defaultValue = "150") int count) throws Exception {
        registrationService.clearAll();
        var carriers = new ConcurrentSkipListSet<Long>();
        var queueCounts = new ConcurrentLinkedQueue<Long>();
        Instant start = Instant.now();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<Integer>>();
            for (int i = 1; i <= count; i++) {
                final int idx = i;
                futures.add(executor.submit(() -> {
                    carriers.add(Thread.currentThread().threadId());
                    return registrationService.register(1L, "学生" + idx, "S" + String.format("%04d", idx));
                }));
            }
            for (var f : futures) {
                Integer rank = f.get(10, TimeUnit.SECONDS);
                if (rank != null && rank > 0) queueCounts.add((long) rank);
            }
        }

        long queueSize = registrationService.getQueueCount();
        Duration elapsed = Duration.between(start, Instant.now());

        String result = String.format(
                "【Redis ZSET + MySQL 报名压测】\n" +
                "报名人数: %d | 队列长度: %d | 耗时: %dms | 吞吐: %.0f ops/s\n" +
                "Carrier线程: %d 个 | 虚拟线程数: %d\n" +
                "Pinning判定: %s",
                count, queueSize, elapsed.toMillis(),
                count * 1000.0 / elapsed.toMillis(),
                carriers.size(), count,
                carriers.size() <= 10 ? "⚠️ SUSPECTED PINNING" : "✅ NORMAL"
        );
        log.info(result.replace("\n", " | "));
        return Result.ok(result);
    }

    /**
     * 模拟考核: 对前 100 名打分, 不通过者递补
     * POST /api/v1/benchmark/exam
     */
    @PostMapping("/exam")
    public Result<String> benchmarkExam() throws Exception {
        var admitted = registrationService.getAdmitted();
        int passCount = 0, failCount = 0;
        Instant start = Instant.now();

        for (String studentId : admitted) {
            var regs = new ArrayList<Long>();
            // 查 MySQL 取 registration id
            try (var conn = dataSource.getConnection();
                 var stmt = conn.prepareStatement(
                     "SELECT id FROM biz_registration WHERE student_id = ? AND deleted = 0")) {
                stmt.setString(1, studentId);
                var rs = stmt.executeQuery();
                while (rs.next()) regs.add(rs.getLong("id"));
            }
            if (!regs.isEmpty()) {
                registrationService.exam(regs.get(0));
                // 简单判断: exam 后 status=2 为通过
                try (var conn = dataSource.getConnection();
                     var stmt = conn.prepareStatement(
                         "SELECT status FROM biz_registration WHERE id = ?")) {
                    stmt.setLong(1, regs.get(0));
                    var rs = stmt.executeQuery();
                    if (rs.next()) {
                        int status = rs.getInt("status");
                        if (status == 2) passCount++; else failCount++;
                    }
                }
            }
        }

        var rejected = registrationService.getAdmitted();
        Duration elapsed = Duration.between(start, Instant.now());

        String result = String.format(
                "【Redis 考核 + 递补压测】\n" +
                "考核人数: %d | 通过: %d | 未通过: %d | 录取: %d 人 | 耗时: %dms",
                admitted.size(), passCount, failCount,
                rejected.size(), elapsed.toMillis()
        );
        log.info(result.replace("\n", " | "));
        return Result.ok(result);
    }

    /**
     * 虚拟线程兼容性检测
     * GET /api/v1/benchmark/info
     */
    @GetMapping("/info")
    public Result<String> info() {
        var t = Thread.currentThread();
        return Result.ok(String.format(
                "Thread: %s | Virtual: %s | PID: %d | CPUs: %d | Redis host: %s",
                t.getName(), t.isVirtual(),
                ProcessHandle.current().pid(),
                Runtime.getRuntime().availableProcessors(),
                System.getenv().getOrDefault("REDIS_HOST", "redis")
        ));
    }
}
