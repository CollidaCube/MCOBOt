package com.collidacube.bot.modals;

import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.listener.interaction.ModalSubmitListener;
import org.javacord.api.util.event.ListenerManager;

import java.util.HashMap;

import static com.collidacube.bot.Bot.api;

public abstract class ModalForm<T extends InteractionBase> implements ModalSubmitListener {

	private static final HashMap<Long, ModalForm<?>> openModals = new HashMap<>();

	public final T originalInteraction;
	public final String customId;
	public final long userId;
	public final ListenerManager<ModalSubmitListener> listener;
	public ModalForm(T interaction, String title, HighLevelComponent... components) {
		this.originalInteraction = interaction;
		this.customId = toId(title);

		interaction.respondWithModal(
				customId,
				title,
				components
		);

		this.listener = api.addModalSubmitListener(this);
		this.userId = originalInteraction.getUser().getId();

		if (openModals.containsKey(userId))
			openModals.get(userId).finish();

		openModals.put(userId, this);
	}

	public static String toId(String str) {
		return str.toLowerCase().replaceAll(" ", "-");
	}

	public abstract void onSubmitForm(ModalSubmitEvent event);

	@Override
	public void onModalSubmit(ModalSubmitEvent event) {
		System.out.println("Modal Submit [" + event.getModalInteraction().getCustomId() + "] expecting \"" + customId + "\"");
		if (event.getModalInteraction().getCustomId().equals(customId)) onSubmitForm(event);
	}

	public void finish() {
		api.removeListener(this);
		openModals.remove(userId);
	}

}
