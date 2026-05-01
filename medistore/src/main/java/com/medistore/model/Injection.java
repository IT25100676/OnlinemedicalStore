package com.medistore.model;

import java.time.LocalDate;

public class Injection extends Medicine {
    private int dosageMg;
    private boolean requiresPrescription;

    public Injection(String medicineId, String name, String manufacturer,
                     double price, int stockQuantity, LocalDate expiryDate,
                     String category, String description,
                     int dosageMg, boolean requiresPrescription) {
        super(medicineId, name, manufacturer, price, stockQuantity,
                expiryDate, category, description);
        this.dosageMg = dosageMg;
        this.requiresPrescription = requiresPrescription;
    }

    public int getDosageMg() { return dosageMg; }
    public void setDosageMg(int dosageMg) { this.dosageMg = dosageMg; }

    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    @Override
    public String getDisplayFormat() {
        String prescription = requiresPrescription ? " [Rx Required]" : "";
        return String.format("Injection: %s (%dmg)%s - $%.2f",
                getName(), dosageMg, prescription, getPrice());
    }

    @Override
    public String getMedicineType() {
        return "INJECTION";
    }

    @Override
    public String toString() {
        return super.toString() + "|" + dosageMg + "|" + requiresPrescription;
    }
}
