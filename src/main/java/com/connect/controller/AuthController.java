package com.connect.controller;

import java.security.Principal;
import java.util.Map;

import com.connect.dto.LoginUserDTO;
import com.connect.dto.OtpDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.model.User;
import com.connect.service.AuthService;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    // Handler method for handling the signup functionality.
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signupHandler(@RequestBody User user) {
        log.info("Signup request by {}", user.toString());
        authService.sendOTP(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("response", "OTP sent to your email"));
    }

    @PostMapping("/signup/verifyOTP")
    public ResponseEntity<Map<String, String>> verifyOTPHandler(@RequestBody OtpDTO otpDto) {
        log.info("OTP Verification Request");
        authService.createUser(otpDto.getOtp(), otpDto.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("response", "User created successfully"));
    }

    @GetMapping("/signup/resendOTP")
    public ResponseEntity<Map<String, String>> resendOTPHandler(@RequestParam("email") String email) {
        log.info("Resend OTP Request by the email: {}", email);
        authService.resendOTP(email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("response", "OTP sent successfully"));
    }

    // Handler method for handling the login functionality.
    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginUserDTO loginUser) throws Exception {
        log.info("Login Request by {}", loginUser.toString());
        var response = authService.handleLogin(loginUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutHandler(@RequestParam("username") String username) {
        if (username.isEmpty()) {
            log.error("No user found");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "User not found"));
        }
        if (authService.handleLogout(username) == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("response", "Something went wrong at the server"));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("response", "Logged out successfully"));

    }
}