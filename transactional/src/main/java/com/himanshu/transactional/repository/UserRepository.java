package com.himanshu.transactional.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.himanshu.transactional.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
