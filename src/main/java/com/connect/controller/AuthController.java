package com.connect.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.connect.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.model.OTP;
import com.connect.model.User;
import com.connect.pojo.LoginRequest;
import com.connect.service.UserService;
import com.connect.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<?> loginHandler(@RequestBody LoginRequest loginRequest) throws Exception {
        // Temporary logging statements.
        System.out.println("Request Recieved");
        System.out.println(loginRequest.toString());

        // Spring security authentication for checking the authentication.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

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