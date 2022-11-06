package com.collidacube.bot.modals;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.listener.interaction.ModalSubmitListener;

import com.collidacube.bot.Bot;

public abstract class ModalLegacy implements ModalSubmitListener {

	private final String title, customId;
	private final ActionRow[] components;
	public ModalLegacy(String title, String customId, LowLevelComponent... components) {
		this.title = title;
		this.customId = customId;
		this.components = new ActionRow[components.length];
		for (int i = 0; i < components.length; i++) this.components[i] = ActionRow.of(components[i]);

		Bot.api.addModalSubmitListener(this);
	}

	public void respondTo(InteractionBase interaction) {
		interaction.respondWithModal(customId, title, components);
	}

	public abstract void onSubmit(ModalInteraction interaction);

	@Override
	public void onModalSubmit(ModalSubmitEvent event) {
		ModalInteraction interaction = event.getModalInteraction();
		if (interaction.getCustomId().equals(customId)) onSubmit(interaction);
		Bot.api.removeListener(ModalSubmitListener.class, this);
	}
}
