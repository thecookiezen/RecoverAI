package com.thecookiezen.recoverai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.EmbabelComponent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.agent.api.common.Ai;
import com.embabel.chat.Conversation;
import com.embabel.chat.UserMessage;
import com.thecookiezen.recoverai.domain.Assessment;
import com.thecookiezen.recoverai.domain.Discipline;
import com.thecookiezen.recoverai.domain.RecoveryPlan;

@Agent
public class RecoverAiAgent {

    private final RecoverAiProperties properties;
    
    public RecoverAiAgent(RecoverAiProperties properties) {
        this.properties = properties;
    }

    @Action(canRerun = true, trigger = UserMessage.class)
    void respond(Conversation conversation, ActionContext context) {
        var assistantMessage = context.ai()
                .withLlm(properties.chatLlm())
                .withSystemPrompt("You are RecoverAI, a helpful assistant that helps organizations diagnose and recover from 'AI psychosis' - the organizational delusion that AI can solve all problems without proper processes.")
                .respond(conversation.getMessages());
        context.sendMessage(conversation.addMessage(assistantMessage));
    }

    @Action
    public Assessment diagnose(List<String> observations, Discipline discipline, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("observations", observations);
        return ai.withDefaultLlm()
            .rendering("diagnostician/diagnose")
            .createObject(Assessment.class, model);
    }

    @Action
    public RecoveryPlan formulateRecoveryPlan(Assessment diagnosis, String userRole, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("userRole", userRole);
        return ai.withDefaultLlm()
            .rendering("strategist/recovery_plan")
            .createObject(RecoveryPlan.class, model);
    }

    @Action
    public String generateDiplomaticScript(Assessment diagnosis, RecoveryPlan plan, Discipline targetDiscipline, Ai ai) {
        Map<String, Object> model = new HashMap<>();
        model.put("diagnosis", diagnosis);
        model.put("plan", plan);
        model.put("targetDiscipline", targetDiscipline);
        return ai.withDefaultLlm()
            .rendering("diplomat/communication")
            .generateText(model);
    }
}
