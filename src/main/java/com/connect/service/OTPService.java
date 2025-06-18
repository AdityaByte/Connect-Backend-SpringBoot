package com.connect.service;

import java.security.SecureRandom;
import com.connect.dto.OtpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OTPService {

    @Autowired
    private RedisService redisService;

    private static final SecureRandom random = new SecureRandom();

    public String generateOTPForUser(String userEmail) {
        int otp = 1000 + random.nextInt(9000);
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setOtp(String.valueOf(otp));
        otpDTO.setEmail(userEmail);
        redisService.cacheOTPWithTTL(otpDTO);
        return String.valueOf(otp);
    }

    public boolean verifyOTP(String userEmail, String otp) {
        OtpDTO otpDTO = redisService.getOTP(userEmail);
        if (otpDTO == null) {
            log.error("OTP is expired");
            return false;
        } else if (otpDTO.getEmail() != null && !otpDTO.getEmail().equals(userEmail)) {
            log.error("Target Email is different");
            return false;
        } else if (otpDTO.getOtp() != null && !otpDTO.getOtp().equals(otp)) {
            log.error("Invalid OTP");
            return false;
        }
        return true;
    }
}
