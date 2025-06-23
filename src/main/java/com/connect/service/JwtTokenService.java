package com.connect.service;

import com.connect.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class JwtTokenService {

    @Autowired
    private JwtUtil jwtUtil;

    public String extractUsername(String token) {
        try {
            return Optional.ofNullable(jwtUtil.extractClaims(token))
                    .map(Claims::getSubject)
                    .orElseGet(() -> {
                        log.error("Failed to extract the username: subject is null");
                        return null;
                    });
        } catch (Exception e) {
            log.error("Failed to extract the username from the token");
            return null;
        }
    }
}