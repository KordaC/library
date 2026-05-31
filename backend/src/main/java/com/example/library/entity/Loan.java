package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan")
public class Loan {

    @Id
    private UUID id;

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Column(name = "copy_id", nullable = false)
    private UUID copyId;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_at")
    private LocalDate returnedAt;

    @Column(name = "renewal_count", nullable = false)
    private int renewalCount;

    @Column(nullable = false)
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCardId() { return cardId; }
    public void setCardId(UUID cardId) { this.cardId = cardId; }
    public UUID getCopyId() { return copyId; }
    public void setCopyId(UUID copyId) { this.copyId = copyId; }
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDate returnedAt) { this.returnedAt = returnedAt; }
    public int getRenewalCount() { return renewalCount; }
    public void setRenewalCount(int renewalCount) { this.renewalCount = renewalCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
