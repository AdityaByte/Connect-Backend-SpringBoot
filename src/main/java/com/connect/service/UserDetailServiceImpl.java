package com.connect.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.connect.model.User;
import com.connect.repository.UserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Email not found: " + email);
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getEmail())
                .password(user.get().getPassword())
                .build();
        return userDetails;
    }

}