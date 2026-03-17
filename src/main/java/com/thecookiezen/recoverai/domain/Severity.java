package com.thecookiezen.recoverai.domain;

import java.util.Arrays;

public enum Severity {
    NOT_AT_ALL(1, "Not at all"),
    RARELY(2, "Rarely"),
    SOMETIMES(3, "Sometimes"),
    OFTEN(4, "Often"),
    VERY_FREQUENTLY(5, "Very frequently");

    private final int value;
    private final String label;

    Severity(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayText() {
        return value + " - " + label;
    }

    public static Severity fromValue(int value) {
        return Arrays.stream(values())
            .filter(s -> s.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid severity value: " + value));
    }
}
