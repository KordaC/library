package com.example.library.support;

import com.example.library.dto.AuthDtos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ReportTestSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ReportTestSupport() {}

    public static String loginAndGetToken(MockMvc mockMvc, String login) throws Exception {
        String body = """
                {"login":"%s","password":"%s"}
                """.formatted(login, ReportTestData.DEMO_PASSWORD);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = MAPPER.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("accessToken").asText();
    }

    public static AuthDtos.LoginResponse login(AuthDtos.LoginRequest request,
                                               com.example.library.service.AuthService authService) {
        return authService.login(request);
    }
}
