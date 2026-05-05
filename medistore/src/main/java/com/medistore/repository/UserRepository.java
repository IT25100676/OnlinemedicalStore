package com.medistore.repository;

import com.medistore.model.Admin;
import com.medistore.model.Customer;
import com.medistore.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ============================================================
 * UserRepository — File-Based CRUD Operations
 * ============================================================
 *
 * Implements all CRUD operations using a flat-file database (users.txt).
 * Each line in users.txt represents one user in pipe-delimited format:
 *   id|username|email|password|address|phone|role|active|createdAt
 *
 * CRUD Methods:
 *   CREATE  → save(User)
 *   READ    → findById(), findByEmail(), findAll(), searchUsers()
 *   UPDATE  → update(User)
 *   DELETE  → deleteById()
 *
 * OOP Concepts:
 *   - Encapsulation: file I/O logic is hidden behind clean method signatures
 *   - Polymorphism: fromFileLine() reconstructs the correct subclass
 *     (Customer or Admin) based on the role field
 */
@Repository
public class UserRepository {

    @Value("${app.storage.users-file:data/users.txt}")
    private String usersFilePath;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    /**
     * Saves a new user to users.txt.
     * Throws exception if email already exists (unique constraint).
     */
    public User save(User user) throws IOException {
        ensureFileExists();

        if (findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(usersFilePath), StandardOpenOption.APPEND)) {
            writer.write(user.toFileString());
            writer.newLine();
        }
        return user;
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    /**
     * Returns all users from the file.
     * Uses polymorphism: fromFileLine() creates the right subtype.
     */
    public List<User> findAll() throws IOException {
        ensureFileExists();
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    fromFileLine(line).ifPresent(users::add);
                }
            }
        }
        return users;
    }

    /**
     * Find a user by their unique ID.
     */
    public Optional<User> findById(String id) throws IOException {
        return findAll().stream()
                .filter(u -> u.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    /**
     * Find a user by email address (used for login).
     */
    public Optional<User> findByEmail(String email) throws IOException {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Search users by username, email, or ID (Admin user list).
     */
    public List<User> searchUsers(String query) throws IOException {
        return findAll().stream()
                .filter(u -> u.matchesSearch(query))
                .collect(Collectors.toList());
    }

    /**
     * Find users filtered by role (CUSTOMER or ADMIN).
     */
    public List<User> findByRole(String role) throws IOException {
        return findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    /**
     * Count total registered users.
     */
    public long countAll() throws IOException {
        return findAll().size();
    }

    /**
     * Count active users.
     */
    public long countActive() throws IOException {
        return findAll().stream().filter(User::isActive).count();
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    /**
     * Updates an existing user's record in the file.
     * Strategy: read all → replace matching line → write all back.
     */
    public User update(User updatedUser) throws IOException {
        List<User> allUsers = findAll();

        boolean found = false;
        List<String> lines = new ArrayList<>();

        for (User u : allUsers) {
            if (u.getId().equals(updatedUser.getId())) {
                lines.add(updatedUser.toFileString());
                found = true;
            } else {
                lines.add(u.toFileString());
            }
        }

        if (!found) {
            throw new IllegalArgumentException("User not found: " + updatedUser.getId());
        }

        writeAllLines(lines);
        return updatedUser;
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    /**
     * Removes a user from the file by their ID.
     */
    public void deleteById(String id) throws IOException {
        List<User> allUsers = findAll();
        List<String> lines = allUsers.stream()
                .filter(u -> !u.getId().equals(id))
                .map(User::toFileString)
                .collect(Collectors.toList());
        writeAllLines(lines);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private void writeAllLines(List<String> lines) throws IOException {
        Files.write(Paths.get(usersFilePath), lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void ensureFileExists() throws IOException {
        Path path = Paths.get(usersFilePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent() != null ? path.getParent() : Paths.get("."));
            Files.createFile(path);
        }
    }

    /**
     * Parses a line from users.txt and reconstructs the correct User subclass.
     * OOP Polymorphism: returns Customer or Admin depending on the role field.
     *
     * Format: id|username|email|password|address|phone|role|active|createdAt
     */
    private Optional<User> fromFileLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 9) return Optional.empty();

            String id       = parts[0];
            String username = parts[1];
            String email    = parts[2];
            String password = parts[3];
            String address  = parts[4];
            String phone    = parts[5];
            String role     = parts[6];
            boolean active  = Boolean.parseBoolean(parts[7]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[8]);

            User user;
            // ─ Polymorphism: create the right subclass based on role ─
            if ("ADMIN".equalsIgnoreCase(role)) {
                user = new Admin(username, email, password, address, phone, "General");
            } else {
                user = new Customer(username, email, password, address, phone);
            }

            user.setId(id);
            user.setActive(active);
            user.setCreatedAt(createdAt);
            return Optional.of(user);

        } catch (Exception e) {
            // Skip malformed lines gracefully
            return Optional.empty();
        }
    }
}
