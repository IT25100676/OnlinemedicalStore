package com.medistore.service;

import com.medistore.model.Injection;
import com.medistore.model.Medicine;
import com.medistore.model.Syrup;
import com.medistore.model.Tablet;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileMedicineService implements MedicineService {

    private static final String FILE_PATH = "data/medicines.txt";
    private static final String DELIMITER = "\\|";

    @Override
    public void addMedicine(Medicine medicine) {
        List<Medicine> medicines = readAllFromFile();
        medicines.add(medicine);
        writeAllToFile(medicines);
    }

    @Override
    public List<Medicine> getAllMedicines() {
        return readAllFromFile();
    }

    @Override
    public Medicine getMedicineById(String medicineId) {
        return readAllFromFile().stream()
                .filter(m -> m.getMedicineId().equalsIgnoreCase(medicineId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Medicine> searchMedicines(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return readAllFromFile().stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerKeyword) ||
                        m.getManufacturer().toLowerCase().contains(lowerKeyword) ||
                        m.getCategory().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Medicine> searchByCategory(String category) {
        return readAllFromFile().stream()
                .filter(m -> m.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMedicine(Medicine updatedMedicine) {
        List<Medicine> medicines = readAllFromFile();
        for (int i = 0; i < medicines.size(); i++) {
            if (medicines.get(i).getMedicineId().equals(updatedMedicine.getMedicineId())) {
                medicines.set(i, updatedMedicine);
                break;
            }
        }
        writeAllToFile(medicines);
    }

    @Override
    public void deleteMedicine(String medicineId) {
        List<Medicine> medicines = readAllFromFile();
        medicines.removeIf(m -> m.getMedicineId().equals(medicineId));
        writeAllToFile(medicines);
    }

    @Override
    public List<Medicine> getExpiredMedicines() {
        return readAllFromFile().stream()
                .filter(Medicine::isExpired)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medicine> getLowStockMedicines() {
        return readAllFromFile().stream()
                .filter(Medicine::isLowStock)
                .collect(Collectors.toList());
    }

    @Override
    public boolean medicineExists(String medicineId) {
        return readAllFromFile().stream()
                .anyMatch(m -> m.getMedicineId().equals(medicineId));
    }

    private List<Medicine> readAllFromFile() {
        List<Medicine> medicines = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return medicines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Medicine medicine = parseMedicine(line);
                if (medicine != null) {
                    medicines.add(medicine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading medicines file: " + e.getMessage());
        }
        return medicines;
    }

    private void writeAllToFile(List<Medicine> medicines) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Medicine medicine : medicines) {
                writer.write(medicine.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to medicines file: " + e.getMessage());
        }
    }

    private Medicine parseMedicine(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 9) return null;

        String medicineId = parts[0];
        String name = parts[1];
        String manufacturer = parts[2];
        double price = Double.parseDouble(parts[3]);
        int stockQuantity = Integer.parseInt(parts[4]);
        LocalDate expiryDate = LocalDate.parse(parts[5]);
        String category = parts[6];
        String description = parts[7];
        String type = parts[8];

        switch (type) {
            case "TABLET":
                if (parts.length < 11){
                    System.err.println("Skipping invalid Tablet: " + line);
                    return null;
                }

                int mgPerTablet = Integer.parseInt(parts[9]);
                int tabletsPerStrip = Integer.parseInt(parts[10]);
                return new Tablet(medicineId, name, manufacturer, price,
                        stockQuantity, expiryDate, category, description,
                        mgPerTablet, tabletsPerStrip);

            case "SYRUP":
                if (parts.length < 11) {
                    System.err.println("Skipping invalid Syrup: " + line);
                    return null;
                }
                int volumeMl = Integer.parseInt(parts[9]);
                String flavor = parts[10];
                return new Syrup(medicineId, name, manufacturer, price,
                        stockQuantity, expiryDate, category, description,
                        volumeMl, flavor);

            case "INJECTION":
                if (parts.length < 11) {
                    System.err.println("Skipping invalid Injection: " + line);
                    return null;
                }
                int dosageMg = Integer.parseInt(parts[9]);
                boolean requiresPrescription = Boolean.parseBoolean(parts[10]);
                return new Injection(medicineId, name, manufacturer, price,
                        stockQuantity, expiryDate, category, description,
                        dosageMg, requiresPrescription);

            default:
                return null;
        }
    }
}
