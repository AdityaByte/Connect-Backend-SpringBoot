package com.connect.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.connect.pojo.OTPDetails;;

@Service
public class OTPService {

    private static final SecureRandom random = new SecureRandom();
    private static final Map<String, OTPDetails> otpStore = new HashMap<>();

    public static String generateOTPForUser(String userEmail) {
        int otp = 1000 + random.nextInt(9000);
        OTPDetails otpDetails = new OTPDetails(String.valueOf(otp), 120); // Valid for 2 minutes.
        otpStore.put(userEmail, otpDetails);
        return otpDetails.getOTP();
    }

    public static boolean verifyOTP(String userEmail, String otp) {
        OTPDetails otpDetails = otpStore.get(userEmail);
        if (otpDetails == null || otpDetails.isExpired()) {
            throw new RuntimeException("OTP is expired");
        }
        return otpDetails.getOTP().equals(otp);
    }
}
