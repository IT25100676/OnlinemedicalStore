package com.medistore.controller;

import com.medistore.model.User;
import com.medistore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles the Profile Page (view + edit) for logged-in users.
 *
 * Pages:
 *   GET  /profile         → View profile
 *   POST /profile/update  → Update email, address, phone
 *   POST /profile/password→ Update password
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String profilePage(Authentication auth, Model model) {
        try {
            String email = auth.getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            model.addAttribute("user", user);
            return "user/profile";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/update")
    public String updateProfile(Authentication auth,
                                @RequestParam String email,
                                @RequestParam String address,
                                @RequestParam String phone,
                                RedirectAttributes redirectAttrs) {
        try {
            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            userService.updateProfile(currentUser.getId(), email, address, phone);
            redirectAttrs.addFlashAttribute("success", "Profile updated successfully!");

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String updatePassword(Authentication auth,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttrs) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttrs.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/profile";
            }

            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            userService.updatePassword(currentUser.getId(), newPassword);
            redirectAttrs.addFlashAttribute("success", "Password updated successfully!");

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Password update failed.");
        }
        return "redirect:/profile";
    }
}
