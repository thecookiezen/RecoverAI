package com.thecookiezen.recoverai.shell;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jline.utils.AttributedString;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.embabel.agent.api.identity.User;
import com.embabel.agent.api.channel.MessageOutputChannelEvent;
import com.embabel.agent.api.channel.OutputChannel;
import com.embabel.agent.api.channel.OutputChannelEvent;
import com.embabel.agent.api.identity.SimpleUser;
import com.embabel.chat.AssistantMessage;
import com.embabel.chat.Chatbot;
import com.embabel.chat.Message;
import com.embabel.chat.UserMessage;
import com.thecookiezen.recoverai.domain.Assessment;
import com.thecookiezen.recoverai.domain.Discipline;
import com.thecookiezen.recoverai.domain.RecoveryPlan;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire;

@ShellComponent
class RecoverAiShell extends AbstractShellComponent {
    
    private static final User ANONYMOUS_USER = new SimpleUser(
            "anonymous",
            "Anonymous User",
            "anonymous",
            null
    );

    private final Chatbot chatbot;
    private final IntakeQuestionnaire questionnaire;

    public RecoverAiShell(
            Chatbot chatbot,
            IntakeQuestionnaire questionnaire
    ) {
        this.chatbot = chatbot;
        this.questionnaire = questionnaire;
    }

    @ShellMethod("Talk with LLM")
    public String ask(@ShellOption({"--message"}) String ask) throws Exception {
        var userMessage = new UserMessage(ask);

        var queue = new ArrayBlockingQueue<Message>(10);
        var outputChannel = new QueueingOutputChannel(queue);
        var session = chatbot.createSession(ANONYMOUS_USER, outputChannel, UUID.randomUUID().toString(), null);
        session.onUserMessage(userMessage);

        var response = queue.poll(60, TimeUnit.SECONDS);
        return response.getContent();
    }

    @ShellMethod("Run the full RecoverAI diagnostic workflow")
    public String diagnose() {
        var terminal = getTerminal();
        var writer = terminal.writer();

        try {
            writer.println(AttributedString.fromAnsi("\n@|bold,cyan ╔══════════════════════════════════════════════════════════╗@|@").toAnsi());
            writer.println(AttributedString.fromAnsi("@|bold,cyan ║          RecoverAI - Organizational Diagnosis           ║@|@").toAnsi());
            writer.println(AttributedString.fromAnsi("@|bold,cyan ╚══════════════════════════════════════════════════════════╝@|@\n").toAnsi());
            writer.flush();

            var result = questionnaire.runQuestionnaire();
            var inventory = result.inventory();

            writer.println(AttributedString.fromAnsi("\n@|bold,green === Running Diagnosis ===|@").toAnsi());
            writer.flush();

            var observations = inventory.getObservations();
            Assessment assessment = diagnostician.diagnose(observations, null);

            writer.println(AttributedString.fromAnsi("\n@|bold,white Diagnosis Results:|@").toAnsi());
            writer.println(AttributedString.fromAnsi("  @|yellow Stage:|@ " + assessment.stage()).toAnsi());
            writer.println(AttributedString.fromAnsi("  @|yellow Severity:|@ " + String.format("%.1f/10", assessment.severityScore())).toAnsi());
            writer.println(AttributedString.fromAnsi("  @|yellow Symptoms:|@ " + assessment.symptoms()).toAnsi());
            writer.flush();

            writer.println(AttributedString.fromAnsi("\n@|bold,green === Formulating Recovery Plan ===|@").toAnsi());
            writer.flush();

            RecoveryPlan plan = strategist.formulatePlan(assessment, result.userRole(), null);

            writer.println(AttributedString.fromAnsi("\n@|bold,white Recovery Strategy:|@ " + plan.strategy()).toAnsi());
            writer.println(AttributedString.fromAnsi("@|bold,white Tactical Steps:|@").toAnsi());
            for (int i = 0; i < plan.steps().size(); i++) {
                writer.println(AttributedString.fromAnsi("  " + (i + 1) + ". " + plan.steps().get(i)).toAnsi());
            }
            writer.flush();

            writer.println(AttributedString.fromAnsi("\n@|bold,green === Generating Diplomatic Communication ===|@").toAnsi());
            writer.flush();

            Discipline targetDiscipline = result.discipline();
            String script = diplomat.generateCommunicationScript(assessment, plan, targetDiscipline, null);

            writer.println(AttributedString.fromAnsi("\n@|bold,cyan ╔══════════════════════════════════════════════════════════╗@|@").toAnsi());
            writer.println(AttributedString.fromAnsi("@|bold,cyan ║               Diplomatic Script Ready                    ║@|@").toAnsi());
            writer.println(AttributedString.fromAnsi("@|bold,cyan ╚══════════════════════════════════════════════════════════╝@|@\n").toAnsi());
            writer.println(script);
            writer.println(AttributedString.fromAnsi("\n@|italic,yellow (This script is designed to protect your career while addressing the issue)|@").toAnsi());
            writer.flush();

            return "\nDiagnosis complete. Use this information wisely.";

        } catch (Exception e) {
            return "Error during diagnosis: " + e.getMessage();
        }
    }

    @ShellMethod("Quick diagnosis from command line observations")
    public String assess(@ShellOption({"--observations"}) String observations,
                         @ShellOption(value = {"--discipline"}, defaultValue = "ENGINEERING") String discipline) {
        try {
            Discipline disc = Discipline.valueOf(discipline.toUpperCase());
            var obsList = java.util.Arrays.asList(observations.split(";"));
            
            Assessment assessment = diagnostician.diagnose(obsList, null);
            
            return String.format("""
                
                === Organizational Assessment ===
                Stage: %s
                Severity: %.1f/10
                Symptoms: %s
                
                Run 'diagnose' for a full interactive assessment and recovery plan.
                """,
                assessment.stage(),
                assessment.severityScore(),
                assessment.symptoms()
            );
        } catch (Exception e) {
            return "Error during assessment: " + e.getMessage();
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