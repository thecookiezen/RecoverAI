package com.thecookiezen.recoverai.domain;

public enum PsychosisStage {
    PRODROMAL("Stage 1: Unusual interest, AI-first hype, mild detachment from reality."),
    ACUTE("Stage 2: Hallucinations. Managers pushing AI code to prod without PRs."),
    RESIDUAL("Stage 3: The crash. Tech debt, broken production, team burnout/cynicism.");

    public final String description;

    private PsychosisStage(String description) {
        this.description = description;
    }
}
