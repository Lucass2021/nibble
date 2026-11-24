package com.lucasdias.nibble.common.infrastructure.web;

import com.lucasdias.nibble.auth.domain.InvalidLoginException;
import com.lucasdias.nibble.auth.domain.TooManyLoginRequestsException;
import com.lucasdias.nibble.topic.domain.InvalidLlmKeyException;
import com.lucasdias.nibble.topic.domain.LlmResponseException;
import com.lucasdias.nibble.topic.domain.LlmUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public ProblemDetail handleInvalidLogin(InvalidLoginException error) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, error.getMessage());
        problem.setProperty("code", "INVALID_LOGIN");
        return problem;
    }

    @ExceptionHandler(LlmResponseException.class)
    public ProblemDetail handleLlmResponse(LlmResponseException error) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, error.getMessage());
        problem.setProperty("code", "LLM_INVALID_RESPONSE");
        return problem;
    }

    @ExceptionHandler(InvalidLlmKeyException.class)
    public ProblemDetail handleInvalidLlmKey(InvalidLlmKeyException error) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
        problem.setProperty("code", "LLM_INVALID_KEY");
        return problem;
    }

    @ExceptionHandler(LlmUnavailableException.class)
    public ProblemDetail handleLlmUnavailable(LlmUnavailableException error) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.GATEWAY_TIMEOUT, error.getMessage());
        problem.setProperty("code", "LLM_UNAVAILABLE");
        return problem;
    }

    @ExceptionHandler(TooManyLoginRequestsException.class)
    public ProblemDetail handleTooManyLoginRequests(TooManyLoginRequestsException error) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, error.getMessage());
        problem.setProperty("code", "TOO_MANY_LOGIN_REQUESTS");
        return problem;
    }
}
