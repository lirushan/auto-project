package com.example.autoproject.service.impl;

import com.example.autoproject.common.BusinessException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {

    private final ConcurrentHashMap<String, CodeEntry> store = new ConcurrentHashMap<>();

    record CodeEntry(String code, long expireAt) {}

    /**
     * 发送验证码 (Mock 123456, 60s 频率限制, 5min TTL)
     */
    public void sendCode(String phone) {
        CodeEntry last = store.get(phone);
        if (last != null && System.currentTimeMillis() - (last.expireAt - 300000) < 60000) {
            throw new BusinessException(429, "发送过于频繁，请60秒后重试");
        }
        store.put(phone, new CodeEntry("123456", System.currentTimeMillis() + 300000));
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String phone, String code) {
        CodeEntry entry = store.get(phone);
        if (entry == null || System.currentTimeMillis() > entry.expireAt) {
            return false;
        }
        return entry.code.equals(code);
    }
}
