package com.thecookiezen.recoverai.intake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.stereotype.Component;

import com.thecookiezen.recoverai.domain.Discipline;
import com.thecookiezen.recoverai.domain.IntakeQuestions;
import com.thecookiezen.recoverai.domain.Severity;
import com.thecookiezen.recoverai.domain.SymptomCategory;
import com.thecookiezen.recoverai.domain.SymptomInventory;
import com.thecookiezen.recoverai.domain.SymptomInventory.SymptomResponse;

@Component
public class IntakeQuestionnaire {

    private static final AttributedStyle CYAN_BOLD = new AttributedStyle().bold().foreground(AttributedStyle.CYAN);
    private static final AttributedStyle YELLOW_BOLD = new AttributedStyle().bold().foreground(AttributedStyle.YELLOW);
    private static final AttributedStyle ITALIC = new AttributedStyle().italic();

    private final ComponentFlow.Builder componentFlowBuilder;

    public IntakeQuestionnaire(ComponentFlow.Builder componentFlowBuilder) {
        this.componentFlowBuilder = componentFlowBuilder;
    }

    public QuestionnaireResult runQuestionnaire(Terminal terminal) {
        var writer = terminal.writer();
        
        writer.println(new AttributedString("=== RecoverAI Organizational Assessment ===", CYAN_BOLD).toAnsi(terminal));
        writer.println(new AttributedString("This questionnaire will help diagnose the stage of AI psychosis in your organization.", ITALIC).toAnsi(terminal));
        writer.flush();

        Discipline discipline = promptForDiscipline(terminal);
        String userRole = promptForUserRole(terminal);
        List<SymptomResponse> responses = collectSymptomResponses(writer, terminal);

        SymptomInventory inventory = IntakeQuestions.createInventory(responses);

        return new QuestionnaireResult(discipline, userRole, inventory);
    }

    private Discipline promptForDiscipline(Terminal terminal) {
        List<SelectItem> items = new ArrayList<>();
        for (Discipline d : Discipline.values()) {
            items.add(SelectItem.of(d.name(), d.name()));
        }

        var result = componentFlowBuilder.clone()
            .terminal(terminal)
            .withSingleItemSelector("discipline")
                .name("What is your discipline/role category?")
                .selectItems(items)
                .and()
            .build()
            .run();

        String selected = result.getContext().get("discipline");
        return Discipline.valueOf(selected);
    }

    private String promptForUserRole(Terminal terminal) {
        var result = componentFlowBuilder.clone()
            .terminal(terminal)
            .withStringInput("role")
                .name("What is your specific job title? (e.g., Senior Software Engineer, Product Manager)")
                .defaultValue("Unspecified")
                .and()
            .build()
            .run();

        return result.getContext().get("role");
    }

    private List<SymptomResponse> collectSymptomResponses(PrintWriter writer, Terminal terminal) {
        List<SymptomResponse> responses = new ArrayList<>();

        for (SymptomCategory category : SymptomCategory.values()) {
            writer.println(new AttributedString("=== " + category.getTitle() + " ===", YELLOW_BOLD).toAnsi(terminal));
            writer.println(new AttributedString(category.getDescription(), ITALIC).toAnsi(terminal));
            writer.flush();

            List<String> questions = IntakeQuestions.getQuestionsForCategory(category);
            for (String question : questions) {
                SymptomResponse response = askSymptomQuestion(question, terminal);
                responses.add(response);
            }
        }

        return responses;
    }

    private SymptomResponse askSymptomQuestion(String question, Terminal terminal) {
        List<SelectItem> severityItems = new ArrayList<>();
        for (Severity severity : Severity.values()) {
            severityItems.add(SelectItem.of(severity.getDisplayText(), severity.getDisplayText()));
        }

        var result = componentFlowBuilder.clone()
            .terminal(terminal)
            .withSingleItemSelector("severity")
                .name(question)
                .selectItems(severityItems)
                .and()
            .build()
            .run();

        String selected = result.getContext().get("severity");
        int severityValue = Integer.parseInt(selected.substring(0, 1));

        var commentResult = ComponentFlow.builder()
            .terminal(terminal)
            .withStringInput("comment")
                .name("Any additional context? (press Enter to skip)")
                .defaultValue("")
                .and()
            .build()
            .run();

        String comment = commentResult.getContext().get("comment");
        String answer = (comment == null || comment.isEmpty())
            ? Severity.fromValue(severityValue).getLabel()
            : comment;

        return new SymptomResponse(question, answer, severityValue);
    }

    public record QuestionnaireResult (
        Discipline discipline,
        String userRole,
        SymptomInventory inventory
    ) {}
}
