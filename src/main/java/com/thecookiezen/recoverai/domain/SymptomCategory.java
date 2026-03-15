package com.thecookiezen.recoverai.domain;

public enum SymptomCategory {
    DELUSION_OF_VELOCITY(
        "Delusion of Velocity",
        "The Speed Trap - Obsession with AI-generated output metrics"
    ),
    HALLUCINATORY_RESOURCE_PLANNING(
        "Hallucinatory Resource Planning", 
        "Belief that AI replaces human expertise and capacity"
    ),
    REALITY_DETACHMENT(
        "Reality Detachment",
        "Disconnection from actual costs, outcomes, and accountability"
    );

    private final String title;
    private final String description;

    SymptomCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
