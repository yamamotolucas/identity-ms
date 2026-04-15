package br.com.identity_ms.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum UserRole {
    ADMIN,
    MANAGER,
    BARBER,
    CUSTOMER;

    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null) return null;
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role: " + value));
    }
}