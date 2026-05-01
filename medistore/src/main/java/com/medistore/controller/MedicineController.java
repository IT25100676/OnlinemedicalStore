package com.medistore.controller;

import com.medistore.model.Injection;
import com.medistore.model.Medicine;
import com.medistore.model.Syrup;
import com.medistore.model.Tablet;
import com.medistore.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/medicine")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping
    public String listMedicines(Model model) {
        List<Medicine> medicines = medicineService.getAllMedicines();
        model.addAttribute("medicines", medicines);
        model.addAttribute("expiredCount", medicineService.getExpiredMedicines().size());
        model.addAttribute("lowStockCount", medicineService.getLowStockMedicines().size());
        return "management/medicine/medicineList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categories", new String[]{"Antibiotics", "Pain Relief", "Vitamins", "Cardiac", "Diabetes", "General"});
        return "management/medicine/addMedicine";
    }

    @PostMapping("/add")
    public String addMedicine(
            @RequestParam String medicineId,
            @RequestParam String name,
            @RequestParam String manufacturer,
            @RequestParam double price,
            @RequestParam int stockQuantity,
            @RequestParam String expiryDate,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String medicineType,
            @RequestParam(required = false) Integer mgPerTablet,
            @RequestParam(required = false) Integer tabletsPerStrip,
            @RequestParam(required = false) Integer volumeMl,
            @RequestParam(required = false) String flavor,
            @RequestParam(required = false) Integer dosageMg,
            @RequestParam(required = false) Boolean requiresPrescription,
            RedirectAttributes redirectAttributes) {

        if (medicineService.medicineExists(medicineId)) {
            redirectAttributes.addFlashAttribute("error", "Medicine ID already exists!");
            return "redirect:/admin/medicine/add";
        }

        LocalDate expDate = LocalDate.parse(expiryDate);
        Medicine medicine;

        switch (medicineType) {
            case "TABLET":
                medicine = new Tablet(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        mgPerTablet, tabletsPerStrip);
                break;
            case "SYRUP":
                medicine = new Syrup(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        volumeMl, flavor);
                break;
            case "INJECTION":
                medicine = new Injection(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        dosageMg, requiresPrescription != null ? requiresPrescription : false);
                break;
            default:
                redirectAttributes.addFlashAttribute("error", "Invalid medicine type!");
                return "redirect:/admin/medicine/add";
        }

        medicineService.addMedicine(medicine);
        redirectAttributes.addFlashAttribute("success", "Medicine added successfully!");
        return "redirect:/admin/medicine";
    }

    @GetMapping("/search")
    public String searchMedicines(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String category,
                                  Model model) {
        List<Medicine> results;

        if (keyword != null && !keyword.isEmpty()) {
            results = medicineService.searchMedicines(keyword);
            model.addAttribute("searchKeyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            results = medicineService.searchByCategory(category);
            model.addAttribute("searchCategory", category);
        } else {
            results = medicineService.getAllMedicines();
        }

        model.addAttribute("medicines", results);
        model.addAttribute("categories", new String[]{"Antibiotics", "Pain Relief", "Vitamins", "Cardiac", "Diabetes", "General"});
        return "management/medicine/searchMedicine";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) {
        Medicine medicine = medicineService.getMedicineById(id);
        if (medicine == null) {
            return "redirect:/admin/medicine";
        }
        model.addAttribute("medicine", medicine);
        model.addAttribute("categories", new String[]{"Antibiotics", "Pain Relief", "Vitamins", "Cardiac", "Diabetes", "General"});
        return "management/medicine/updateMedicine";
    }

    @PostMapping("/update")
    public String updateMedicine(
            @RequestParam String medicineId,
            @RequestParam String name,
            @RequestParam String manufacturer,
            @RequestParam double price,
            @RequestParam int stockQuantity,
            @RequestParam String expiryDate,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String medicineType,
            @RequestParam(required = false) Integer mgPerTablet,
            @RequestParam(required = false) Integer tabletsPerStrip,
            @RequestParam(required = false) Integer volumeMl,
            @RequestParam(required = false) String flavor,
            @RequestParam(required = false) Integer dosageMg,
            @RequestParam(required = false) Boolean requiresPrescription,
            RedirectAttributes redirectAttributes) {

        LocalDate expDate = LocalDate.parse(expiryDate);
        Medicine medicine;

        switch (medicineType) {
            case "TABLET":
                medicine = new Tablet(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        mgPerTablet, tabletsPerStrip);
                break;
            case "SYRUP":
                medicine = new Syrup(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        volumeMl, flavor);
                break;
            case "INJECTION":
                medicine = new Injection(medicineId, name, manufacturer, price,
                        stockQuantity, expDate, category, description,
                        dosageMg, requiresPrescription != null ? requiresPrescription : false);
                break;
            default:
                redirectAttributes.addFlashAttribute("error", "Invalid medicine type!");
                return "redirect:/admin/medicine";
        }

        medicineService.updateMedicine(medicine);
        redirectAttributes.addFlashAttribute("success", "Medicine updated successfully!");
        return "redirect:/admin/medicine";
    }

    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable String id, RedirectAttributes redirectAttributes) {
        medicineService.deleteMedicine(id);
        redirectAttributes.addFlashAttribute("success", "Medicine deleted successfully!");
        return "redirect:/admin/medicine";
    }

    @GetMapping("/expired")
    public String viewExpired(Model model) {
        model.addAttribute("medicines", medicineService.getExpiredMedicines());
        model.addAttribute("viewType", "Expired");
        return "management/medicine/medicineList";
    }

    @GetMapping("/low-stock")
    public String viewLowStock(Model model) {
        model.addAttribute("medicines", medicineService.getLowStockMedicines());
        model.addAttribute("viewType", "Low Stock");
        return "management/medicine/medicineList";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/medicine";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Controller working!";
    }
}
