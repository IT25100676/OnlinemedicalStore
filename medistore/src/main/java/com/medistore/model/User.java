package com.medistore.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ============================================================
 * OOP CONCEPTS DEMONSTRATED:
 * ============================================================
 *
 * 1. ENCAPSULATION:
 *    - All fields are private (data hiding)
 *    - Access controlled through public getters/setters
 *    - Validation logic encapsulated inside the class
 *
 * 2. INHERITANCE (Abstract Base Class):
 *    - Customer and Admin classes inherit from this User class
 *    - Shared attributes (id, username, email, password) defined here
 *    - Subclasses extend behavior without duplicating code
 *
 * 3. ABSTRACTION:
 *    - Abstract method getRole() forces subclasses to define their role
 *    - Abstract method validateLogin() enforces different login logic
 *    - Common behavior (getId, getEmail) inherited automatically
 *
 * 4. POLYMORPHISM:
 *    - getRole() returns different values depending on subclass type
 *    - validateLogin() behaves differently for Customer vs Admin
 */
public abstract class User {

    // ─── PRIVATE FIELDS (Encapsulation) ─────────────────────────────────────
    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // ─── CONSTRUCTORS ────────────────────────────────────────────────────────

    protected User() {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    protected User(String username, String email, String password, String address, String phone) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    // ─── ABSTRACT METHODS (Abstraction + Polymorphism) ───────────────────────

    /**
     * Each subclass must declare its own role.
     * Polymorphism: Customer returns "CUSTOMER", Admin returns "ADMIN"
     */
    public abstract String getRole();

    /**
     * Each subclass can implement its own login validation logic.
     * Polymorphism: Admin may require 2FA, Customer uses simple password check
     */
    public abstract boolean validateLogin(String rawPassword);

    /**
     * Returns a formatted display name for each user type.
     * Overridden in each subclass for polymorphic display.
     */
    public abstract String getDisplayName();

    // ─── COMMON BEHAVIOUR (Shared by all subclasses) ─────────────────────────

    /**
     * Checks if this user matches a search query.
     * Works for all subclasses without overriding.
     */
    public boolean matchesSearch(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase();
        return username.toLowerCase().contains(q)
                || email.toLowerCase().contains(q)
                || id.toLowerCase().contains(q);
    }

    /**
     * Serializes user to a single line for file storage (users.txt)
     * Format: id|username|email|password|address|phone|role|active|createdAt
     */
    public String toFileString() {
        return String.join("|",
                id,
                username,
                email,
                password,
                address,
                phone,
                getRole(),
                String.valueOf(active),
                createdAt.toString()
        );
    }

    // ─── GETTERS & SETTERS (Encapsulation) ───────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', role='" + getRole() + "'}";
    }
}
