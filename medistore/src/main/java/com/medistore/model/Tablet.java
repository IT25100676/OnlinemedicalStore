package com.medistore.model;

import java.time.LocalDate;

public class Tablet extends Medicine {
    private int mgPerTablet;
    private int tabletsPerStrip;

    public Tablet(String medicineId, String name, String manufacturer,
                  double price, int stockQuantity, LocalDate expiryDate,
                  String category, String description,
                  int mgPerTablet, int tabletsPerStrip) {
        super(medicineId, name, manufacturer, price, stockQuantity,
                expiryDate, category, description);
        this.mgPerTablet = mgPerTablet;
        this.tabletsPerStrip = tabletsPerStrip;
    }

    public int getMgPerTablet() { return mgPerTablet; }
    public void setMgPerTablet(int mgPerTablet) { this.mgPerTablet = mgPerTablet; }

    public int getTabletsPerStrip() { return tabletsPerStrip; }
    public void setTabletsPerStrip(int tabletsPerStrip) { this.tabletsPerStrip = tabletsPerStrip; }

    @Override
    public String getDisplayFormat() {
        return String.format("Tablet: %s (%dmg x %d tablets) - $%.2f",
                getName(), mgPerTablet, tabletsPerStrip, getPrice());
    }

    @Override
    public String getMedicineType() {
        return "TABLET";
    }

    @Override
    public String toString() {
        return super.toString() + "|" + mgPerTablet + "|" + tabletsPerStrip;
    }
}
