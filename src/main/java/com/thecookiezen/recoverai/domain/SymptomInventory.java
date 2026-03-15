package com.thecookiezen.recoverai.domain;

import java.util.List;
import java.util.Map;

public record SymptomInventory(
    Map<SymptomCategory, List<SymptomResponse>> responses
) {
    public record SymptomResponse(
        String question,
        String answer,
        int severityRating
    ) {}
    
    public List<SymptomResponse> getAllResponses() {
        return responses.values().stream()
            .flatMap(List::stream)
            .toList();
    }
    
    public List<String> getObservations() {
        return getAllResponses().stream()
            .map(r -> r.question() + ": " + r.answer() + " (Severity: " + r.severityRating + "/5)")
            .toList();
    }
    
    public double calculateOverallSeverity() {
        return getAllResponses().stream()
            .mapToInt(SymptomResponse::severityRating)
            .average()
            .orElse(0.0) * 2.0;
    }
}
