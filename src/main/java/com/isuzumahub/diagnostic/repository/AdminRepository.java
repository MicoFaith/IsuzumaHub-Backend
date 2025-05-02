package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
}