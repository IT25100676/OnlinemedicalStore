package com.medistore.model;

/**
 * ============================================================
 * OOP CONCEPTS DEMONSTRATED:
 * ============================================================
 *
 * 1. INHERITANCE:
 *    - Customer extends User → inherits id, username, email, password,
 *      address, phone, createdAt, active fields and all common methods
 *    - Does NOT need to redeclare those fields (code reuse)
 *
 * 2. POLYMORPHISM (Method Overriding):
 *    - getRole()       → returns "CUSTOMER" (overrides abstract method)
 *    - validateLogin() → simple email+password match (no 2FA needed)
 *    - getDisplayName()→ returns "Customer: <username>"
 *
 * 3. ENCAPSULATION:
 *    - Customer-specific fields (loyaltyPoints, prescriptionCount)
 *      are private and accessed only through getters/setters
 */
public class Customer extends User {

    // ─── Customer-specific private fields (Encapsulation) ────────────────────
    private int loyaltyPoints;
    private int prescriptionCount;
    private String membershipTier; // BRONZE, SILVER, GOLD

    // ─── CONSTRUCTORS ────────────────────────────────────────────────────────

    public Customer() {
        super();
        this.loyaltyPoints = 0;
        this.prescriptionCount = 0;
        this.membershipTier = "BRONZE";
    }

    public Customer(String username, String email, String password, String address, String phone) {
        super(username, email, password, address, phone);
        this.loyaltyPoints = 0;
        this.prescriptionCount = 0;
        this.membershipTier = "BRONZE";
    }

    // ─── POLYMORPHISM: Overriding abstract methods ────────────────────────────

    @Override
    public String getRole() {

        return "CUSTOMER";
    }

    /**
     * Customer login: simple email + password comparison.
     * Polymorphism: this is different from Admin's validateLogin()
     */
    @Override
    public boolean validateLogin(String rawPassword) {
        return this.getPassword() != null && this.getPassword().equals(rawPassword);
    }

    @Override
    public String getDisplayName() {
        return "Customer: " + getUsername();
    }

    // ─── Customer-specific business logic ────────────────────────────────────

    /**
     * Add loyalty points after a purchase.
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateMembershipTier();
    }

    /**
     * Automatically upgrade membership tier based on loyalty points.
     * Encapsulates business rule within the model.
     */
    private void updateMembershipTier() {
        if (loyaltyPoints >= 1000) {
            this.membershipTier = "GOLD";
        } else if (loyaltyPoints >= 500) {
            this.membershipTier = "SILVER";
        } else {
            this.membershipTier = "BRONZE";
        }
    }

    // ─── GETTERS & SETTERS (Encapsulation) ───────────────────────────────────

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        updateMembershipTier();
    }

    public int getPrescriptionCount() { return prescriptionCount; }
    public void setPrescriptionCount(int prescriptionCount) {
        this.prescriptionCount = prescriptionCount;
    }

    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }
}
