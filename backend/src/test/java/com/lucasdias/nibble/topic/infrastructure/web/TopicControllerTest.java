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
                        .header("X-OpenRouter-Key", "sk-test")
                        .content("""
                                { "title": "Kotlin" }
                                """))
                .andExpect(status().isUnauthorized());

        verify(llmClient, never()).generate(anyString(), anyString(), anyString());
    }


    @Test
    void create_returnsAcceptedWithValidJwt() throws Exception {
        when(jwtService.parseUserId("fake-jwt")).thenReturn(1L);

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-OpenRouter-Key", "sk-test")
                        .header("Authorization", "Bearer fake-jwt")
                        .content("""
                                { "title": "Kotlin" }
                                """))
                .andExpect(status().isAccepted());

        verify(llmClient).generate("Kotlin", "en", "sk-test");
    }
}