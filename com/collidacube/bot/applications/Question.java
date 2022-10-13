package com.collidacube.bot.applications;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class Question {

    private final String question;
    private final ResponseType expectedResponse;
    private final ActionRow actionRow;
    public Question(String question, ResponseType expectedResponse, ActionRow actionRow) {
        if (expectedResponse == null) expectedResponse = ResponseType.MESSAGE;
        this.question = question;
        this.expectedResponse = expectedResponse;
        this.actionRow = actionRow.getComponents().size() == 0 ? null : actionRow;
    }

    public String getQuestion() {
        return question;
    }

    public ResponseType getExpectedResponse() {
        return expectedResponse;
    }

    public ActionRow getActionRow() {
        return actionRow;
    }

    public CompletableFuture<Message> ask(User user, String title) {
        MessageBuilder msg = new MessageBuilder().addEmbed(
                new EmbedBuilder()
                        .setAuthor(user)
                        .setTitle(title)
                        .setDescription(question)
                        .setFooter("API | Applications")
                        .setColor(Color.BLUE)
        );
        if (actionRow != null) msg.addComponents(actionRow);
        return msg.send(user);
    }

    public static Question of(String question, ResponseType expectResponse, ActionRow actionRow) {
        return new Question(question, expectResponse, actionRow);
    }

    public static Question of(String question, ResponseType expectResponse, List<LowLevelComponent> actionRow) {
        return new Question(question, expectResponse, ActionRow.of(actionRow));
    }

    public static Question of(String question, ResponseType expectResponse, LowLevelComponent... actionRow) {
        return new Question(question, expectResponse, ActionRow.of(actionRow));
    }

    public static Question of(String question, ActionRow actionRow) {
        return new Question(question, ResponseType.MESSAGE, actionRow);
    }

    public static Question of(String question, List<LowLevelComponent> actionRow) {
        return new Question(question, ResponseType.MESSAGE, ActionRow.of(actionRow));
    }

    public static Question of(String question, LowLevelComponent... actionRow) {
        return new Question(question, ResponseType.MESSAGE, ActionRow.of(actionRow));
    }

}
