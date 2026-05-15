package com.medistore.config;

import com.medistore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a default Admin account on first run so you can log in immediately.
 * Default credentials: admin@medistore.com / Admin@123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (userService.getAllUsers().isEmpty()) {
                userService.registerAdmin(
                    "SuperAdmin",
                    "admin@medistore.com",
                    "Admin@123",
                    "MediStore HQ, Colombo, Sri Lanka",
                    "0771234567",
                    "IT Administration"
                );
                System.out.println("==============================================");
                System.out.println("  Default admin created:");
                System.out.println("  Email:    admin@medistore.com");
                System.out.println("  Password: Admin@123");
                System.out.println("==============================================");
            }
        } catch (Exception e) {
            // Already exists, skip
        }

    }

}
