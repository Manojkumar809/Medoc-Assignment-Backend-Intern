package com.medoc_assignment.opd_token_alloc.service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medoc_assignment.opd_token_alloc.constants.TokenSource;
import com.medoc_assignment.opd_token_alloc.constants.TokenStatus;
import com.medoc_assignment.opd_token_alloc.dto.SlotStatusResponse;
import com.medoc_assignment.opd_token_alloc.dto.TokenResponse;
import com.medoc_assignment.opd_token_alloc.model.Patient;
import com.medoc_assignment.opd_token_alloc.model.Slot;
import com.medoc_assignment.opd_token_alloc.model.Token;
import com.medoc_assignment.opd_token_alloc.repository.PatientRepo;
import com.medoc_assignment.opd_token_alloc.repository.SlotRepo;
import com.medoc_assignment.opd_token_alloc.repository.TokenRepo;

@Service
public class TokenService {
    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private SlotRepo slotRepo;

    @Autowired
    private PatientRepo patientRepo;

    /* -------------------- BOOK TOKEN -------------------- */
    @Transactional
    public TokenResponse bookToken(int patientId, int slotId, TokenSource source) {

        try {
            Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            Slot slot = slotRepo.findById(slotId)
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            List<Token> activeTokens = tokenRepo.findActiveTokens(slotId);

            Token incoming = new Token();
            incoming.setPatient(patient);
            incoming.setSlot(slot);
            incoming.setSource(source);
            incoming.setPriority(source.priority);
            incoming.setStatus(TokenStatus.BOOKED);

            // --- Emergency override ---
            if (source == TokenSource.EMERGENCY) {
                if (activeTokens.size() < slot.getMaxCapacity() + slot.getOverflowCapacity()) {
                    tokenRepo.save(incoming);
                    return toResponse(incoming);
                } else {
                    throw new RuntimeException("Emergency overflow exceeded");
                }
            }

            // --- Normal booking: within capacity ---
            if (activeTokens.size() < slot.getMaxCapacity()) {
                tokenRepo.save(incoming);
                return toResponse(incoming);
            }

            // --- Reallocation logic: displace lower priority token ---
            Token lowest = activeTokens.stream()
                    .max(Comparator.comparingInt(Token::getPriority))
                    .orElse(null);

            if (lowest != null && lowest.getPriority() > incoming.getPriority()) {
                lowest.setStatus(TokenStatus.WAITLISTED);
                tokenRepo.save(lowest);

                tokenRepo.save(incoming);
                return toResponse(incoming);
            }

            // --- Slot full, waitlist ---
            incoming.setStatus(TokenStatus.WAITLISTED);
            tokenRepo.save(incoming);
            return toResponse(incoming);
            
        } catch (RuntimeException e) {
            // Handle known runtime errors
            System.err.println("Unexpected error booking token: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while booking token");
        } catch (Exception e) {
            // Handle unknown errors
            System.err.println("Unexpected error booking token: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while booking token");
        }
    }

    // -------------------- CANCEL TOKEN --------------------
    @Transactional
    public void cancelToken(int tokenId) {
        try {
            Token token = tokenRepo.findById(tokenId)
                    .orElseThrow(() -> new RuntimeException("Token not found"));
    
            token.setStatus(TokenStatus.CANCELLED);
            tokenRepo.save(token);
    
            promoteFromWaitlist(token.getSlot().getId());
        } catch (Exception e) {
            System.err.println("Unexpected error while cancelling token: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while cancelling token");
        }
    }

    // -------------------- NO-SHOW --------------------
    @Transactional
    public void markNoShow(int tokenId) {
        try {
            Token token = tokenRepo.findById(tokenId)
                    .orElseThrow(() -> new RuntimeException("Token not found"));
    
            token.setStatus(TokenStatus.NO_SHOW);
            tokenRepo.save(token);
    
            promoteFromWaitlist(token.getSlot().getId());
        } catch (Exception e) {
            System.err.println("Unexpected error while setting no-show status: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while setting no-show status");
        }
    }


    // -------------------- GET SLOT STATUS --------------------
    @Transactional(readOnly = true)
    public SlotStatusResponse getSlotStatus(int slotId) {
        try {
            Slot slot = slotRepo.findById(slotId).orElseThrow();
            int active = tokenRepo.findActiveTokens(slotId).size();
            int waitlist = tokenRepo.findWaitlistedTokens(slotId).size();
    
            SlotStatusResponse resp = new SlotStatusResponse();
            resp.setSlotId(slotId);
            resp.setMaxCapacity(slot.getMaxCapacity());
            resp.setActiveTokens(active);
            resp.setWaitlistCount(waitlist);
            return resp;
        } catch (Exception e) {
            System.err.println("Unexpected error while getting slot status: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while getting slot status");
        }
    }

    // -------------------- PROMOTE WAITLIST --------------------
    @Transactional
    public void promoteFromWaitlist(int slotId) {
        try {
            Slot slot = slotRepo.findById(slotId).orElseThrow();
            List<Token> active = tokenRepo.findActiveTokens(slotId);
    
            if (active.size() >= slot.getMaxCapacity()) return;
    
            List<Token> waitlist = tokenRepo.findWaitlistedTokens(slotId);
            if (!waitlist.isEmpty()) {
                Token promoted = waitlist.get(0);
                promoted.setStatus(TokenStatus.BOOKED);
                tokenRepo.save(promoted);
            }
        } catch (Exception e) {
            System.err.println("Unexpected error while promiting from waitlist: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while promiting from waitlist");
        }
    }

    // -------------------- HELPER: TokenResponse --------------------
    private TokenResponse toResponse(Token token) {
        try {
            TokenResponse resp = new TokenResponse();
            resp.setTokenId(token.getId());
            resp.setStatus(token.getStatus().name());
            resp.setSource(token.getSource().name());
            resp.setPatientId(token.getPatient().getId());
            resp.setSlotId(token.getSlot().getId());
            return resp;
        } catch (Exception e) {
            System.err.println("Unexpected error while returning token response: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while returning token response");
        }
    }

    // ------------------- Get Tokens in a slot ------------------
    public List<TokenResponse> getAllTokensForSlot(int slotId) {
        Slot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        List<Token> tokens = tokenRepo.findBySlot(slot);

        List<TokenResponse> response = new ArrayList<>();
        for (Token token : tokens) {
            TokenResponse tResp = new TokenResponse();
            tResp.setTokenId(token.getId());
            tResp.setPatientId(token.getPatient().getId());
            tResp.setSlotId(slot.getId());
            tResp.setStatus(token.getStatus().name());
            tResp.setSource(token.getSource().name());
            response.add(tResp);
        }

        return response;
    }
}

