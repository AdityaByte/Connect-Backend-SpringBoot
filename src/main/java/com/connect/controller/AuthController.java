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
        System.out.println("Signup request :?");
        System.out.println(user.toString());
        if (userService.createUser(user)) {
            return new ResponseEntity<>(Map.of("response", "User Created"), HttpStatus.CREATED);
        }
        // Else something went wrong on the server so we have to send a response to the client.
        return new ResponseEntity<>(Map.of("response", "ERROR: Credenatials already exists try different one."), HttpStatus.BAD_REQUEST);
    }

    // Handler method for handling the login functionality.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginHandler(@RequestBody User user ) {
        System.out.println("Login request :?");
        System.out.println(user.toString());
        if (userService.checkUser(user)) {
            return new ResponseEntity<>(Map.of("response", "Login successful"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("response", "Login failed"), HttpStatus.BAD_REQUEST);
    }

}