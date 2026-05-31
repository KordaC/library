package com.example.library.dto;

public final class LoanDtos {

    private LoanDtos() {}

    public record LoanItem(
            String id,
            String bookTitle,
            String authorName,
            String loanDate,
            String dueDate,
            String returnedAt,
            String status,
            int renewalCount,
            boolean canRenew,
            boolean overdue
    ) {}
}
