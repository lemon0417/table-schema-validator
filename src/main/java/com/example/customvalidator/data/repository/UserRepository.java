package com.example.customvalidator.data.repository;

import com.example.customvalidator.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
