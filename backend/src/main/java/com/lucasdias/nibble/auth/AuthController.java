package com.lucasdias.nibble.auth;

import com.lucasdias.nibble.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MagicLinkService magicLinkService;
    private final JwtService jwtService;

    @PostMapping("/auth/magic-link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestMagicLink(@Valid @RequestBody MagicLinkRequest request) {
         magicLinkService.requestLink(request.email());
    }

    @PostMapping("/auth/verify-token")
    public AuthResponse verifyToken(@Valid @RequestBody VerifyTokenRequest request){
        User user = magicLinkService.verifyToken(request.token());
        String jwt = jwtService.generate(user);
        return new AuthResponse(jwt);
    }

    @PostMapping("/auth/verify-code")
    public AuthResponse verifyCode(@Valid @RequestBody VerifyCodeRequest request){
        User user = magicLinkService.verifyCode(request.email(), request.code());
        String jwt = jwtService.generate(user);
        return new AuthResponse(jwt);
    }

}