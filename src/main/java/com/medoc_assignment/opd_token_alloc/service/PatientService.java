package com.medoc_assignment.opd_token_alloc.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medoc_assignment.opd_token_alloc.model.Patient;
import com.medoc_assignment.opd_token_alloc.repository.PatientRepo;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

    // ---- Create a new patient ----
    @Transactional
    public Patient createPatient(String name, String phone) {
        try {
            Patient patient = new Patient();
            patient.setName(name);
            patient.setPhone(phone);
            return patientRepo.save(patient);
        } catch (Exception e) {
            System.err.println("Unexpected Error while creating a patient" + e.getMessage());
            throw new RuntimeException("Unexpected Error while creating a patient");
        }
    }

    // ---- Get all patients ----
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        try {
            return patientRepo.findAll();
        } catch (Exception e) {
            System.err.println("Unexpected Error while fetching patients" + e.getMessage());
            throw new RuntimeException("Unexpected Error while fetching patients");
        }
    }

    // ---- Get patient by ID ----
    @Transactional(readOnly = true)
    public Patient getPatientById(int id) {
        try {
            return patientRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        } catch (Exception e) {
            System.err.println("Unexpected Error while fetching a patient" + e.getMessage());
            throw new RuntimeException("Unexpected Error while fetching a patient");
        }
    }
}
