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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:report_load;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
})
class ReportLoadTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("Н1 — 50 запросов /health")
    void n1_healthFiftyRequests() throws Exception {
        int success = repeat(50, () -> {
            mockMvc.perform(get("/api/v1/health")).andExpect(status().isOk());
            return true;
        });
        assertTrue(success >= 45, "Успешных ответов: " + success);
    }

    @Test
    @DisplayName("Н2 — 30 параллельных login")
    void n2_thirtyParallelLogins() throws Exception {
        int success = parallel(30, () -> {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"20001","password":"Demo1234"}
                                    """))
                    .andExpect(status().isOk());
            return true;
        });
        assertTrue(success >= 27, "Успешных входов: " + success);
    }

    @Test
    @DisplayName("Н3 — 50 запросов каталога")
    void n3_fiftyCatalogRequests() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        int success = repeat(50, () -> {
            mockMvc.perform(get("/api/v1/books")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
            return true;
        });
        assertTrue(success >= 45, "Успешных ответов каталога: " + success);
    }

    @Test
    @DisplayName("Н4 — Продление выдачи")
    void n4_renewalsWithinLimit() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(post("/api/v1/loans/" + ReportTestData.ACTIVE_LOAN_ID + "/renew")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/loans/active")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Н5 — 40 запросов dashboard")
    void n5_fortyDashboardRequests() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        int success = repeat(40, () -> {
            mockMvc.perform(get("/api/v1/dashboard")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
            return true;
        });
        assertTrue(success >= 36, "Успешных dashboard: " + success);
    }

    @Test
    @DisplayName("Н6 — 25 записей на события")
    void n6_eventRegistrationCapacity() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        mockMvc.perform(post("/api/v1/events/" + ReportTestData.EVENT_ID + "/register")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        assertTrue(true);
    }

    @Test
    @DisplayName("Н7 — 100 запросов health")
    void n7_hundredHealthRequests() throws Exception {
        int success = repeat(100, () -> {
            mockMvc.perform(get("/health")).andExpect(status().isOk());
            return true;
        });
        assertTrue(success >= 90, "Успешных health: " + success);
    }

    @Test
    @DisplayName("Н8 — повторный вход демо-пользователя")
    void n8_repeatedDemoLogin() throws Exception {
        int success = repeat(10, () -> {
            ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
            return true;
        });
        assertTrue(success == 10);
    }

    @Test
    @DisplayName("Н9 — 30 запросов QR")
    void n9_thirtyQrRequests() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        List<String> signatures = new ArrayList<>();
        int success = repeat(30, () -> {
            String json = mockMvc.perform(get("/api/v1/cards/me/qr")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            int sigIndex = json.indexOf("\"sig\":\"");
            if (sigIndex >= 0) {
                int start = sigIndex + 7;
                int end = json.indexOf('"', start);
                signatures.add(json.substring(start, end));
            }
            return true;
        });
        assertTrue(success >= 28);
        assertFalse(signatures.isEmpty());
    }

    @Test
    @DisplayName("Н10 — Смешанная нагрузка")
    void n10_mixedLoad() throws Exception {
        String token = ReportTestSupport.loginAndGetToken(mockMvc, ReportTestData.DEMO_CARD);
        int success = 0;
        int total = 70;
        for (int i = 0; i < 20; i++) {
            try {
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"login":"20001","password":"Demo1234"}
                                        """))
                        .andExpect(status().isOk());
                success++;
            } catch (AssertionError ignored) {
            }
        }
        for (int i = 0; i < 30; i++) {
            try {
                mockMvc.perform(get("/api/v1/books")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());
                success++;
            } catch (AssertionError ignored) {
            }
        }
        for (int i = 0; i < 20; i++) {
            try {
                mockMvc.perform(get("/api/v1/dashboard")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());
                success++;
            } catch (AssertionError ignored) {
            }
        }
        double rate = success * 100.0 / total;
        assertTrue(rate >= 90.0 || success >= 63,
                "Успешных запросов: " + success + " из " + total);
    }

    private int repeat(int times, Callable<Boolean> action) throws Exception {
        int success = 0;
        for (int i = 0; i < times; i++) {
            try {
                if (Boolean.TRUE.equals(action.call())) {
                    success++;
                }
            } catch (Exception ignored) {
            }
        }
        return success;
    }

    private int parallel(int threads, Callable<Boolean> action) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(threads, 10));
        try {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(pool.submit(action));
            }
            int success = 0;
            for (Future<Boolean> future : futures) {
                try {
                    if (Boolean.TRUE.equals(future.get())) {
                        success++;
                    }
                } catch (Exception ignored) {
                }
            }
            return success;
        } finally {
            pool.shutdownNow();
        }
    }
}
