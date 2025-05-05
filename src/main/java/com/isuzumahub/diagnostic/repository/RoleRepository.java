package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String user);
}