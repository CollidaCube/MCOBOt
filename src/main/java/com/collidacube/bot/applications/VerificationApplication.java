package com.collidacube.bot.applications;

import java.awt.Color;
import java.util.Map.Entry;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.javacordext.applications.Application;
import com.collidacube.javacordext.applications.Question;
import com.collidacube.javacordext.applications.ResponseManager;
import com.collidacube.javacordext.applications.ResponseType;
import com.collidacube.javacordext.applications.responses.Response;
import com.collidacube.javacordext.applications.templates.ApplicationTemplate;

public class VerificationApplication extends ApplicationTemplate {

    public VerificationApplication() {
        super(
            "Verification",
            false,
            true,
            Question.of("Please send an image of the **front** of your MCO nametag.", ResponseType.ATTACHMENT),
            Question.of("Please send an image of the **back** of your MCO nametag.", ResponseType.ATTACHMENT),
            Question.of("What is the `username#xxxx` of a **VERIFIED** user that can vouch for you __actively__ participating in MCO?"),
            Question.of("How old are you? (If you'd rather not answer, please respond with an appropriate message)")
        );
    }

    @Override
    public boolean onApplicationStart(User user, String applicationId) {
        return true;
    }

    private static final ServerTextChannel channel = Bot.getHubServer().getTextChannelById(1028172303097397258L).orElse(null);
    @Override
    public void onApplicationComplete(Application app, Throwable error) {
        if (error != null) return;
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(app.getApplicant())
                .setTitle("Verification Request")
                .setFooter("MCO Bot | Applications")
                .setColor(Color.BLUE);

        ResponseManager responses = app.getResponses();
        for (Entry<Question, Response> entry : responses)
            embed.addField(entry.getKey().getQuestion(), entry.getValue().getValue());

        app.getApplicant().addRole(Bot.getVerifiedRole());
        
        channel.sendMessage(embed);
    }
    
}
