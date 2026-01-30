package com.medoc_assignment.opd_token_alloc.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.medoc_assignment.opd_token_alloc.model.Doctor;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Integer> {
    
}
