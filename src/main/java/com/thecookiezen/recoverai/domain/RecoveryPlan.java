package com.thecookiezen.recoverai.domain;

import java.util.List;

public record RecoveryPlan(String strategy, List<String> steps, String managementCommunicationScript) {
    
}
