package com.himanshu.cacheable.records;

import java.io.Serializable;

import com.himanshu.cacheable.entities.User;

public record UserResponse(
    Long id,
    String name,
    String email,
    boolean active
) implements Serializable {
    
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.isActive()
        );
    }
}
