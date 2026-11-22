package com.lucasdias.nibble.topic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeneratedTopic (
        @NotNull @Size(min = 5, max = 5) List<@Valid GeneratedCard> cards,
        @NotNull @Size(min = 3, max = 3) List<@Valid GeneratedQuestion> questions
) { }