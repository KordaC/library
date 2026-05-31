package com.example.applibrary.data.remote.dto;

import java.util.List;

public final class DashboardDtos {

    private DashboardDtos() {}

    public static class DashboardResponse {
        public CardSummary card;
        public AuthDtos.UserBrief user;
        public LoanCounts loans;
        public List<NotificationItem> notifications;
    }

    public static class CardSummary {
        public String number;
        public String status;
    }

    public static class LoanCounts {
        public int activeCount;
        public int overdueCount;
    }

    public static class NotificationItem {
        public String id;
        public String type;
        public String title;
        public String body;
        public boolean read;
    }

    public static class QrResponse {
        public QrPayload payload;
        public String scanUrl;
    }

    public static class QrPayload {
        public int v;
        public String uid;
        public String card;
        public long iat;
        public long exp;
        public String sig;
    }
}
