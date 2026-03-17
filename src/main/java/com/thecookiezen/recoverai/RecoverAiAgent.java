package com.thecookiezen.recoverai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.Ai;
import com.thecookiezen.recoverai.domain.Assessment;
import com.thecookiezen.recoverai.domain.Discipline;
import com.thecookiezen.recoverai.domain.RecoveryPlan;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire.QuestionnaireResult;

@Agent(description = "Helpful assistant that diagnose and recover from 'AI psychosis' - the organizational delusion that AI can solve all problems without proper processes")
public class RecoverAiAgent {

    private final RecoverAiProperties properties;
    
    public RecoverAiAgent(RecoverAiProperties properties) {
        this.properties = properties;
    }

    @Action
    Assessment diagnose(QuestionnaireResult questionnaireResult, Ai ai) {
        return ai.withLlm(properties.chatLlm())
            .rendering("diagnostician/diagnose")
            .createObject(Assessment.class, Map.of(
                    "observations", questionnaireResult.inventory().getAllResponses()
            ));
    }

    @Action
    public RecoveryPlan formulateRecoveryPlan(Assessment diagnosis, QuestionnaireResult questionnaireResult, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("userRole", questionnaireResult.userRole());
        return ai.withDefaultLlm()
            .rendering("strategist/recovery_plan")
            .createObject(RecoveryPlan.class, model);
    }

    @Action
    @AchievesGoal(description = "", 
        export = @Export(
                    remote = true,
                    name = "fixme",
                    startingInputTypes = {QuestionnaireResult.class})
    )
    public String generateDiplomaticScript(Assessment diagnosis, RecoveryPlan plan, QuestionnaireResult result, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("plan", plan);
        model.put("targetDiscipline", result.discipline().toString());
        return ai.withDefaultLlm()
            .rendering("diplomat/communication")
            .generateText(model);
    }
}
