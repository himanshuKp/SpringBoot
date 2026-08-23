package com.himanshu.cacheable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.himanshu.cacheable.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be defined here if needed

}
