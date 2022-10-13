package com.collidacube.bot.commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.util.event.ListenerManager;

import static com.collidacube.bot.Bot.api;

public abstract class Command implements SlashCommandCreateListener {

	public final SlashCommand command;
	public final ListenerManager<SlashCommandCreateListener> listener;
	public Command() {
		command = initSlashCommand();
		listener = api.addSlashCommandCreateListener(this);
	}

	public abstract SlashCommand initSlashCommand();

	public abstract void onCommand(SlashCommandCreateEvent event);

	@Override
	public void onSlashCommandCreate(SlashCommandCreateEvent event) {
		if (event.getSlashCommandInteraction().getCommandId() == command.getId()) onCommand(event);
	}

}
