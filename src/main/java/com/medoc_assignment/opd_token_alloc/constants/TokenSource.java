package com.medoc_assignment.opd_token_alloc.constants;

public enum TokenSource {
    EMERGENCY(1),
    PAID(2),
    FOLLOWUP(3),
    ONLINE(4),
    WALKIN(5);

    public final int priority;
    TokenSource(int priority) {
        this.priority = priority;
    }
}
