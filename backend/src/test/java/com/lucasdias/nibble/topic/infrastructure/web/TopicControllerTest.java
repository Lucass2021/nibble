package com.lucasdias.nibble.topic.infrastructure.web;

import com.lucasdias.nibble.auth.infrastructure.security.JwtService;
import com.lucasdias.nibble.common.infrastructure.config.SecurityConfig;
import com.lucasdias.nibble.topic.domain.LlmClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
@Import(SecurityConfig.class)
class TopicControllerTest {
    private static final String JWT = "fake-jwt";
    private static final String API_KEY = "sk-test";
    private static final String TITLE = "Kotlin";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private LlmClient llmClient;

    @Test
    void create_returns401WithoutJwt() throws Exception {
        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-OpenRouter-Key", API_KEY)
                        .content("""
                                { "title": "%s" }
                                """.formatted(TITLE)))
                .andExpect(status().isUnauthorized());

        verify(llmClient, never()).generate(anyString(), anyString(), anyString());
    }

    @Test
    void create_returnsAcceptedWithValidJwt() throws Exception {
        when(jwtService.parseUserId(JWT)).thenReturn(1L);

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-OpenRouter-Key", API_KEY)
                        .header("Authorization", "Bearer " + JWT)
                        .content("""
                                { "title": "%s" }
                                """.formatted(TITLE)))
                .andExpect(status().isAccepted());

        verify(llmClient).generate(TITLE, "en", API_KEY);
    }
}