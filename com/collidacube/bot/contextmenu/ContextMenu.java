package com.collidacube.bot.contextmenu;

import com.collidacube.bot.Bot;
import org.javacord.api.interaction.UserContextMenu;
import org.javacord.api.interaction.UserContextMenuBuilder;

public class ContextMenu {

	public ContextMenu(UserContextMenu menu) {
		menu = new UserContextMenuBuilder().setName("test").createGlobal(Bot.api).join();
	}

}
