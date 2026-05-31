package com.example.library.dto;

import com.example.library.dto.AuthDtos.UserBrief;

import java.util.List;

public record DashboardDto(
        CardSummary card,
        UserBrief user,
        LoanCounts loans,
        List<NotificationItem> notifications
) {
    public record CardSummary(String number, String status) {}
    public record LoanCounts(int activeCount, int overdueCount) {}
    public record NotificationItem(String id, String type, String title, String body, boolean read) {}
}
