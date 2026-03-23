package com.thecookiezen.recoverai.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;
import com.embabel.common.core.types.TextSimilaritySearchRequest;
import com.thecookiezen.archiledger.agenticmemory.domain.UpsertMemoryRequest;
import com.thecookiezen.archiledger.domain.model.MemoryNote;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire;
import com.thecookiezen.recoverai.intake.IntakeQuestionnaire.QuestionnaireResult;
import com.thecookiezen.archiledger.agenticmemory.rag.MemoryNoteRetrievable;
import com.thecookiezen.archiledger.agenticmemory.rag.MemoryNoteSearchOperations;

@ShellComponent
class RecoverAiShell extends AbstractShellComponent {

    private static final Logger log = LoggerFactory.getLogger(RecoverAiShell.class);

    private final AgentPlatform agentPlatform;
    private final IntakeQuestionnaire questionnaire;
    private final MemoryNoteSearchOperations search;

    public RecoverAiShell(AgentPlatform agentPlatform, IntakeQuestionnaire questionnaire, MemoryNoteSearchOperations memoryNoteSearchOperations) {
        this.agentPlatform = agentPlatform;
        this.questionnaire = questionnaire;
        this.search = memoryNoteSearchOperations;
    }

    @ShellMethod("memory read test")
    public String memoryFetch(String request) {
        return search.vectorSearch(TextSimilaritySearchRequest.create(request, 0, 10), MemoryNoteRetrievable.class).toString();
    }

    @ShellMethod("memory write test")
    public void memoryWrite(String... args) {
        String request = String.join(" ", args);
        var invocation = AgentInvocation
            .builder(agentPlatform)
            .options(new ProcessOptions()
                .withVerbosity(new Verbosity()
                    .withShowPrompts(true)
                    .withShowLlmResponses(true)
                    .withDebug(true)))
            .build(MemoryNote.class);

        invocation.invoke(new UpsertMemoryRequest(request));
    }

    @ShellMethod("Run the full RecoverAI diagnostic workflow")
    public DiagnosisResult diagnose() {
        var terminal = getTerminal();
        var writer = terminal.writer();

        try {
            var style = new AttributedStyle().bold().foreground(AttributedStyle.CYAN);
            writer.println(new AttributedString("\n+==========================================================+", style).toAnsi(terminal));
            writer.println(new AttributedString("|          RecoverAI - Organizational Diagnosis           |", style).toAnsi(terminal));
            writer.println(new AttributedString("+==========================================================+\n", style).toAnsi(terminal));
            writer.flush();

            QuestionnaireResult questionnaireResult = runQuestionnaireWithProgress(writer, terminal);
            String script = runAgentWithProgress(questionnaireResult, writer, terminal);

            return new DiagnosisResult(questionnaireResult, script, null);
        } catch (Exception e) {
            log.error("Diagnosis failed", e);
            return new DiagnosisResult(null, null, "Diagnosis failed: " + e.getMessage());
        }
    }

    private QuestionnaireResult runQuestionnaireWithProgress(java.io.PrintWriter writer, org.jline.terminal.Terminal terminal) {
        writer.println(new AttributedString("Step 1/2: Collecting symptoms...", 
            new AttributedStyle().foreground(AttributedStyle.YELLOW)).toAnsi(terminal));
        writer.flush();
        return questionnaire.runQuestionnaire(terminal);
    }

    private String runAgentWithProgress(QuestionnaireResult result, java.io.PrintWriter writer, org.jline.terminal.Terminal terminal) {
        writer.println(new AttributedString("Step 2/2: Generating diagnosis and recovery plan...", 
            new AttributedStyle().foreground(AttributedStyle.YELLOW)).toAnsi(terminal));
        writer.flush();

        var invocation = AgentInvocation
            .builder(agentPlatform)
            .options(new ProcessOptions()
                .withVerbosity(new Verbosity()
                    .withShowPrompts(true)
                    .withShowLlmResponses(true)
                    .withDebug(true)))
            .build(String.class);

        return invocation.invoke(result);
    }

    public record DiagnosisResult(
        QuestionnaireResult questionnaireResult,
        String diplomaticScript,
        String error
    ) {
        public boolean isSuccess() {
            return error == null;
        }
    }
}
