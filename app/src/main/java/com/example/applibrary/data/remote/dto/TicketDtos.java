package com.example.applibrary.data.remote.dto;

public final class TicketDtos {

    private TicketDtos() {}

    public static class QrCardView {
        public String fullName;
        public String cardNumber;
        public String status;
        public String validUntil;
    }
}
