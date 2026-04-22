package com.medistore.controller;

import com.medistore.model.User;
import com.medistore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin Controller — User List Management (Admin-only pages).
 *
 * Pages:
 *   GET  /admin/users              → User list with search
 *   GET  /admin/users/{id}/edit    → Edit a user
 *   POST /admin/users/{id}/toggle  → Enable/disable user
 *   POST /admin/users/{id}/delete  → Delete user
 *   GET  /admin/users/new          → Create new user form
 *   POST /admin/users/create       → Save new user
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // ─── USER LIST ───────────────────────────────────────────────────────────

    @GetMapping("/users")
    public String userList(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "role", required = false) String role,
                           Model model) {
        try {
            List<User> users;
            if (search != null && !search.isBlank()) {
                users = userService.searchUsers(search);
            } else if (role != null && !role.isBlank()) {
                users = userService.getUsersByRole(role);
            } else {
                users = userService.getAllUsers();
            }

            model.addAttribute("users", users);
            model.addAttribute("search", search);
            model.addAttribute("selectedRole", role);
            model.addAttribute("totalUsers", userService.getTotalUsers());
            model.addAttribute("activeUsers", userService.getActiveUsers());
            model.addAttribute("totalCustomers", userService.getUsersByRole("CUSTOMER").size());
            model.addAttribute("totalAdmins", userService.getUsersByRole("ADMIN").size());

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load users: " + e.getMessage());
        }
        return "admin/user-list";
    }

    // ─── CREATE NEW USER ─────────────────────────────────────────────────────

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        return "admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String address,
                             @RequestParam String phone,
                             @RequestParam String role,
                             @RequestParam(required = false, defaultValue = "General") String department,
                             RedirectAttributes redirectAttrs) {
        try {
            if ("ADMIN".equalsIgnoreCase(role)) {
                userService.registerAdmin(username, email, password, address, phone, department);
            } else {
                userService.registerCustomer(username, email, password, address, phone);
            }
            redirectAttrs.addFlashAttribute("success",
                    "User '" + username + "' created successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ─── EDIT USER ───────────────────────────────────────────────────────────

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable String id, Model model) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            model.addAttribute("user", user);
            return "admin/user-edit";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable String id,
                             @RequestParam String email,
                             @RequestParam String address,
                             @RequestParam String phone,
                             RedirectAttributes redirectAttrs) {
        try {
            userService.updateProfile(id, email, address, phone);
            redirectAttrs.addFlashAttribute("success", "User updated successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ─── TOGGLE ACTIVE STATUS ────────────────────────────────────────────────

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable String id, RedirectAttributes redirectAttrs) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userService.setActiveStatus(id, !user.isActive());
            String status = !user.isActive() ? "activated" : "deactivated";
            redirectAttrs.addFlashAttribute("success",
                    "User '" + user.getUsername() + "' has been " + status + ".");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Action failed: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ─── DELETE USER ─────────────────────────────────────────────────────────

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttrs) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userService.deleteUser(id);
            redirectAttrs.addFlashAttribute("success",
                    "User '" + user.getUsername() + "' deleted successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
