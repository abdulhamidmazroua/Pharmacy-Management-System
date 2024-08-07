package com.hameed.springboot.pharmacyms.controller;

import com.hameed.springboot.pharmacyms.model.entity.Medication;
import com.hameed.springboot.pharmacyms.service.CategoryService;
import com.hameed.springboot.pharmacyms.service.MedicationService;
import com.hameed.springboot.pharmacyms.service.UnitOfMeasureService;
import com.hameed.springboot.pharmacyms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.util.List;


@Controller
@RequestMapping("/medications")
public class MedicationsController {

    private MedicationService medicationService;
    private CategoryService categoryService;
    private UnitOfMeasureService unitOfMeasureService;
    private UserService userService;

    @Autowired
    public MedicationsController(MedicationService medicationService,
                                 CategoryService categoryService,
                                 UnitOfMeasureService unitOfMeasureService,
                                 UserService userService) {
        this.medicationService = medicationService;
        this.categoryService = categoryService;
        this.unitOfMeasureService = unitOfMeasureService;
        this.userService = userService;
    }

    // @InitBinder works as a pre-processor
    // It will pre-process each web request to our controller
    // Here, we will use it to trim strings,
    // removing all leading and trailing white space
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping
    public String showAllMedications(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        model.addAttribute("medication", new Medication());
        model.addAttribute("medications", medicationService.getAllMedications());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("unitOfMeasures", unitOfMeasureService.getAllUnitsOfMeasures());
        model.addAttribute("fragment", "/fragments/medications-frag");
        model.addAttribute("fragment_id", "medications-frag");

        return "XMLHttpRequest".equals(requestedWith) ? "/fragments/medications-frag" : "layout";
    }

    @PostMapping("/add")
    public String addMedication(@ModelAttribute("medication") Medication medication, @RequestHeader(value = "X-Requested-With", required = true) String requestedWith) {
        if (!"XMLHttpRequest".equals(requestedWith)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        medicationService.createMedication(medication);
        return "redirect:/medications";
    }

    @PostMapping("/update")
    public String updateMedication(@ModelAttribute("medication") Medication medication, @RequestHeader(value = "X-Requested-With", required = true) String requestedWith) {
        if (!"XMLHttpRequest".equals(requestedWith)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        medicationService.updateMedication(medication);
        return "redirect:/medications";
    }

    @PostMapping("/{id}")
    public String deleteMedication(@PathVariable Long id, @RequestHeader(value = "X-Requested-With", required = true) String requestedWith) {
        if (!"XMLHttpRequest".equals(requestedWith)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        medicationService.deleteMedication(id);
        return "redirect:/medications";
    }


//    // this will be used later in the above showAllMedications endpoint
//    @GetMapping("/page")
//    public String showMedicationsPage(@RequestParam("page") int page, @RequestParam("size") int size, Model model, @ModelAttribute List<Medication> medications) {
//        Pageable pageable = (Pageable) PageRequest.of(page, size);
//        model.addAttribute("medications", medications);
//        return "medications-page";
//    }

//    // this will be used later for implementing
//    @GetMapping("/search/all")
//    @ResponseBody
//    public String searchMedications(Model model,
//                                    @RequestParam(value = "medicationName", required = false) String medicationName,
//                                    @RequestParam(value = "categoryCode", required = false) String CategoryCode) {
//        model.addAttribute("medications", medicationService.searchMedications(medicationName, CategoryCode));
//        return "/medications/page";
//    }

    // medication service endpoint for searching for a medication
    // we should include another one of this for the medication barcode
    @GetMapping("/search")
    @ResponseBody
    public Medication getMedicationByName(@RequestParam(value = "medicationName", required = true) String medicationName) {
        return medicationService.getMedicationByName(medicationName);
    }



}
