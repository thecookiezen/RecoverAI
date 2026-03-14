package com.thecookiezen.recoverai;

import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.EmbabelComponent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.chat.Conversation;
import com.embabel.chat.UserMessage;

@EmbabelComponent
public class RecoverAiAgent {

    private final RecoverAiProperties properties;
    
    public RecoverAiAgent(RecoverAiProperties properties) {
        this.properties = properties;
    }

    @Action(canRerun = true, trigger = UserMessage.class)
    void responde(Conversation conversation, ActionContext context) {
        var assistantMessage = context.ai()
                .withLlm(properties.chatLlm())
                .withSystemPrompt("You are a helpful financial analyst.")
                .respond(conversation.getMessages());
        context.sendMessage(conversation.addMessage(assistantMessage));
    }

}
