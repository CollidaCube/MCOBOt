package com.collidacube.bot.utils;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;

import java.awt.*;

public class LoggingChannel {

	public static final LoggingChannel USER_LOGS           = new LoggingChannel(1019447387074338886L);
	public static final LoggingChannel REGISTRATION_LOGS   = new LoggingChannel(1019447419093651476L);
	public static final LoggingChannel TICKET_LOGS         = new LoggingChannel(1019447435476619264L);
	public static final LoggingChannel DISCORD_LOGS        = new LoggingChannel(1019447464236953611L);
	public static final LoggingChannel MISCELLANEOUS_LOGS  = new LoggingChannel(1019451133409902632L);

	private final ServerTextChannel channel;
	private LoggingChannel(long channelId) {
		this.channel = Bot.getHubServer().getTextChannelById(channelId).orElse(null);
	}

	public void log(EmbedBuilder embed) {
		if (channel == null) return;
		channel.sendMessage(embed);
	}

	public void log(String str) {
		if (channel == null) return;
		channel.sendMessage(str);
	}

	public void log(User culprit, String title, String description) {
		log(culprit, title, description, Color.BLUE);
	}

	public void log(User culprit, String title, String description, Color color) {
		log(new EmbedBuilder()
				.setTitle(title)
				.setAuthor(culprit)
				.setDescription(description + "\n" + getTime())
				.setFooter("MCO Bot | Logging ")
				.setColor(color)
			);
	}

	public static String getTime() {
		long epochTime = System.currentTimeMillis() / 1000L;
		return "<t:" + epochTime + ">";
	}

}
