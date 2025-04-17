package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    Optional<User> findByEmailVerificationToken(String token);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
} 