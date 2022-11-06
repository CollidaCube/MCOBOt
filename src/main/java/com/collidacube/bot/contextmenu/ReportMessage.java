package com.collidacube.bot.contextmenu;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.MessageContextMenuInteraction;

import com.collidacube.bot.modals.ReportModal;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.message.MessageCommand;

public class ReportMessage extends MessageCommand {

	public ReportMessage(DiscordBot bot) {
		super(bot, "report");
	}

	@Override
	public void onCommand(MessageContextMenuInteraction interaction, Message message, User user) {
		new ReportModal(message, user).respondTo(interaction);
	}
}
