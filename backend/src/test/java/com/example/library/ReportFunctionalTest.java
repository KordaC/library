package com.example.library;

import com.example.library.dto.AuthDtos;
import com.example.library.dto.BookDtos;
import com.example.library.dto.EventDtos;
import com.example.library.dto.RegistrationDtos;
import com.example.library.service.AuthService;
import com.example.library.service.CardService;
import com.example.library.service.CatalogService;
import com.example.library.service.DashboardService;
import com.example.library.service.EventService;
import com.example.library.service.LoanService;
import com.example.library.service.RegistrationService;
import com.example.library.support.ReportTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:report_functional;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
})
class ReportFunctionalTest {

    @Autowired AuthService authService;
    @Autowired CardService cardService;
    @Autowired CatalogService catalogService;
    @Autowired LoanService loanService;
    @Autowired EventService eventService;
    @Autowired RegistrationService registrationService;
    @Autowired DashboardService dashboardService;

    @Test
    @DisplayName("Ф1 — Вход по номеру билета")
    void f1_loginByCardNumber() {
        AuthDtos.LoginResponse response = authService.login(
                new AuthDtos.LoginRequest(ReportTestData.DEMO_CARD, ReportTestData.DEMO_PASSWORD));
        assertNotNull(response.accessToken());
        assertEquals(ReportTestData.DEMO_CARD, response.user().cardNumber());
        assertTrue(response.user().fullName().contains("Иванов"));
    }

    @Test
    @DisplayName("Ф2 — Вход по телефону")
    void f2_loginByPhone() {
        AuthDtos.LoginResponse response = authService.login(
                new AuthDtos.LoginRequest(ReportTestData.DEMO_PHONE, ReportTestData.DEMO_PASSWORD));
        assertNotNull(response.accessToken());
        assertEquals(ReportTestData.DEMO_CARD, response.user().cardNumber());
    }

    @Test
    @DisplayName("Ф3 — Вход по e-mail")
    void f3_loginByEmail() {
        AuthDtos.LoginResponse response = authService.login(
                new AuthDtos.LoginRequest(ReportTestData.DEMO_EMAIL, ReportTestData.DEMO_PASSWORD));
        assertNotNull(response.accessToken());
        assertEquals(ReportTestData.DEMO_CARD, response.user().cardNumber());
    }

    @Test
    @DisplayName("Ф4 — Отображение QR-билета")
    void f4_qrPayloadGenerated() {
        var qr = cardService.getQrForUser(ReportTestData.DEMO_USER_ID, "http://localhost:8080");
        assertNotNull(qr.payload());
        assertEquals(ReportTestData.DEMO_CARD, qr.payload().card());
        assertNotNull(qr.scanUrl());
        assertTrue(qr.scanUrl().contains("token="));
        assertEquals("ACTIVE", qr.display().get("status"));
    }

    @Test
    @DisplayName("Ф5 — Увеличение QR (данные для полноэкранного режима)")
    void f5_qrContainsScanUrl() {
        var qr = cardService.getQrForUser(ReportTestData.DEMO_USER_ID, "http://localhost:8080");
        assertTrue(qr.scanUrl().contains("/card/ticket.html"));
        assertNotNull(qr.payload().sig());
    }

    @Test
    @DisplayName("Ф6 — Информация о читателе")
    void f6_ticketInfoFromQrToken() {
        var qr = cardService.getQrForUser(ReportTestData.DEMO_USER_ID, "http://localhost:8080");
        String token = qr.scanUrl().substring(qr.scanUrl().indexOf("token=") + 6);
        var view = cardService.resolveQrToken(token);
        assertTrue(view.fullName().contains("Иванов"));
        assertEquals(ReportTestData.DEMO_CARD, view.cardNumber());
        assertEquals("ACTIVE", view.status());
        assertFalse(view.validUntil().isBlank());
    }

    @Test
    @DisplayName("Ф7 — Поиск в каталоге")
    void f7_catalogSearchTolstoy() {
        var books = catalogService.listBooks("Толстой", null, "title");
        assertFalse(books.isEmpty());
        assertTrue(books.stream().anyMatch(b -> b.authorName().contains("Толстой")));
    }

    @Test
    @DisplayName("Ф8 — Продление выдачи")
    void f8_renewLoan() {
        BookDtos.RenewResponse renew = loanService.renewLoan(
                ReportTestData.DEMO_USER_ID, ReportTestData.ACTIVE_LOAN_ID);
        assertTrue(renew.renewalCount() >= 1);
        assertNotNull(renew.newDueDate());
    }

    @Test
    @DisplayName("Ф9 — Запись на событие")
    void f9_registerForEvent() {
        EventDtos.RegisterResponse response = eventService.register(
                ReportTestData.DEMO_USER_ID, ReportTestData.EVENT_ID);
        assertTrue(response.registered());
        var events = eventService.listEvents(ReportTestData.DEMO_USER_ID);
        assertTrue(events.stream()
                .filter(e -> e.id().equals(ReportTestData.EVENT_ID.toString()))
                .anyMatch(EventDtos.EventItem::registeredByMe));
        eventService.unregister(ReportTestData.DEMO_USER_ID, ReportTestData.EVENT_ID);
    }

    @Test
    @DisplayName("Ф10 — Регистрация читателя")
    void f10_fullRegistrationFlow() {
        String email = "reader" + System.nanoTime() + "@test.local";
        String phone = "+7901" + (System.nanoTime() % 1_000_0000L);
        var created = registrationService.createRequest(new RegistrationDtos.NewRegistrationRequest(
                "Новиков", "Николай", "Петрович",
                LocalDate.of(1998, 3, 15),
                "4010", "123456", "",
                phone, email));
        UUID requestId = UUID.fromString(created.requestId());
        var paid = registrationService.mockPay(requestId);
        assertEquals("SUCCESS", paid.paymentStatus());
        assertNotNull(paid.cardNumber());
        var completed = registrationService.completeRegistration(requestId,
                new RegistrationDtos.CompleteRegistrationRequest("Reader1234", "Reader1234"));
        assertNotNull(completed.accessToken());
        assertNotNull(completed.user().cardNumber());
    }
}
