package com.medoc_assignment.opd_token_alloc.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medoc_assignment.opd_token_alloc.dto.BookTokenRequest;
import com.medoc_assignment.opd_token_alloc.dto.SlotStatusResponse;
import com.medoc_assignment.opd_token_alloc.dto.TokenResponse;
import com.medoc_assignment.opd_token_alloc.service.TokenService;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // --- Book Token ---
    @PostMapping("/book")
    public ResponseEntity<TokenResponse> book(@RequestBody BookTokenRequest req) {
        return ResponseEntity.ok(
                tokenService.bookToken(req.getPatientId(), req.getSlotId(), req.getSource())
        );
    }

    // --- Cancel Token ---
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable int id) {
        tokenService.cancelToken(id);
        return ResponseEntity.ok().build();
    }

    // --- No-Show ---
    @PostMapping("/{id}/no-show")
    public ResponseEntity<Void> noShow(@PathVariable int id) {
        tokenService.markNoShow(id);
        return ResponseEntity.ok().build();
    }

    // --- Slot Status ---
    @GetMapping("/slot/{id}/status")
    public ResponseEntity<SlotStatusResponse> getSlotStatus(@PathVariable int id) {
        return ResponseEntity.ok(tokenService.getSlotStatus(id));
    }

    // --- Tokens in slot ---
    @GetMapping("/slot/{slotId}/tokens")
    public ResponseEntity<List<TokenResponse>> getAllTokensForSlot(@PathVariable int slotId) {
        List<TokenResponse> tokens = tokenService.getAllTokensForSlot(slotId);
        return ResponseEntity.ok(tokens);
    }
}
