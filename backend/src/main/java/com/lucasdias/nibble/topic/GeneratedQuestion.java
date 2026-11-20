package com.lucasdias.nibble.topic;

import java.util.List;

public record GeneratedQuestion (
        String statement,
        List<String> options,
        int correctOption,
        String explanation
) { }