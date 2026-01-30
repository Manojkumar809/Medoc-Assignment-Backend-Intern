package com.medoc_assignment.opd_token_alloc.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medoc_assignment.opd_token_alloc.model.Doctor;
import com.medoc_assignment.opd_token_alloc.repository.DoctorRepo;

@Service
public class DoctorService {
    
    @Autowired
     private DoctorRepo doctorRepo;

    @Transactional
    public Doctor createDoctor(String name, String specialization) {
        try {
            Doctor doctor = new Doctor();
            doctor.setName(name);
            doctor.setSpecialization(specialization);
            return doctorRepo.save(doctor);
        } catch (Exception e) {
            System.err.println("Unexpected Exception while creating doctor" + e.getMessage());
            throw new RuntimeException("Unexpected Exception while creating doctor");
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        try {
            return doctorRepo.findAll();
        } catch (Exception e) {
            System.err.println("Unexpected Exception while fetching doctors" + e.getMessage());
            throw new RuntimeException("Unexpected Exception while fetching doctors");
        }
    }
}
