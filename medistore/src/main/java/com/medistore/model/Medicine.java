package com.medistore.model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Medicine implements Serializable {
    private String medicineId;
    private String name;
    private String manufacturer;
    private double price;
    private int stockQuantity;
    private LocalDate expiryDate;
    private String category;
    private String description;

    public Medicine(String medicineId, String name, String manufacturer,
                    double price, int stockQuantity, LocalDate expiryDate,
                    String category, String description) {
        this.medicineId = medicineId;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.category = category;
        this.description = description;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public abstract String getDisplayFormat();
    public abstract String getMedicineType();

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isLowStock() {
        return stockQuantity < 10;
    }

    @Override
    public String toString() {
        return medicineId + "|" + name + "|" + manufacturer + "|" + price + "|"
                + stockQuantity + "|" + expiryDate + "|" + category + "|"
                + description + "|" + getMedicineType();
    }
}
