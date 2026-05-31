package com.example.applibrary.data.remote.dto;

public final class LoanDtos {

    private LoanDtos() {}

    public static class LoanItem {
        public String id;
        public String bookTitle;
        public String authorName;
        public String loanDate;
        public String dueDate;
        public String returnedAt;
        public String status;
        public int renewalCount;
        public boolean canRenew;
        public boolean overdue;
    }

    public static class RenewResponse {
        public String loanId;
        public String newDueDate;
        public int renewalCount;
    }
}
