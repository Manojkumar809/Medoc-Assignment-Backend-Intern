package com.medoc_assignment.opd_token_alloc.model;
import java.time.LocalDateTime;
import com.medoc_assignment.opd_token_alloc.constants.TokenSource;
import com.medoc_assignment.opd_token_alloc.constants.TokenStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @Enumerated(EnumType.STRING)
    private TokenSource source;

    private int priority;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
