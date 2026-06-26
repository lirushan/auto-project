package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.ReviewResultDTO;
import com.example.autoproject.entity.Registration;
import com.example.autoproject.mapper.CourseMapper;
import com.example.autoproject.mapper.RegistrationMapper;
import com.example.autoproject.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final StringRedisTemplate redis;
    private final RegistrationMapper registrationMapper;
    private final CourseMapper courseMapper;

    private static String seqKey(Long courseId) { return "biz:course:" + courseId + ":seq"; }
    private static String queueKey(Long courseId) { return "biz:course:" + courseId + ":queue"; }

    @Override
    @Transactional
    public Integer register(Long courseId, String studentName, String studentId) {
        // 检查课程序号
        Long seq = redis.opsForValue().increment(seqKey(courseId));
        int rankNum = seq != null ? seq.intValue() : 0;

        // Redis ZADD
        redis.opsForZSet().add(queueKey(courseId), studentId, rankNum);

        // MySQL 持久化
        Registration reg = new Registration();
        reg.setStudentName(studentName);
        reg.setStudentId(studentId);
        reg.setCourseId(courseId);
        reg.setRegisterTime(LocalDateTime.now());
        reg.setRankNum(rankNum);
        reg.setStatus(0); // 0=排队
        reg.setCreateTime(LocalDateTime.now());
        reg.setUpdateTime(LocalDateTime.now());
        registrationMapper.insert(reg);

        log.debug("学生 {} 报名课程 {} 成功, 序号 {}", studentName, courseId, rankNum);
        return rankNum;
    }

    @Override
    @Transactional
    public int approveBatch(Long courseId, List<Long> regIds) {
        for (Long regId : regIds) {
            Registration reg = registrationMapper.selectById(regId);
            if (reg == null) continue;
            reg.setStatus(2); // 通过
            reg.setUpdateTime(LocalDateTime.now());
            registrationMapper.updateById(reg);
            // 从排队队列移除
            redis.opsForZSet().remove(queueKey(courseId), reg.getStudentId());
        }
        return regIds.size();
    }

    @Override
    @Transactional
    public ReviewResultDTO rejectBatch(Long courseId, List<Long> regIds) {
        int rejected = 0;
        int successors = 0;
        for (Long regId : regIds) {
            Registration reg = registrationMapper.selectById(regId);
            if (reg == null) continue;
            reg.setStatus(3); // 拒绝
            reg.setUpdateTime(LocalDateTime.now());
            registrationMapper.updateById(reg);
            // 从排队队列移除
            redis.opsForZSet().remove(queueKey(courseId), reg.getStudentId());
            rejected++;

            // 递补: 取队列中下一个
            Set<String> nextSet = redis.opsForZSet().range(queueKey(courseId), 0, 0);
            if (nextSet != null && !nextSet.isEmpty()) {
                String nextStudentId = nextSet.iterator().next();
                List<Registration> nextRegs = registrationMapper.selectList(
                        new LambdaQueryWrapper<Registration>()
                                .eq(Registration::getStudentId, nextStudentId)
                                .eq(Registration::getCourseId, courseId)
                                .eq(Registration::getStatus, 0));
                if (!nextRegs.isEmpty()) {
                    Registration nextReg = nextRegs.get(0);
                    nextReg.setStatus(4); // 递补
                    nextReg.setUpdateTime(LocalDateTime.now());
                    registrationMapper.updateById(nextReg);
                    redis.opsForZSet().remove(queueKey(courseId), nextStudentId);
                    successors++;
                    log.info("递补: 课程 {} 学生 {} 递补进入考核", courseId, nextReg.getStudentName());
                }
            }
        }
        return new ReviewResultDTO(rejected, successors);
    }

    @Override
    @Transactional
    public void exam(Long regId) {
        Registration reg = registrationMapper.selectById(regId);
        if (reg == null) return;

        BigDecimal score = BigDecimal.valueOf(30 + Math.random() * 70).setScale(2, java.math.RoundingMode.HALF_UP);
        boolean passed = score.compareTo(new BigDecimal("60")) >= 0;

        reg.setExamTime(LocalDateTime.now());
        reg.setExamScore(score);
        reg.setStatus(passed ? 2 : 3);
        registrationMapper.updateById(reg);

        if (!passed && reg.getCourseId() != null) {
            // 淘汰: 从 Redis 队列移除
            redis.opsForZSet().remove(queueKey(reg.getCourseId()), reg.getStudentId());

            // 递补
            var course = courseMapper.selectById(reg.getCourseId());
            if (course != null) {
                int quota = course.getQuota() != null ? course.getQuota() : 100;
                Set<String> next = redis.opsForZSet().range(queueKey(reg.getCourseId()), quota - 1, quota - 1);
                if (next != null && !next.isEmpty()) {
                    String nextStudentId = next.iterator().next();
                    List<Registration> nextRegs = registrationMapper.selectList(
                            new LambdaQueryWrapper<Registration>()
                                    .eq(Registration::getStudentId, nextStudentId)
                                    .eq(Registration::getCourseId, reg.getCourseId()));
                    if (!nextRegs.isEmpty()) {
                        Registration nextReg = nextRegs.get(0);
                        nextReg.setStatus(4);
                        nextReg.setUpdateTime(LocalDateTime.now());
                        registrationMapper.updateById(nextReg);
                        log.info("递补: {} → 学生 {} 递补进入考核", reg.getStudentName(), nextReg.getStudentName());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getAdmitted() {
        // Legacy: This method is course-agnostic; keep for backward compat.
        return List.of();
    }

    @Override
    public Long getQueueCount() {
        // Return total across all course queues (approximate)
        long total = 0;
        var keys = redis.keys("biz:course:*:queue");
        if (keys != null) {
            for (String key : keys) {
                Long count = redis.opsForZSet().zCard(key);
                if (count != null) total += count;
            }
        }
        return total;
    }

    @Override
    public void clearAll() {
        var keys = redis.keys("biz:course:*:seq");
        if (keys != null && keys.size() > 0) redis.delete(keys);
        var qkeys = redis.keys("biz:course:*:queue");
        if (qkeys != null && qkeys.size() > 0) redis.delete(qkeys);
        redis.delete("biz:reg:seq");
        redis.delete("biz:registration:queue");
        registrationMapper.delete(new LambdaQueryWrapper<>());
    }
}
