package com.thecookiezen.recoverai;

import com.embabel.agent.api.event.AgenticEventListener;
import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.api.event.AgentProcessEvent;
import com.embabel.agent.api.event.AgentProcessCompletedEvent;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;
import com.thecookiezen.archiledger.agenticmemory.domain.UpsertMemoryRequest;
import com.thecookiezen.archiledger.domain.model.MemoryNote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class MemoryAgentEventListener implements AgenticEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MemoryAgentEventListener.class);

    private final AgentPlatform agentPlatform;

    public MemoryAgentEventListener(@Lazy AgentPlatform agentPlatform) {
        this.agentPlatform = agentPlatform;
        logger.info("MemoryAgentEventListener registered");
    }

    @Override
    public void onProcessEvent(AgentProcessEvent event) {
        if (event instanceof AgentProcessCompletedEvent completedEvent) {
            try {
                var result = completedEvent.getResult();
                
                if (result instanceof String s) {
                    logger.info("Triggering memory storage for process: {}", event.getProcessId());

                    var invocation = AgentInvocation
                        .builder(agentPlatform)
                        .options(new ProcessOptions()
                            .withVerbosity(new Verbosity()
                                .withShowPrompts(true)
                                .withShowLlmResponses(true)
                                .withDebug(true)))
                        .build(MemoryNote.class);

                    var newNote = invocation.invoke(new UpsertMemoryRequest(s));
                    logger.debug("Memory created: {}", newNote.toString());
                }
            } catch (Exception e) {
                logger.error("Error triggering memory storage: {}", e.getMessage(), e);
            }
        }
    }

}
