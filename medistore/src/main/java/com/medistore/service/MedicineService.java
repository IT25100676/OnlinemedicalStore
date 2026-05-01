package com.medistore.service;

import com.medistore.model.Medicine;
import java.util.List;

public interface MedicineService {
    void addMedicine(Medicine medicine);
    List<Medicine> getAllMedicines();
    Medicine getMedicineById(String medicineId);
    List<Medicine> searchMedicines(String keyword);
    List<Medicine> searchByCategory(String category);
    void updateMedicine(Medicine medicine);
    void deleteMedicine(String medicineId);
    List<Medicine> getExpiredMedicines();
    List<Medicine> getLowStockMedicines();
    boolean medicineExists(String medicineId);
}
