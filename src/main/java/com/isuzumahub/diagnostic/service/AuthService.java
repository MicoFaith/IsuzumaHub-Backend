package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.Role;
import com.isuzumahub.diagnostic.model.User;
import com.isuzumahub.diagnostic.repository.AdminRepository;
import com.isuzumahub.diagnostic.repository.EmployeeRepository;
import com.isuzumahub.diagnostic.repository.RoleRepository;
import com.isuzumahub.diagnostic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticate(String email, String password) {
        Object user = findUserByEmail(email);
        if (user == null) return false;
        String storedPassword = user instanceof User ? ((User) user).getPassword() :
                user instanceof Admin ? ((Admin) user).getPassword() : ((Employee) user).getPassword();
        return passwordEncoder.matches(password, storedPassword);
    }

    public UserDetails findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        if (user != null) return user;
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) return admin;
        Employee employee = employeeRepository.findByEmail(email).orElse(null);
        return employee;
    }

//    public void saveUser(User user) {
//        userRepository.save(user);
//    }
    public void saveUser(User user) {
        // Assign default role if not set
        if (user.getRoles() == null) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("USER");
                        return roleRepository.save(newRole);
                    });
            user.setRoles(userRole);
        }
        userRepository.save(user);
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public boolean checkUser(String email) {

       if(userRepository.findByEmail(email) != null) {
           return false;
       } else if (adminRepository.findByEmail(email) != null) {
           return false;

       } else if (employeeRepository.findByEmail(email) != null) {
        return false;
       }else{
           return true;
       }
    }
}