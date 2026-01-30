package com.medoc_assignment.opd_token_alloc.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.medoc_assignment.opd_token_alloc.model.Slot;
import com.medoc_assignment.opd_token_alloc.service.SlotService;

@RestController
@RequestMapping("/api/slot")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @PostMapping("/create")
    public ResponseEntity<Slot> createSlot(
            @RequestParam int doctorId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam int maxCapacity) {
        Slot slot = slotService.createSlot(doctorId, startTime, endTime, maxCapacity);
        return ResponseEntity.ok(slot);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Slot>> getAllSlots() {
        return ResponseEntity.ok(slotService.getAllSlots());
    }
}
