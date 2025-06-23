package com.connect.service;

import java.util.*;

import com.connect.dto.LoginUserDTO;
import com.connect.enums.UserRole;
import com.connect.enums.UserStatus;
import com.connect.exception.TimeoutException;
import com.connect.security.CustomUserDetails;
import com.connect.utils.EmailUtil;
import com.connect.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
@AllArgsConstructor
@Slf4j
public class AuthService {

    private UserRepository repository;

    private EmailService emailService;

    private RedisService redisService;

    private OTPService otpService;

    private AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    private EmailUtil emailUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void signupHandler(User user) {

        // Checking the User exists previously in the DB or not by Email.
        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    // If User Exist throwing an Exception that was further handled by the GlobalExceptionHandler.
                    throw new DuplicateResourceException("User already exists by email: " + u.getEmail());
                });

        // Checking the User exists previously in the DB or not by Username.
        repository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    // If User Exist throwing an Exception that was further handled by the GlobalExceptionHandler.
                    throw new DuplicateResourceException("User already exists by username: " + u.getUsername());
                });

        // If the User doesn't exist in the DB.
        // Sending an OTP to the User's email for verifying its profile.
        String generatedOTP = otpService.generateOTPForUser(user.getEmail());

        // Sending Email via Email Service class.
        emailService.sendEmail(
                user.getEmail(),
                "One Time Password",
                emailUtil.getBody().formatted(user.getUsername(), generatedOTP)
        );

        // Putting the User to the Redis Cache.
        redisService.cacheUserWithTTL(user);
    }

    public void resendOTP(String email) {

        // Fetching the Stored User Data from the persistent storage.
        // Checking the user is the same user in the UserStore.
        // If it's not throwing a Runtime Exception which was further handled by the ControllerAdvice.
        User storedUser = Optional.ofNullable((User) redisService.getUser(email))
                .orElseThrow(() -> new RuntimeException("Email doesn't exists, Bad Request!"));

        // Removing the Pre-cached OTP before creating the Another one if the otp exists.
        redisService.removeOTP(email);

        // Generating a new OTP and saving it into the redis cache.
        String generatedOTP = otpService.generateOTPForUser(email);

        // Resending the OTP to the User's email.
        emailService.sendEmail(
                storedUser.getEmail(),
                "One Time Password",
                emailUtil.getBody().formatted(storedUser.getUsername(), generatedOTP)
        );

    }

    public User createUser(String otp, String email) {

        // Before creating the User we need to verify the OTP is valid or not.
        // If the OTP is valid we proceed further else we throw an Exception.
        if (!otpService.verifyOTP(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Explicitly removing the cache otp if exists in the db.
        redisService.removeOTP(email);

        // Fetching the User from the temporary storage and storing it in the database.
        User savedUser = Optional.ofNullable((User) redisService.getUser(email))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setUserRole(List.of(UserRole.USER));
                    return user;
                })
                .orElseThrow(() -> new TimeoutException("User not found in the cache, Try again later"));


        User createdUser = repository.createUser(savedUser)
                .orElseThrow(() -> new UserCreationException("Something went wrong at the server! try again later."));

        // Explicitly removing the cached user.
        redisService.removeUser(email);

        // Returning the User if successfully created else throwing an exception.
        return createdUser;
    }

    public Map<String, Object> loginHandler(LoginUserDTO loginUser) throws Exception {

        // Spring security authentication for checking the authentication.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(customUserDetails.getUsername(), customUserDetails.getEmail());
        Date expiry = jwtUtil.getExpirationDate(token);

        // Changing the status of the User to Active.
        // Async method
        repository.updateUserStatus(customUserDetails.getUsername(), UserStatus.ACTIVE);

        return Map.of(
                "token", token,
                "expiresAt", expiry
        );
    }

    @CacheEvict(value = "userCache", allEntries = true)
    public User handleLogout(String username) {
        return repository.updateUserStatus(username, UserStatus.ACTIVE)
                .orElse(null);
    }

}