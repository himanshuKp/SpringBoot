package com.himanshu.cacheable.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.himanshu.cacheable.entities.User;
import com.himanshu.cacheable.exceptions.UserNotFoundException;
import com.himanshu.cacheable.records.UserResponse;
import com.himanshu.cacheable.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Cacheable annotation is used to cache the result of the method based on the user id. If the user is not found, it throws a UserNotFoundException.
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public UserResponse getUserById(Long id) {
        System.out.println("Fetching user from database for id: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.fromEntity(user);
    }

    // Transactional annotation is used to indicate that the method should be executed within a transaction. 
    // CachePut annotation is used to update the cache with the new user status after updating it in the database. 
    // If the user is not found, it throws a UserNotFoundException.
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUserStatus(Long id, boolean status) {
        System.out.println("Updating user status in database for id: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(status);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        System.out.println("Deleting user from database for id: " + id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
