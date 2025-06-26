package com.connect.service;

import java.security.SecureRandom;
import com.connect.dto.OtpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OTPService {

    private final RedisService redisService;

    private static final SecureRandom random = new SecureRandom();

    public String generateOTPForUser(String email) {

        // Generating OTP ( 4 characters )
        int otp = 1000 + random.nextInt(9000);

        OtpDTO otpDTO = OtpDTO.builder()
                .otp(String.valueOf(otp))
                .email(email)
                .build();

        // Caching the OTP with default time-limit of 3 minutes.
        redisService.cacheOTPWithTTL(otpDTO);

        return String.valueOf(otp);
    }

    public boolean verifyOTP(String email, String otp) {

        OtpDTO otpDTO = redisService.getOTP(email);

        if (otpDTO == null) {
            log.error("OTP is expired");
            return false;
        } else if (otpDTO.getEmail() != null && !otpDTO.getEmail().equals(email)) {
            log.error("Target Email is different");
            return false;
        } else if (otpDTO.getOtp() != null && !otpDTO.getOtp().equals(otp)) {
            log.error("Invalid OTP");
            return false;
        }
        return true;
    }
}
