package com.collidacube.bot.modules;

import com.collidacube.bot.Bot;
import com.collidacube.bot.applications.Application;
import com.collidacube.bot.applications.Question;
import com.collidacube.bot.applications.ResponseManager;
import com.collidacube.bot.applications.ResponseType;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class Verification implements ServerMemberJoinListener {

    public static final Question[] questions = new Question[] {
            Question.of("Please send an image of the **front** of your MCO nametag.", ResponseType.ATTACHMENT),
            Question.of("Please send an image of the **back** of your MCO nametag.", ResponseType.ATTACHMENT),
            Question.of("What is the `username#xxxx` of a **VERIFIED** user that can vouch for you __actively__ participating in MCO?"),
            Question.of("How old are you? (If you'd rather not answer, please respond with an appropriate message)")
    };

    public static CompletableFuture<Application> startApplication(User user) {
        CompletableFuture<Application> app = Application.startApplication(user, questions);
        return app == null ? null : app.whenComplete(Verification::reportApplication);
    }

    private static final ServerTextChannel channel = Bot.getHubServer().getTextChannelById(1028172303097397258L).orElse(null);
    public static void reportApplication(Application app, Throwable error) {
        if (error != null) return;
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(app.getApplicant())
                .setTitle("Verification Request")
                .setFooter("MCO Bot | Applications")
                .setColor(Color.BLUE);

        ResponseManager responses = app.getResponses();
        for (Question question : questions)
            embed.addField(question.getQuestion(), responses.getResponse(question).getValue());
        
        channel.sendMessage(embed);
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        startApplication(event.getUser());
    }
    
}
