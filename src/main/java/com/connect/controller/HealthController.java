package com.connect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Health Controller

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Health is ok");
    }
}
