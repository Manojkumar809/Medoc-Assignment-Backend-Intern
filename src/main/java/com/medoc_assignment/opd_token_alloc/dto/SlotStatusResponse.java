package com.medoc_assignment.opd_token_alloc.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotStatusResponse {
    private int slotId;
    private int maxCapacity;
    private int activeTokens;
    private int waitlistCount;
}
