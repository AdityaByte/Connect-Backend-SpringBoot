package com.connect.pojo;

import com.connect.model.User;

public class UserDetails {
    private User user;

    public UserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}