package com.collidacube.bot.modals;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ModalInteraction;

import com.collidacube.bot.data.report.Report;

public class ReportModal extends ModalLegacy {

	private final Message msg;
	private final User user;
	public ReportModal(Message msg, User user) {
		super(
				"Message Report",
				"message-report",
				TextInput.create(
						TextInputStyle.PARAGRAPH,
						"comments",
						"Comments:",
						"Please type your comments here...",
						""
				)
		);

		this.msg = msg;
		this.user = user;
	}

	@Override
	public void onSubmit(ModalInteraction interaction) {
		String comments = interaction.getTextInputValueByCustomId("comments").orElse(null);

		Report report = new Report(msg, comments, user);
		EmbedBuilder receipt = report.submitReport();
		interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(receipt)
				.respond();
	}

}
