package com.lucasdias.nibble.auth.infrastructure.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lucasdias.nibble.auth.application.MagicLinkService;
import com.lucasdias.nibble.auth.domain.InvalidLoginException;
import com.lucasdias.nibble.auth.infrastructure.security.JwtService;
import com.lucasdias.nibble.common.infrastructure.config.SecurityConfig;
import com.lucasdias.nibble.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {
    private static final String EMAIL = "lucas@email.com";
    private static final String CODE = "123123";
    private static final String JWT = "fake-jwt";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MagicLinkService magicLinkService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void verifyCode_returns401WhenCodeIsWrong() throws Exception {
        when(magicLinkService.verifyCode(EMAIL, CODE)).thenThrow(InvalidLoginException.class);

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "code": "%s" }
                                """.formatted(EMAIL, CODE)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_LOGIN"));
    }

    @Test
    void verifyCode_returns400WhenCodeIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "code": "1234567" }
                                """.formatted(EMAIL)))
                .andExpect(status().isBadRequest());

        verify(magicLinkService, never()).verifyCode(anyString(), anyString());
    }

    @Test
    void verifyCode_returnsJwtWhenCodeIsRight() throws Exception {
        User user = new User(EMAIL);

        when(magicLinkService.verifyCode(EMAIL, CODE)).thenReturn(user);
        when(jwtService.generate(user)).thenReturn(JWT);

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "code": "%s" }
                                """.formatted(EMAIL, CODE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(JWT));
    }
}
