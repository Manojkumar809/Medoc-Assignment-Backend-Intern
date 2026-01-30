# OPD Token Allocation Engine

## Overview

This project implements an **OPD Token Allocation System** for hospitals that manages patient tokens across fixed doctor time slots while handling real-world variability such as priority patients, cancellations, no-shows, and emergency insertions.

The system enforces hard per-slot limits, supports dynamic reallocation, and prioritizes tokens based on source type.

---

## Core Concepts

### Doctors & Slots

Doctors operate in fixed time slots (e.g., 9:00–10:00).

Each slot has:

- **maxCapacity** → hard limit for normal bookings  
- Optional **overflow** (used for emergencies)

### Token Sources & Priority

Tokens are generated from multiple sources with predefined priority:

| Source     | Priority |
|------------|----------|
| EMERGENCY  | Highest  |
| PAID       | High     |
| FOLLOWUP   | Medium   |
| ONLINE     | Low      |
| WALK-IN    | Lowest   |

> Lower numeric value = higher priority.

### Token Lifecycle

A token can be in one of the following states:

- **BOOKED**  
- **WAITLISTED**  
- **CANCELLED**  
- **NO_SHOW**  
- **COMPLETED**

---

## Allocation Algorithm

### Booking Flow

**Within capacity**  
If active tokens < maxCapacity, token is **BOOKED**.

**Slot full**  
- If incoming token has **higher priority** than the lowest-priority booked token:
  - Lower-priority token is moved to **WAITLIST**
  - Incoming token is **BOOKED**
- If no displacement possible:
  - Incoming token is **WAITLISTED**

**Emergency tokens**  
- Allowed via **controlled overflow** or **priority-based displacement** (bounded to protect doctor workload)

### Cancellation / No-Show Handling

- When a token is **cancelled** or marked **no-show**:
  - The highest-priority waitlisted token (FIFO within same priority) is **promoted to BOOKED**
- Promotion is **automatic and transactional**

### Elastic Capacity Management

Elasticity is achieved via:

- Priority-based displacement
- Waitlist promotion
- Controlled overflow for emergencies

> Slot times are **not dynamically shifted**. This design prioritizes predictability and doctor workload safety over aggressive rescheduling.

---

## API Design

### Doctor APIs

- `POST /api/doctors/create`  
- `GET /api/doctors/all`  

### Patient APIs

- `POST /api/patient/create`  
- `GET /api/patient/all`  

### Slot APIs

- `POST /api/slot/create`  
- `GET /api/slot/all`  

### Token APIs

- `POST /token/book`  
- `POST /token/{id}/cancel`  
- `POST /token/{id}/no-show`  
- `GET /token/slot/{id}/status`  
- `GET /token/slot/{slotId}/tokens`  

### Simulation

- `GET /api/simulate`

---

## Simulation Scenario

The simulation demonstrates:

- 3 doctors  
- Multiple slots  
- Online bookings filling slots  
- Paid patients displacing lower-priority tokens  
- Emergency insertions  
- Walk-in waitlisting  
- Cancellation leading to automatic promotion  

> Simulation assumes a **fresh database state**.

---

## Edge Cases Handled

- Slot **hard limit enforcement**  
- **Priority-based displacement**  
- Waitlist ordering (**priority + FIFO**)  
- Cancellation and no-show promotion  
- Emergency insertion under **bounded overflow**  
- Invalid patient / slot handling

---

## Failure Handling

- All operations are **transactional**  
- Invalid operations throw **descriptive runtime exceptions**  
- Booking, cancellation, and promotion logic is **atomic**  
- System avoids **silent failures**

---

## Trade-offs & Design Decisions

- **No slot shifting on doctor delay**  
  - Chosen to keep scheduling predictable and safe.
- **Bounded emergency overflow**  
  - Prevents unlimited doctor overload.
- **No concurrency locking implemented**  
  - In production, slot-level locking or serializable isolation would be added.

---

## Tech Stack

- Java  
- Spring Boot  
- Spring Data JPA  
- H2 / Relational DB  
- REST APIs  

---

## Conclusion

This system provides a **realistic, production-aligned OPD token allocation model** that balances fairness, urgency, and operational safety while remaining extensible for future enhancements like analytics or predictive scheduling.
