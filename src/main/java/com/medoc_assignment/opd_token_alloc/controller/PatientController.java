package com.medoc_assignment.opd_token_alloc.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.medoc_assignment.opd_token_alloc.model.Patient;
import com.medoc_assignment.opd_token_alloc.service.PatientService;

@RestController
@RequestMapping("api/patient")
public class PatientController {
    
    @Autowired
     private PatientService patientService;

    // ---- Create a new patient ----
    @PostMapping("/create")
    public ResponseEntity<Patient> createPatient(
            @RequestParam String name,
            @RequestParam String phone) {

        Patient patient = patientService.createPatient(name, phone);
        return ResponseEntity.ok(patient);
    }

    // ---- Get all patients ----
    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // ---- Get patient by ID ----
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable int id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }
}
