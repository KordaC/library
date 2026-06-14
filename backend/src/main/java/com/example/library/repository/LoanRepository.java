package com.example.library.repository;

import com.example.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByCardIdAndStatus(UUID cardId, String status);
    List<Loan> findByCardIdAndStatusOrderByLoanDateDesc(UUID cardId, String status);
    List<Loan> findByCardIdAndStatusOrderByReturnedAtDesc(UUID cardId, String status);
    long countByCardIdAndStatus(UUID cardId, String status);
    long countByCardIdAndStatusAndDueDateBefore(UUID cardId, String status, LocalDate date);
    List<Loan> findByCardId(UUID cardId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT l FROM Loan l WHERE l.status = 'ACTIVE'
            """)
    List<Loan> findAllActive();
}
