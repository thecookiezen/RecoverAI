package com.thecookiezen.recoverai.intake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jline.utils.AttributedString;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import com.thecookiezen.recoverai.domain.Discipline;
import com.thecookiezen.recoverai.domain.SymptomCategory;
import com.thecookiezen.recoverai.domain.SymptomInventory;
import com.thecookiezen.recoverai.domain.SymptomInventory.SymptomResponse;

@Component
public class IntakeQuestionnaire extends AbstractShellComponent {

    private static final String[] SEVERITY_OPTIONS = {"1 - Not at all", "2 - Rarely", "3 - Sometimes", "4 - Often", "5 - Very frequently"};

    public QuestionnaireResult runQuestionnaire() {
        var terminal = getTerminal();
        var writer = terminal.writer();

        writer.println(AttributedString.fromAnsi("\n@|bold,cyan === RecoverAI Organizational Assessment ===|@").toAnsi());
        writer.println(AttributedString.fromAnsi("@|italic This questionnaire will help diagnose the stage of AI psychosis in your organization.|@\n").toAnsi());
        writer.flush();

        Discipline discipline = promptForDiscipline();
        String userRole = promptForUserRole();
        List<SymptomResponse> responses = collectSymptomResponses(writer);

        SymptomInventory inventory = IntakeQuestions.createInventory(responses);

        return new QuestionnaireResult(discipline, userRole, inventory);
    }

    private Discipline promptForDiscipline() {
        List<SelectItem> items = new ArrayList<>();
        for (Discipline d : Discipline.values()) {
            items.add(SelectItem.of(d.name(), d.name()));
        }

        var result = ComponentFlow.builder()
            .withSingleItemSelector("discipline")
                .name("What is your discipline/role category?")
                .selectItems(items)
                .and()
            .build()
            .run();

        String selected = result.getContext().get("discipline");
        return Discipline.valueOf(selected);
    }

    private String promptForUserRole() {
        var result = ComponentFlow.builder()
            .withStringInput("role")
                .name("What is your specific job title? (e.g., Senior Software Engineer, Product Manager)")
                .and()
            .build()
            .run();

        return result.getContext().get("role");
    }

    private List<SymptomResponse> collectSymptomResponses(PrintWriter writer) {
        List<SymptomResponse> responses = new ArrayList<>();

        for (SymptomCategory category : SymptomCategory.values()) {
            writer.println(AttributedString.fromAnsi("\n@|bold,yellow === " + category.getTitle() + " ===|@").toAnsi());
            writer.println(AttributedString.fromAnsi("@|italic " + category.getDescription() + "|@\n").toAnsi());
            writer.flush();

            List<String> questions = IntakeQuestions.getQuestionsForCategory(category);
            for (String question : questions) {
                SymptomResponse response = askSymptomQuestion(question);
                responses.add(response);
            }
        }

        return responses;
    }

    private SymptomResponse askSymptomQuestion(String question) {
        List<SelectItem> severityItems = new ArrayList<>();
        for (String option : SEVERITY_OPTIONS) {
            severityItems.add(SelectItem.of(option, option));
        }

        var result = ComponentFlow.builder()
            .withSingleItemSelector("severity")
                .name(question)
                .selectItems(severityItems)
                .and()
            .build()
            .run();

        String selected = result.getContext().get("severity");
        int severity = Integer.parseInt(selected.substring(0, 1));

        var commentResult = ComponentFlow.builder()
            .withStringInput("comment")
                .name("Any additional context? (press Enter to skip)")
                .defaultValue("")
                .and()
            .build()
            .run();

        String comment = commentResult.getContext().get("comment");
        String answer = (comment == null || comment.isEmpty()) 
            ? (severity <= 2 ? "No/Minimal" : severity <= 3 ? "Moderate" : "Significant") 
            : comment;

        return new SymptomResponse(question, answer, severity);
    }

    public record QuestionnaireResult(
        Discipline discipline,
        String userRole,
        SymptomInventory inventory
    ) {}
}
