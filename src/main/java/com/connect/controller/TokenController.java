package com.connect.controller;

import com.connect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller for handling the Verify Token Request

@RestController
@RequestMapping("/api")
public class TokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {

            System.out.println("Request recieved for token verification");
            if (authHeader == null || !authHeader.startsWith("Bearer")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Missing Authorization Header");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractClaims(token).getSubject();


           if (jwtUtil.isTokenValid(token, username)) {
               System.out.println("Token is valid");
               return ResponseEntity.ok("Token is valid");
           } else {
               System.out.println("Token is invalid");
               return ResponseEntity
                       .status(HttpStatus.UNAUTHORIZED)
                       .body("Token is invalid");
           }
        }
        catch (Exception e) {
            System.out.println("[ERROR] Token Validation Failed: "+ e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Token Validation Failed");
        }
    }
}