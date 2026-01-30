package com.medoc_assignment.opd_token_alloc.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medoc_assignment.opd_token_alloc.constants.TokenSource;
import com.medoc_assignment.opd_token_alloc.dto.BookTokenRequest;
import com.medoc_assignment.opd_token_alloc.dto.TokenResponse;
import com.medoc_assignment.opd_token_alloc.model.Doctor;
import com.medoc_assignment.opd_token_alloc.model.Patient;
import com.medoc_assignment.opd_token_alloc.model.Slot;

@Service
public class SimulationService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Transactional
    public void runSimulation() {
        try {
            // Step 0: Create doctors and slots if not present
            createDummyDoctorsAndSlots();
            List<Slot> slots = slotService.getAllSlots();
            
            // Generate patients
            for (int i = 1; i <= 30; i++) {
                patientService.createPatient("Patient " + i, "99999" + i);
            }
            
            int patientIndex = 0;
            List<Patient> patients = patientService.getAllPatients();
            
            System.out.println("----- START OF SIMULATION -----");
            
            for (Slot slot : slots) {
                if (patientIndex >= patients.size()) {
                    System.out.println("No more patients available, ending simulation early.");
                    break;
                }
                slot.setOverflowCapacity(2);
                System.out.println("\n=== Slot " + slot.getId() + " (" + slot.getStartTime() + " - " + slot.getEndTime() + ") ===");

                // 1 Fill slot with online bookings
                for (int i = 0; i < slot.getMaxCapacity(); i++) {
                    BookTokenRequest req = new BookTokenRequest();
                    req.setPatientId(patients.get(patientIndex).getId());
                    req.setSlotId(slot.getId());
                    req.setSource(TokenSource.ONLINE);

                    TokenResponse resp = tokenService.bookToken(req.getPatientId(),
                     req.getSlotId(), req.getSource());
                    System.out.println("\n[Online Booking] " + formatToken(resp));
                    printSlotTokens(slot.getId());

                    patientIndex++;
                }

                // 2 Paid patient → may displace lower priority
                BookTokenRequest paidReq = new BookTokenRequest();
                paidReq.setPatientId(patients.get(patientIndex).getId());
                paidReq.setSlotId(slot.getId());
                paidReq.setSource(TokenSource.PAID);
                
                TokenResponse paidResp = tokenService.bookToken(paidReq.getPatientId(), 
                 paidReq.getSlotId(), paidReq.getSource());
                System.out.println("\n[Paid Booking] " + formatToken(paidResp));
                printSlotTokens(slot.getId());
                patientIndex++;

                // 3 Emergency patient → allowed via overflow
                BookTokenRequest emergencyReq = new BookTokenRequest();
                emergencyReq.setPatientId(patients.get(patientIndex).getId());
                emergencyReq.setSlotId(slot.getId());
                emergencyReq.setSource(TokenSource.EMERGENCY);
                
                TokenResponse emergencyResp = tokenService.bookToken(emergencyReq.getPatientId(),
                 emergencyReq.getSlotId(), emergencyReq.getSource());
                System.out.println("\nEmergency Booking " + formatToken(emergencyResp));
                printSlotTokens(slot.getId());
                patientIndex++;

                // 4 Walk-in → waitlisted
                BookTokenRequest walkinReq = new BookTokenRequest();
                walkinReq.setPatientId(patients.get(patientIndex).getId());
                walkinReq.setSlotId(slot.getId());
                walkinReq.setSource(TokenSource.WALKIN);

                TokenResponse walkinResp = tokenService.bookToken(walkinReq.getPatientId(), 
                walkinReq.getSlotId(), walkinReq.getSource());
                System.out.println("\nWalk-in Booking " + formatToken(walkinResp));
                printSlotTokens(slot.getId());
                patientIndex++;
                
                // 5 Cancellation → promote from waitlist
                BookTokenRequest cancelReq = new BookTokenRequest();
                cancelReq.setPatientId(patients.get(patientIndex).getId());
                cancelReq.setSlotId(slot.getId());
                cancelReq.setSource(TokenSource.ONLINE);
                
                TokenResponse toCancel = tokenService.bookToken(cancelReq.getPatientId(), 
                cancelReq.getSlotId(), cancelReq.getSource());
                System.out.println("\nBooking for Cancellation " + formatToken(toCancel));
                printSlotTokens(slot.getId());

                // Cancel it
                tokenService.cancelToken(toCancel.getTokenId());
                System.out.println("\nToken Cancelled ID=" + toCancel.getTokenId());
                printSlotTokens(slot.getId());

                patientIndex++;
            }

            System.out.println("\n----- END OF SIMULATION -----");
        } catch (Exception e) {
            System.err.println("Unexpected Error during simulation : " + e.getMessage());
            throw new RuntimeException("Unexpected Error during simulation :");
        }
    }

    private String formatToken(TokenResponse token) {
        try {
            return "TokenID=" + token.getTokenId() +
                   ", PatientID=" + token.getPatientId() +
                   ", SlotID=" + token.getSlotId() +
                   ", Status=" + token.getStatus() +
                   ", Source=" + token.getSource();
        } catch (Exception e) {
            System.err.println("Unexpected Error during formatToken : " + e.getMessage());
            throw new RuntimeException("Unexpected Error during formatToken :");
        }
    }

    private void printSlotTokens(int slotId) {
        try {
            System.out.println("Current Tokens for Slot " + slotId + ":");
            tokenService.getSlotStatus(slotId); // slot status can be shown
            // Alternatively, fetch all tokens for that slot from DB for full detail:
            List<TokenResponse> tokens = tokenService.getAllTokensForSlot(slotId);
            if (tokens != null) {
                for (TokenResponse t : tokens) {
                    System.out.println(formatToken(t));
                }
            }
        } catch (Exception e) {
            System.err.println("Unexpected Error while printSlotTokens : " + e.getMessage());
            throw new RuntimeException("Unexpected Error while printing slot tokens :");
        }
    }

    @Transactional
    public void createDummyDoctorsAndSlots() {
        try {
            // Create doctors
            Doctor doctor1 = doctorService.createDoctor("Dr. Alice", "Cardiologist");
            Doctor doctor2 = doctorService.createDoctor("Dr. Bob", "Anesthesiologist");
            Doctor doctor3 = doctorService.createDoctor("Dr. Charlie", "Dentist");

            // Create slots via SlotService (using doctorId + String start/end)
            slotService.createSlot(doctor1.getId(), "2026-01-30T09:00", "2026-01-30T10:00", 5);
            slotService.createSlot(doctor1.getId(), "2026-01-30T10:00", "2026-01-30T11:00", 5);
            slotService.createSlot(doctor2.getId(), "2026-01-30T09:00", "2026-01-30T10:00", 5);
            slotService.createSlot(doctor3.getId(), "2026-01-30T09:00", "2026-01-30T10:00", 5);

            System.out.println("Dummy doctors and slots created successfully.");
        } catch (Exception e) {
            System.err.println("Unexpected Error while creating dummy data : " + e.getMessage());
            throw new RuntimeException("Unexpected Error while creating dummy data :");
        }
    }

}
