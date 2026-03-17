package com.thecookiezen.recoverai.shell;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;
import com.embabel.agent.api.channel.MessageOutputChannelEvent;
import com.embabel.agent.api.channel.OutputChannel;
import com.embabel.agent.api.channel.OutputChannelEvent;
import com.embabel.chat.AssistantMessage;
import com.embabel.chat.Message;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire;

@ShellComponent
class RecoverAiShell extends AbstractShellComponent {
    
    private final AgentPlatform agentPlatform;;
    private final IntakeQuestionnaire questionnaire;

    BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(10);
    OutputChannel outputChannel = new QueueingOutputChannel(queue);

    public RecoverAiShell(
            AgentPlatform agentPlatform,
            IntakeQuestionnaire questionnaire
    ) {
        this.agentPlatform = agentPlatform;
        this.questionnaire = questionnaire;
    }

    @ShellMethod("Run the full RecoverAI diagnostic workflow")
    public String diagnose() {
        var terminal = getTerminal();
        var writer = terminal.writer();

        try {
            var style = new AttributedStyle().bold().foreground(AttributedStyle.CYAN);
            writer.println(new AttributedString("\n+==========================================================+", style).toAnsi(terminal));
            writer.println(new AttributedString("|          RecoverAI - Organizational Diagnosis           |", style).toAnsi(terminal));
            writer.println(new AttributedString("+==========================================================+\n", style).toAnsi(terminal));
            writer.flush();

            var result = questionnaire.runQuestionnaire(terminal);
            var inventory = result.inventory();

            
            var invocation = AgentInvocation
                .builder(agentPlatform)
                .options(new ProcessOptions()
                    .withVerbosity(new Verbosity()
                        .withShowPrompts(true)
                        .withShowLlmResponses(true)
                        .withDebug(true)))
                .build(String.class);

            return invocation.invoke(Map.of(
                "request", inventory)
            );

        } catch (Exception e) {
            return "Error during diagnosis: " + e.getMessage();
        }
    }

    private record QueueingOutputChannel(BlockingQueue<Message> queue) implements OutputChannel {
        @Override
        public void send(OutputChannelEvent event) {
            if (event instanceof MessageOutputChannelEvent msgEvent) {
                var msg = msgEvent.getMessage();
                if (msg instanceof AssistantMessage) {
                    queue.offer(msg);
                }
            }
        }
    }
}