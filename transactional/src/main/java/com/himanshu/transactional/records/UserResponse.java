package com.himanshu.transactional.records;

import com.himanshu.transactional.entities.User;

public record UserResponse(Long id, String name, String email, boolean active) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
        );
    }
}