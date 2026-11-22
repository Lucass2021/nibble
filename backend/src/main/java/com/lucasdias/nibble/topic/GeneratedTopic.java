package com.lucasdias.nibble.topic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeneratedTopic (
        @NotNull @Size(min = CARD_COUNT, max = CARD_COUNT) List<@Valid GeneratedCard> cards,
        @NotNull @Size(min = QUESTION_COUNT, max = QUESTION_COUNT) List<@Valid GeneratedQuestion> questions
) {
    public static final int CARD_COUNT = 5;
    public static final int QUESTION_COUNT = 3;
}