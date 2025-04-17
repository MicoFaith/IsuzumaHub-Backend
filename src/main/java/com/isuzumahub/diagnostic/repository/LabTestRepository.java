package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    Optional<LabTest> findByName(String name);
    List<LabTest> findByCategory(String category);
    boolean existsByName(String name);
    List<LabTest> findByActiveTrue();
} 