package com.thecookiezen.recoverai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.Verbosity;
import com.embabel.chat.Chatbot;
import com.embabel.chat.agent.AgentProcessChatbot;
import com.embabel.chat.support.InMemoryConversationFactory;

@Configuration
public class ChatConfiguration {
    @Bean
    Chatbot chatbot(AgentPlatform agentPlatform) {
        return AgentProcessChatbot.utilityFromPlatform(
                agentPlatform,
                new InMemoryConversationFactory(),
                new Verbosity().showPrompts()
        );
    }
}
