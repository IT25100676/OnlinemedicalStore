package com.medistore.model;

import java.time.LocalDate;

public class Syrup extends Medicine {
    private int volumeMl;
    private String flavor;

    public Syrup(String medicineId, String name, String manufacturer,
                 double price, int stockQuantity, LocalDate expiryDate,
                 String category, String description,
                 int volumeMl, String flavor) {
        super(medicineId, name, manufacturer, price, stockQuantity,
                expiryDate, category, description);
        this.volumeMl = volumeMl;
        this.flavor = flavor;
    }

    public int getVolumeMl() { return volumeMl; }
    public void setVolumeMl(int volumeMl) { this.volumeMl = volumeMl; }

    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }

    @Override
    public String getDisplayFormat() {
        return String.format("Syrup: %s (%dml, %s flavor) - $%.2f",
                getName(), volumeMl, flavor, getPrice());
    }

    @Override
    public String getMedicineType() {
        return "SYRUP";
    }

    @Override
    public String toString() {
        return super.toString() + "|" + volumeMl + "|" + flavor;
    }
}
