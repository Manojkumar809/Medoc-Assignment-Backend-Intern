package com.medoc_assignment.opd_token_alloc.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medoc_assignment.opd_token_alloc.model.Slot;
import com.medoc_assignment.opd_token_alloc.model.Token;

@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {
    @Query(value = """ 
    SELECT * FROM token WHERE slot_id = :slotId AND status = 'BOOKED' """, 
    nativeQuery = true)
    List<Token> findActiveTokens(@Param("slotId") int slotId);

    @Query(value = """
    SELECT * FROM token WHERE slot_id = :slotId AND status = 'WAITLISTED'
    ORDER BY priority ASC, created_at ASC """, nativeQuery = true)
    List<Token> findWaitlistedTokens(@Param("slotId") int slotId);

    List<Token> findBySlot(Slot slot);

}
