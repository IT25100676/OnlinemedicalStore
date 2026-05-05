package com.medistore.service;

import com.medistore.model.Admin;
import com.medistore.model.Customer;
import com.medistore.model.User;
import com.medistore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * UserService — Business Logic for User Management
 * ============================================================
 *
 * Sits between Controller and Repository.
 * Handles:
 *   - Password hashing before saving
 *   - Login validation using polymorphic validateLogin()
 *   - Registration logic for Customer vs Admin
 *   - Search and filter delegation
 *
 * OOP Concepts:
 *   - Polymorphism: registerUser() accepts any User subtype (Customer or Admin)
 *   - Encapsulation: password hashing is hidden inside this service layer
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── REGISTER (CREATE) ────────────────────────────────────────────────────

    /**
     * Register a new Customer.
     * Hashes password before saving.
     */
    public Customer registerCustomer(String username, String email,
                                     String rawPassword, String address, String phone)
            throws IOException {
        String hashed = passwordEncoder.encode(rawPassword);
        Customer customer = new Customer(username, email, hashed, address, phone);
        userRepository.save(customer);
        return customer;
    }

    /**
     * Register a new Admin (only callable by existing super admins).
     */
    public Admin registerAdmin(String username, String email,
                               String rawPassword, String address,
                               String phone, String department)
            throws IOException {
        String hashed = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin(username, email, hashed, address, phone, department);
        userRepository.save(admin);
        return admin;
    }

    // ─── LOGIN (READ + Validate) ──────────────────────────────────────────────

    /**
     * Validates login using polymorphic validateLogin() on each User subtype.
     * Customer.validateLogin() vs Admin.validateLogin() behave differently.
     */
    public Optional<User> login(String email, String rawPassword) throws IOException {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Use Spring Security's password encoder for hash comparison
            if (passwordEncoder.matches(rawPassword, user.getPassword()) && user.isActive()) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    public List<User> getAllUsers() throws IOException {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) throws IOException {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) throws IOException {
        return userRepository.findByEmail(email);
    }

    public List<User> searchUsers(String query) throws IOException {
        return userRepository.searchUsers(query);
    }

    public List<User> getUsersByRole(String role) throws IOException {
        return userRepository.findByRole(role);
    }

    public long getTotalUsers() throws IOException {
        return userRepository.countAll();
    }

    public long getActiveUsers() throws IOException {
        return userRepository.countActive();
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    /**
     * Update user profile (email, address, phone).
     * Password update handled separately for security.
     */
    public User updateProfile(String id, String email, String address, String phone)
            throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setEmail(email);
        user.setAddress(address);
        user.setPhone(phone);
        return userRepository.update(user);
    }

    /**
     * Update password securely — hashes new password before saving.
     */
    public User updatePassword(String id, String newRawPassword) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setPassword(passwordEncoder.encode(newRawPassword));
        return userRepository.update(user);
    }

    /**
     * Toggle user active status (Admin operation).
     */
    public User setActiveStatus(String id, boolean active) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setActive(active);
        return userRepository.update(user);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    /**
     * Permanently remove a user account.
     */
    public void deleteUser(String id) throws IOException {
        userRepository.deleteById(id);
    }
}
