package com.lucasdias.nibble.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MagicLinkService magicLinkService;

    @PostMapping("/auth/magic-link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestMagicLink(@Valid @RequestBody MagicLinkRequest request) {
         magicLinkService.requestLink(request.email());
    }
}