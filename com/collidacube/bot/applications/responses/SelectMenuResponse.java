package com.collidacube.bot.applications.responses;

import com.collidacube.bot.applications.ResponseManager;

import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

import java.util.List;
import java.util.stream.Collectors;

public class SelectMenuResponse extends Response implements SelectMenuChooseListener {

    protected SelectMenuResponse(ResponseManager manager) {
        super(manager);
    }

    @Override
    public void init() {
        requestMessage.addSelectMenuChooseListener(this);
    }

    protected List<SelectMenuOption> choices = null;
    @Override
    public String getValue() {
        if (choices == null || choices.size() == 0) return null;
        return choices.stream()
                .map(SelectMenuOption::getLabel)
                .collect(Collectors.joining(", "));
    }

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        SelectMenuInteraction interaction = event.getSelectMenuInteraction();
        interaction.acknowledge();
        choices = interaction.getChosenOptions();
        completeExpectancy();
    }

    @Override
    public void finish() {
        requestMessage.removeListener(SelectMenuChooseListener.class, this);
    }
    
}
