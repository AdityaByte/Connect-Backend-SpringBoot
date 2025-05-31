package com.connect.service;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.connect.model.User;
import com.connect.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean createUser(User user) {
        // Before creating a user we need to check that the user already exists or not
        // or we need to check that the email or username already exists or not.
        Optional<User> userByEmail = repository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            return false;
        }
        // Else we need to check by the username.
        Optional<User> userByUsername = repository.findByUsername(user.getUsername());
        if (userByUsername.isPresent()) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.createUser(user);
    }

    public boolean checkUser(User user) {
        Optional<User> fetchedUser = repository.findByEmail(user.getEmail());
        if (fetchedUser.isPresent()) {
            if (passwordEncoder.matches(user.getPassword(), fetchedUser.get().getPassword())) {
                return true;
            }
        }
        return false;
    }

}