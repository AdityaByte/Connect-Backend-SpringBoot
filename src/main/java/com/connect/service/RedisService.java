package com.connect.service;

import com.connect.dto.OtpDTO;
import com.connect.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// Redis Service for Redis Operation.

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheUserWithTTL(User user) {
        redisTemplate.opsForValue().set("user:"+user.getEmail(), user);
    }

    public User getUser(String key) {
        // Key is the email.
        return (User) redisTemplate.opsForValue().get("user:"+key);
    }

    // Method for explicitly removing the user
    public boolean removeUser(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete("user:" + key));
    }

    public void cacheOTPWithTTL(OtpDTO otpDTO) {
        redisTemplate.opsForValue().set("otp:"+otpDTO.getEmail(), otpDTO, 5, TimeUnit.MINUTES);
    }

    public OtpDTO getOTP(String key) {
        return (OtpDTO) redisTemplate.opsForValue().get("otp:"+key);
    }

    public boolean removeOTP(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete("otp:"+key));
    }
}
