package com.connect.service;

import com.connect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    @Autowired
    private JwtUtil jwtUtil;

    public String extractUsername(String token) {
        try {
            var claims = jwtUtil.extractClaims(token);
            String username = claims.getSubject();
            System.out.println("extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
