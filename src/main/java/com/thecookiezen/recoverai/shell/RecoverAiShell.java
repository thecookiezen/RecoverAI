package com.thecookiezen.recoverai.shell;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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

@ShellComponent
class RecoverAiShell {
    
    private static final User ANONYMOUS_USER = new SimpleUser(
            "anonymous",
            "Anonymous User",
            "anonymous",
            null
    );

    Chatbot chatbot;
    
    public RecoverAiShell(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    @ShellMethod("Displays greeting message to the user whose name is supplied")
    public String echo(@ShellOption({"-N", "--name"}) String name) {
        return String.format("Hello %s! You are running spring shell cli-demo.", name);
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