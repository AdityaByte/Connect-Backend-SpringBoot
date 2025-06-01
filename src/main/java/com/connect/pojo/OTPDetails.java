package com.connect.pojo;

public class OTPDetails {
    private String otp;
    private long expiryTime;

    public OTPDetails(String otp, int validitySeconds) {
        this.otp = otp;
        this.expiryTime = System.currentTimeMillis() + (validitySeconds * 1000L); // Crucial bug
    }

    public String getOTP() {
        return this.otp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
