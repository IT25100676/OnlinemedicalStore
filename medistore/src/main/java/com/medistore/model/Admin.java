package com.medistore.model;

/**
 * ============================================================
 * OOP CONCEPTS DEMONSTRATED:
 * ============================================================
 *
 * 1. INHERITANCE:
 *    - Admin extends User → inherits all common User fields/methods
 *    - Adds admin-specific fields: adminLevel, department, securityPin
 *
 * 2. POLYMORPHISM (Method Overriding):
 *    - getRole()       → returns "ADMIN"
 *    - validateLogin() → stricter validation (checks if admin is active
 *                        AND password matches)
 *    - getDisplayName()→ returns "Admin: <username> [<department>]"
 *
 * 3. ENCAPSULATION:
 *    - securityPin is private and never exposed in toString()
 *    - adminLevel controls what the admin can access
 */
public class Admin extends User {

    // ─── Admin-specific private fields (Encapsulation) ───────────────────────
    private int adminLevel;       // 1 = Super Admin, 2 = Manager, 3 = Staff
    private String department;
    private String securityPin;   // Extra security layer for Admin
    private int actionsPerformed; // Audit count

    // ─── CONSTRUCTORS ────────────────────────────────────────────────────────

    public Admin() {
        super();
        this.adminLevel = 2;
        this.department = "General";
        this.actionsPerformed = 0;
    }

    public Admin(String username, String email, String password,
                 String address, String phone, String department) {
        super(username, email, password, address, phone);
        this.adminLevel = 2;
        this.department = department;
        this.actionsPerformed = 0;
    }

    // ─── POLYMORPHISM: Overriding abstract methods ────────────────────────────

    @Override
    public String getRole() {
        return "ADMIN";
    }

    /**
     * Admin login: stricter than Customer.
     * Checks: account must be active + password must match.
     * Polymorphism: same method name, different behaviour from Customer.
     */
    @Override
    public boolean validateLogin(String rawPassword) {
        if (!this.isActive()) {
            return false; // Disabled admin accounts cannot login
        }
        return this.getPassword() != null && this.getPassword().equals(rawPassword);
    }

    @Override
    public String getDisplayName() {
        return "Admin: " + getUsername() + " [" + department + "]";
    }

    // ─── Admin-specific business logic ───────────────────────────────────────

    public void recordAction() {
        this.actionsPerformed++;
    }

    public boolean isSuperAdmin() {
        return this.adminLevel == 1;
    }

    // ─── GETTERS & SETTERS (Encapsulation) ───────────────────────────────────

    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSecurityPin() { return securityPin; }
    public void setSecurityPin(String securityPin) { this.securityPin = securityPin; }

    public int getActionsPerformed() { return actionsPerformed; }
    public void setActionsPerformed(int actionsPerformed) {
        this.actionsPerformed = actionsPerformed;
    }

    /** Never expose security pin in toString – Encapsulation */
    @Override
    public String toString() {
        return "Admin{id='" + getId() + "', username='" + getUsername()
                + "', department='" + department + "', level=" + adminLevel + "}";
    }
}
