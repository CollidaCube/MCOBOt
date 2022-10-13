package com.collidacube.bot.applications.responses;

import com.collidacube.bot.applications.ResponseManager;

import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

public class ButtonResponse extends Response implements ButtonClickListener {

    protected ButtonResponse(ResponseManager manager) {
        super(manager);
    }

    @Override
    public void init() {
        requestMessage.addButtonClickListener(this);
    }

    protected String value = null;
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction interaction = event.getButtonInteraction();
        interaction.acknowledge();
        value = interaction.getCustomId();
        completeExpectancy();
    }

    @Override
    public void finish() {
        requestMessage.removeListener(ButtonClickListener.class, this);
    }
    
}
