package com.connect.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.connect.model.User;
import com.connect.repository.UserRepository;
import com.connect.exception.DuplicateResourceException;
import com.connect.exception.InvalidUserException;
import com.connect.exception.UserCreationException;;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EmailService emailService;

    private String otp;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Map<String, User> userStore = new HashMap<>();

    public void sendOTP(User user) {
        // Before creating a user we need to check that the user already exists or not
        // or we need to check that the email or username already exists or not.
        Optional<User> userByEmail = repository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            throw new DuplicateResourceException("User already exists by email: " + userByEmail.get().getEmail());
        }
        // Else we need to check by the username.
        Optional<User> userByUsername = repository.findByUsername(user.getUsername());
        if (userByUsername.isPresent()) {
            throw new DuplicateResourceException(
                    "User already exists by username:" + userByUsername.get().getUsername());
        }

        // Here we need to send the otp to the client.
        String generatedOTP = OTPService.generateOTPForUser(user.getEmail());

        // If this function doesn't send the email it will generate an error which was
        // further catched by the ControllerAdvice.
        emailService.sendEmail(user.getEmail(), "One Time Password", String
                .format("Hey %s\nYour OTP for signup is %s\nRegards from Connect", user.getUsername(), generatedOTP));

        // Here we need to save the user details in the user store.
        userStore.put(user.getEmail(), user); // Since we have set this userdetails and when the user has completed the
                                             // signup we will free the memory.
    }

    public void resendOTP(String email) {
        User storedUser = userStore.get(email);
        if (storedUser == null) {
            throw new RuntimeException("Email Doesn't exists. Bad Request!");
        }
        // Else we need to resend the OTP.
        String generatedOTP = OTPService.generateOTPForUser(email);

        emailService.sendEmail(storedUser.getEmail(), "One Time Password", String
        .format("Hey %s\nYour OTP for signup is %s\nRegards from Connect", storedUser.getUsername(), generatedOTP));
    }

    public User createUser(String otp, String email) {
        // Firstly we need to verify the otp that the otp is correct or not.
        // Before that we need to check that the otp is associated with the user or not.

        if (!OTPService.verifyOTP(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Else here we need to create the user ok.

        User savedUser = userStore.get(email);

        savedUser.setPassword(passwordEncoder.encode(savedUser.getPassword()));

        Optional<User> createdUser = repository.createUser(savedUser);

        userStore.remove(email); // Have to remove it after all.

        return createdUser
        .orElseThrow(() -> new UserCreationException("Something went wrong at the server! try again later."));
    }

    public User checkUser(User user) {
        Optional<User> fetchedUser = repository.findByEmail(user.getEmail());
        if (fetchedUser.isEmpty()) {
            throw new InvalidUserException("User doesn't exits of the email: " + user.getEmail());
        }
        // Else the user exists in this case.
        if (!passwordEncoder.matches(user.getPassword(), fetchedUser.get().getPassword())) {
            throw new InvalidUserException("Invalid Credentials");
        }

        return fetchedUser.get();
    }

}