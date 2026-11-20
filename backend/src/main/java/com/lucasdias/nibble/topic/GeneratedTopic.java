package com.lucasdias.nibble.topic;

import java.util.List;

public record GeneratedTopic (
        List<GeneratedCard> cards,
        List<GeneratedQuestion> questions
) { }