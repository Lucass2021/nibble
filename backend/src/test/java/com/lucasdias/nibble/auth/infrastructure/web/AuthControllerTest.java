package com.lucasdias.nibble.auth.infrastructure.web;

import com.lucasdias.nibble.auth.application.MagicLinkService;
import com.lucasdias.nibble.auth.domain.InvalidLoginException;
import com.lucasdias.nibble.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MagicLinkService magicLinkService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void verifyCode_returns401WhenCodeIsWrong() throws Exception {
        when(magicLinkService.verifyCode("lucas@email.com", "123123")).thenThrow(InvalidLoginException.class);

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "lucas@email.com", "code": "123123" }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_LOGIN"));
    }
}