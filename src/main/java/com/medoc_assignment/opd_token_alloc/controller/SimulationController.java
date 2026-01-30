package com.medoc_assignment.opd_token_alloc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medoc_assignment.opd_token_alloc.service.SimulationService;

@RestController
@RequestMapping("/api/simulate")
public class SimulationController {
    
    @Autowired
    private SimulationService simulationService;

    @GetMapping("")
    public ResponseEntity<Void> runSimulation() {
        simulationService.runSimulation();
        return ResponseEntity.ok().build();
    }
}
