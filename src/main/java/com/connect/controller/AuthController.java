package com.connect.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.connect.dto.LoginUserDTO;
import com.connect.dto.OtpDTO;
import com.connect.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.model.User;
import com.connect.service.UserService;
import com.connect.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Handler method for handling the signup functionality.
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signupHandler(@RequestBody User user) {
        log.info("Signup request by ", user.toString());
        userService.sendOTP(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("response", "OTP sent to your email"));
    }

    @PostMapping("/signup/verifyOTP")
    public ResponseEntity<Map<String, String>> verifyOTPHandler(@RequestBody OtpDTO otpDto) {
        userService.createUser(otpDto.getOtp(), otpDto.getEmail());
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
    public ResponseEntity<?> loginHandler(@RequestBody LoginUserDTO loginUser) throws Exception {
        // Temporary logging statements.
        log.info("Login Request by ", loginUser.toString());

        // Spring security authentication for checking the authentication.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(customUserDetails.getUsername(), customUserDetails.getEmail());
        Date expiry = jwtUtil.getExpirationDate(token);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiresAt", expiry);

        return ResponseEntity.ok(response);
    }
}