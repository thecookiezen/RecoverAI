package com.thecookiezen.recoverai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.rag.tools.ToolishRag;
import com.thecookiezen.recoverai.domain.Assessment;
import com.thecookiezen.recoverai.domain.RecoveryPlan;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire.QuestionnaireResult;
import com.thecookiezen.recoverai.rag.MemoryNoteRetrievable;
import com.thecookiezen.recoverai.rag.MemoryNoteSearchOperations;

@Agent(description = "Helpful assistant that diagnose and recover from 'AI psychosis' - the organizational delusion that AI can solve all problems without proper processes")
public class RecoverAiAgent {

    private final RecoverAiProperties properties;
    private final ToolishRag memoryRag;

    public RecoverAiAgent(RecoverAiProperties properties, MemoryNoteSearchOperations memoryNoteSearchOperations) {
        this.properties = properties;
        this.memoryRag = new ToolishRag(
                "memory-notes",
                "Knowledge graph memories relevant to AI psychosis diagnosis and recovery patterns",
                memoryNoteSearchOperations)
            .withSearchFor(List.of(MemoryNoteRetrievable.class));
    }

    @Action
    Assessment diagnose(QuestionnaireResult questionnaireResult, Ai ai) {
        return ai.withLlm(properties.chatLlm())
            .withReference(memoryRag)
            .rendering("diagnostician/diagnose")
            .createObject(Assessment.class, Map.of(
                    "observations", questionnaireResult.inventory().getObservations()
            ));
    }

    @Action
    public RecoveryPlan formulateRecoveryPlan(Assessment diagnosis, QuestionnaireResult questionnaireResult, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("userRole", questionnaireResult.userRole());
        return ai.withLlm(properties.chatLlm())
            .rendering("strategist/recovery_plan")
            .createObject(RecoveryPlan.class, model);
    }

    @Action
    @AchievesGoal(description = "Generate a diplomatic communication script to address AI psychosis in an organization",
        export = @Export(
                    remote = true,
                    name = "recoverai-diagnose",
                    startingInputTypes = {QuestionnaireResult.class})
    )
    public String generateDiplomaticScript(Assessment diagnosis, RecoveryPlan plan, QuestionnaireResult result, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("plan", plan);
        model.put("targetDiscipline", result.discipline().name().toLowerCase());
        return ai.withLlm(properties.chatLlm())
            .rendering("diplomat/communication")
            .generateText(model);
    }
}
