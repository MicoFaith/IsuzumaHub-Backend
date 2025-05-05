package com.isuzumahub.diagnostic.config;

import com.isuzumahub.diagnostic.model.Admin;
import com.isuzumahub.diagnostic.model.Employee;
import com.isuzumahub.diagnostic.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final String email;
    private final String password;
    private final String role;
    private final String fullName;

    public UserPrincipal(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = String.valueOf(user.getRoles());
        this.fullName = user.getFullName(); // Changed from getFullName() if not defined in User
    }

    public UserPrincipal(Admin admin) {
        this.email = admin.getEmail();
        this.password = admin.getPassword();
        this.role = "ADMIN";
        this.fullName = admin.getFullName();
    }

    public UserPrincipal(Employee employee) {
        this.email = employee.getEmail();
        this.password = employee.getPassword();
        this.role = "EMPLOYEE";
        this.fullName = employee.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return fullName;
    }
}
