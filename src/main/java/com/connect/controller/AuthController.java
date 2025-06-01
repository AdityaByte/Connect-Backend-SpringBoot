package com.connect.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.model.OTP;
import com.connect.model.User;
import com.connect.service.UserService;

@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    // Handler method for handling the signup functionality.
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signupHandler(@RequestBody User user) {
        System.out.println("Request recieved");
        System.out.println(user.toString());
        userService.sendOTP(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("response", "OTP sent to your email"));
    }

    @PostMapping("/signup/verifyOTP")
    public ResponseEntity<Map<String, String>> verifyOTPHandler(@RequestBody OTP otp) {
        userService.createUser(otp.getOtp(), otp.getEmail());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("response", "User created successfully"));
    }

    @GetMapping("/signup/resendOTP")
    public ResponseEntity<Map<String, String>> resendOTPHandler(@RequestParam("email") String email) {
        userService.resendOTP(email);
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(Map.of("response", "OTP sent successfully"));
    }

    // Handler method for handling the login functionality.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginHandler(@RequestBody User user) {
        System.out.println("Request Recieved");
        System.out.println(user.toString());
        userService.checkUser(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("response", "Valid Credentials"));
    }
}