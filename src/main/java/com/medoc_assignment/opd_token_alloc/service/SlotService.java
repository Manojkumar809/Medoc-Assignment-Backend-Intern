package com.medoc_assignment.opd_token_alloc.service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medoc_assignment.opd_token_alloc.model.Doctor;
import com.medoc_assignment.opd_token_alloc.model.Slot;
import com.medoc_assignment.opd_token_alloc.repository.SlotRepo;

@Service
public class SlotService {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SlotRepo slotRepo;

    @Transactional
    public Slot createSlot(int doctorId, String startTime, String endTime, int maxCapacity) {
        try {
            // Fetch doctor
            Doctor doctor = doctorService.getAllDoctors()
                                        .stream()
                                        .filter(d -> d.getId() == doctorId)
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException("Doctor not found"));

            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            Slot slot = new Slot();
            slot.setDoctor(doctor);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setMaxCapacity(maxCapacity);
            return slotRepo.save(slot);
        } catch (Exception e) {
            System.err.println("Unexpected Error while creating slot : "+ e.getMessage());
            throw new RuntimeException("Unexpected Error while creating slot");
        }
    }

    public List<Slot> getAllSlots() {
        try {
            return slotRepo.findAll();
        } catch (Exception e) {
            System.err.println("Unexpected Error while fetching slots : "+ e.getMessage());
            throw new RuntimeException("Unexpected Error while fetching slots");
        }
    }
}
