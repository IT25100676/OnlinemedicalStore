package com.medistore.controller;

import com.medistore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles Login, Registration, and Dashboard routing.
 *
 * Pages served:
 *   GET  /login      → Login page
 *   GET  /register   → Registration page
 *   POST /register   → Process registration form
 *   GET  /dashboard  → Redirect based on role
 *   GET  /           → Redirect to login
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ─── HOME ────────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    // ─── DASHBOARD REDIRECT ──────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/users";
        }
        return "redirect:/profile";
    }

    // ─── REGISTRATION ────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  @RequestParam String address,
                                  @RequestParam String phone,
                                  RedirectAttributes redirectAttrs) {
        try {
            // Validate password match
            if (!password.equals(confirmPassword)) {
                redirectAttrs.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/register";
            }

            userService.registerCustomer(username, email, password, address, phone);
            redirectAttrs.addFlashAttribute("success",
                    "Account created successfully! Please log in.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Registration failed. Please try again.");
            return "redirect:/register";
        }
    }
}
