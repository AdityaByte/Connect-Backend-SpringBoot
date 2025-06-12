package com.connect.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.connect.dto.LoginUserDTO;
import com.connect.security.CustomUserDetails;
import com.connect.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.connect.model.User;
import com.connect.repository.UserRepository;
import com.connect.exception.DuplicateResourceException;
import com.connect.exception.UserCreationException;;

// Service
// Handles the main business logic of Authentication

@Service
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Map<String, User> userStore = new HashMap<>();

    public void sendOTP(User user) {
        // Checking the User exists previously in the DB or not by Email.
        Optional<User> userByEmail = repository.findByEmail(user.getEmail());
        // If User Exist throwing an Exception that was further handled by the GlobalExceptionHandler.
        if (userByEmail.isPresent()) {
            throw new DuplicateResourceException("User already exists by email: " + userByEmail.get().getEmail());
        }
        // Checking the User exists previously in the DB or not by Email.
        Optional<User> userByUsername = repository.findByUsername(user.getUsername());
        // If User Exist throwing an Exception that was further handled by the GlobalExceptionHandler.
        if (userByUsername.isPresent()) {
            throw new DuplicateResourceException(
                    "User already exists by username:" + userByUsername.get().getUsername());
        }

        // If the User doesn't exist in the DB.
        // Sending an OTP to the User's email for verifying its profile.
        String generatedOTP = OTPService.generateOTPForUser(user.getEmail());

        // Sending Email via Email Service class.
        emailService.sendEmail(user.getEmail(), "One Time Password", String
                .format("Hey %s\nYour OTP for signup is %s\nRegards from Connect", user.getUsername(), generatedOTP));

        // After successfully Sending the Email
        // For Saving the State of the User for further verification (OTP verification) saving its state
        // in a persistent storage (in memory).
        userStore.put(user.getEmail(), user);
    }

    public void resendOTP(String email) {
        // Fetching the Stored User Data from the persistent storage.
        User storedUser = userStore.get(email);
        // Checking the user is the same user in the UserStore.
        // If it's not throwing a Runtime Exception which was further handled by the ControllerAdvice.
        if (storedUser == null) {
            throw new RuntimeException("Email Doesn't exists. Bad Request!");
        }

        String generatedOTP = OTPService.generateOTPForUser(email);
        // Resending the OTP to the User's email.
        emailService.sendEmail(storedUser.getEmail(), "One Time Password", String
        .format("Hey %s\nYour OTP for signup is %s\nRegards from Connect", storedUser.getUsername(), generatedOTP));
    }

    public User createUser(String otp, String email) {
        // Before creating the User we need to verify the OTP is valid or not.
        // If the OTP is valid we proceed further else we throw an Exception.
        if (!OTPService.verifyOTP(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Fetching the User from the temporary storage and storing it in the database.
        User savedUser = userStore.get(email);
        // Before saving it in the DB encoding the password with BCrypt encryption.
        savedUser.setPassword(passwordEncoder.encode(savedUser.getPassword()));

        Optional<User> createdUser = repository.createUser(savedUser);
        userStore.remove(email); // Have to remove it after all.

        // Returning the User if successfully created else throwing an exception.
        return createdUser
        .orElseThrow(() -> new UserCreationException("Something went wrong at the server! try again later."));
    }

    public Map<String, Object> handleLogin(LoginUserDTO loginUser) throws Exception {
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

        return response;
    }

}