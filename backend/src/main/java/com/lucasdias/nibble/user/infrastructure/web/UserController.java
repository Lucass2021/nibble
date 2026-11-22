package com.lucasdias.nibble.user.infrastructure.web;

import com.lucasdias.nibble.user.domain.User;
import com.lucasdias.nibble.user.domain.UserRepository;
import com.lucasdias.nibble.user.infrastructure.web.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return new MeResponse(user.getId(), user.getEmail());
    }

}