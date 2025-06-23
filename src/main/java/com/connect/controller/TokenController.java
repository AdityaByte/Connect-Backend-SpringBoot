package com.connect.controller;

import com.connect.service.JwtTokenService;
import com.connect.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

// Controller for handling the Verify Token Request

@RestController
@RequestMapping("/api")
@Slf4j
public class TokenController {

    @Autowired
    private JwtTokenService tokenService;

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Request received for token verification");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing Authorization Header");
        }

        return Optional.ofNullable(tokenService.extractUsername(authHeader.substring(7)))
                .map(username -> {
                    return ResponseEntity.ok("Authorized");
                })
                .orElseGet(() -> {
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body("Invalid Token");
                });
    }
}