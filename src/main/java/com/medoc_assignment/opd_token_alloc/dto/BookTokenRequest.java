package com.medoc_assignment.opd_token_alloc.dto;
import com.medoc_assignment.opd_token_alloc.constants.TokenSource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTokenRequest {
    private int patientId;
    private int slotId;
    private TokenSource source;
}
