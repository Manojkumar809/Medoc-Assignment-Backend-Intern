package com.medoc_assignment.opd_token_alloc.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private int tokenId;
    private String status;
    private String source;
    private int patientId;
    private int slotId;
}
