package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countEmployees(@Param("roleName") String roleName);

    @Override
    Optional<User> findById(Long aLong);

    Optional<User>  findByEmail(String email);
}