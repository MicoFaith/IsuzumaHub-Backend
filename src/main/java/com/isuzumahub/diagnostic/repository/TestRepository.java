package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findByTestName(String testName);
}