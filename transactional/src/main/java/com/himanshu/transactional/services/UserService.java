package com.himanshu.transactional.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.himanshu.transactional.entities.User;
import com.himanshu.transactional.exceptions.UserNotFoundException;
import com.himanshu.transactional.records.UserResponse;
import com.himanshu.transactional.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // inheriting the readOnly = true from the class level, so no need to specify it here
    // hibernate disables dirty checking for read-only transactions, so no updates will be persisted
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional  // explicitly marking this method as transactional to allow updates
    public UserResponse updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(active);
        // dirty checking will automatically save the changes when the transaction commits
        return UserResponse.fromEntity(user); 
    }
}
