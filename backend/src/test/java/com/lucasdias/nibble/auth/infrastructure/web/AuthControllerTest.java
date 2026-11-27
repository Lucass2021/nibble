package com.lucasdias.nibble.auth.infrastructure.web;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
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

    @Test
    void verifyCode_returns400WhenCodeIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "lucas@email.com", "code": "1234567" }
                                """))
                .andExpect(status().isBadRequest());

        verify(magicLinkService, never()).verifyCode(anyString(), anyString());
    }

    @Test
    void verifyCode_returnsJwtWhenCodeIsRight() throws Exception {
        User user = new User("lucas@email.com");

        when(magicLinkService.verifyCode("lucas@email.com", "123123")).thenReturn(user);
        when(jwtService.generate(user)).thenReturn("fake-jwt");

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "lucas@email.com", "code": "123123" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("fake-jwt"));
    }
}