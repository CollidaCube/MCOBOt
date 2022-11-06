package com.collidacube.bot.modules;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.collidacube.javacordext.utils.Detect;

public class ProfanityPrevention implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String content = event.getMessageContent();
        if (Detect.profanity(content)) {
            event.deleteMessage("Profanity detected");
            
            User user = event.getMessageAuthor().asUser().orElse(null);
            if (user != null) user.sendMessage("Your message was deleted because we detected profanity! If you believe this is an error, please open a ticket.");
        }
    }
    
}
