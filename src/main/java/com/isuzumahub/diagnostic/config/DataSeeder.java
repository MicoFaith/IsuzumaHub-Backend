package com.isuzumahub.diagnostic.config;

import com.isuzumahub.diagnostic.model.Role;
import com.isuzumahub.diagnostic.model.Test;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.RoleRepository;
import com.isuzumahub.diagnostic.repository.TestRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DataSeeder(TestRepository testRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Create and save a sample Role
        Role role = new Role();
        role.setName("ROLE_USER");
        role = roleRepository.save(role);  // Use RoleRepository to save Role

        // Create a sample user and assign the role
        User user = new User();
        user.setEmail("mico@example.com");
        user.setFullName("mico");
        user.setPassword("mico123");
        user.setRoles(role);  // Assign the saved Role
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);  // Save user with the role

        // Now create a Test object and assign the user
        Test test = new Test();
        test.setTestName("Blood Test");
        test.setDescription("Checks for cholesterol levels");
        test.setCategory("Blood Work");
        test.setPrice(50.0);
        test.setStatus("AVAILABLE");
        test.setUser(user);
        test.setCreatedAt(LocalDateTime.now());
        test.setUpdatedAt(LocalDateTime.now());

        testRepository.save(test);
    }
}