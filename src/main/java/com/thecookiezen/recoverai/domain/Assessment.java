package com.thecookiezen.recoverai.domain;

public record Assessment(PsychosisStage stage, Discipline discipline, String symptoms, double severityScore) {
    
}
