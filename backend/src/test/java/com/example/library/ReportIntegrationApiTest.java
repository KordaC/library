package com.example.library;

import com.example.library.support.ReportTestData;
import com.example.library.support.ReportTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:report_integration;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
})
class ReportIntegrationApiTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("И1 — Клиент — Auth API")
    void i1_authApiReturnsJwt() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"20001","password":"Demo1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.cardNumber").value("20001"));
    }

    @Test
    @DisplayName("И2 — Клиент — Dashboard API")
    void i2_dashboardApi() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.card.number").value("20001"))
                .andExpect(jsonPath("$.data.user.fullName").value(notNullValue()))
                .andExpect(jsonPath("$.data.loans").exists())
                .andExpect(jsonPath("$.data.notifications").isArray());
    }

    @Test
    @DisplayName("И3 — Клиент — QR API")
    void i3_qrApi() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(get("/api/v1/cards/me/qr")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.payload.card").value("20001"))
                .andExpect(jsonPath("$.data.scanUrl").value(containsString("token=")));
    }

    @Test
    @DisplayName("И4 — Deep link QR (проверка токена)")
    void i4_qrTokenResolvesToReader() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        String qrJson = mockMvc.perform(get("/api/v1/cards/me/qr")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String qrToken = qrJson.substring(qrJson.indexOf("token=") + 6);
        int amp = qrToken.indexOf('&');
        if (amp > 0) {
            qrToken = qrToken.substring(0, amp);
        }
        int quote = qrToken.indexOf('"');
        if (quote > 0) {
            qrToken = qrToken.substring(0, quote);
        }
        mockMvc.perform(get("/api/v1/public/ticket").param("token", qrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value(containsString("Иванов")))
                .andExpect(jsonPath("$.data.cardNumber").value("20001"));
    }

    @Test
    @DisplayName("И5 — Public ticket API")
    void i5_publicTicketApi() throws Exception {
        String authToken = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        String qrJson = mockMvc.perform(get("/api/v1/cards/me/qr")
                        .header("Authorization", "Bearer " + authToken))
                .andReturn().getResponse().getContentAsString();
        String qrToken = extractTokenFromJson(qrJson);
        mockMvc.perform(get("/api/v1/public/ticket").param("token", qrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("И6 — Каталог — жанры")
    void i6_catalogByGenre() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(get("/api/v1/books")
                        .param("genreId", ReportTestData.CLASSICS_GENRE_ID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    @DisplayName("И7 — Продление — обновление уведомлений")
    void i7_notificationsAfterRenew() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(post("/api/v1/loans/" + ReportTestData.ACTIVE_LOAN_ID + "/renew")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notifications").isArray());
    }

    @Test
    @DisplayName("И8 — События — запись/отмена")
    void i8_eventRegisterAndCancel() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        UUID eventId = ReportTestData.EVENT_ID;
        mockMvc.perform(post("/api/v1/events/" + eventId + "/register")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.registered").value(true));
        mockMvc.perform(delete("/api/v1/events/" + eventId + "/register")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("И9 — Регистрация — mock-pay")
    void i9_registrationMockPay() throws Exception {
        String email = "api" + System.nanoTime() + "@test.local";
        String phone = "+7902" + (System.nanoTime() % 1_000_0000L);
        String createBody = """
                {
                  "lastName":"Смирнов",
                  "firstName":"Сергей",
                  "middleName":"",
                  "birthDate":"1995-06-20",
                  "passportSeries":"4011",
                  "passportNumber":"654321",
                  "address":"",
                  "phone":"%s",
                  "email":"%s"
                }
                """.formatted(phone, email);
        String createResponse = mockMvc.perform(post("/api/v1/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String requestId = createResponse.replaceAll(".*\"requestId\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        mockMvc.perform(post("/api/v1/registration/new/" + requestId + "/mock-pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.data.cardNumber").isNotEmpty());
    }

    @Test
    @DisplayName("И10 — Health API (доступность сервера)")
    void i10_healthEndpointAvailable() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ok"));
    }

    private static String extractTokenFromJson(String qrJson) {
        int start = qrJson.indexOf("token=") + 6;
        int end = qrJson.indexOf('"', start);
        if (end < 0) {
            end = qrJson.length();
        }
        return qrJson.substring(start, end);
    }
}
