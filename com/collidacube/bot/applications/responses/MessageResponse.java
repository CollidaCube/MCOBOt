package com.collidacube.bot.applications.responses;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.MessageDeleteListener;

import com.collidacube.bot.applications.ResponseManager;

public class MessageResponse extends Response implements MessageCreateListener, MessageDeleteListener {

    protected MessageResponse(ResponseManager manager) {
        super(manager);
    }
    @Override
    public void init() {
        user.addMessageCreateListener(this);
    }

    protected Message message;
    @Override
    public String getValue() {
        if (message == null || message.getContent().length() == 0) return null;
        return "`" + message.getContent() + "`";
    }

    @Override
    public void finish() {
        user.removeListener(MessageCreateListener.class, this);
        if (message != null) message.removeListener(MessageDeleteListener.class, this);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!expectancy.isDone() && event.isPrivateMessage()) {
            if (message != null) message.removeListener(MessageDeleteListener.class, this);
            message = event.getMessage();
            message.addMessageDeleteListener(this);
            completeExpectancy();
        }
    }
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        message = null;
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("Equals?");
        return super.equals(obj);
    }
    
}
