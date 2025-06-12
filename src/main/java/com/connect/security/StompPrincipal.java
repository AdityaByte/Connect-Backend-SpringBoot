package com.connect.security;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final String name; // This field holds the actual username

    public StompPrincipal(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Principal name cannot be null or empty.");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StompPrincipal that = (StompPrincipal) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "StompPrincipal{name='" + name + "'}";
    }
}