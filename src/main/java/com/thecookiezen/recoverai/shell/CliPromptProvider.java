package com.thecookiezen.recoverai.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Primary;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CliPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("recoverAI:>", 
            AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)
        );
    }
}