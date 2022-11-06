package com.collidacube.bot.log;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.javacordext.utils.log.LogMode;
import com.collidacube.javacordext.utils.log.Logger;

public enum LoggingCategory {

	USER_LOGS           (1019447387074338886L),
	REGISTRATION_LOGS   (1019447419093651476L),
	PUNISHMENT_LOGS     (1034672629061267486L),
	TICKET_LOGS         (1019447435476619264L),
	DISCORD_LOGS        (1019447464236953611L),
	MISCELLANEOUS_LOGS  (1019451133409902632L);

	private final ServerTextChannel channel;
	private final Logger logger = Bot.bot.logger;
	LoggingCategory(long channelId) {
		this.channel = Bot.getHubServer().getTextChannelById(channelId).orElse(null);
	}

	public void debug(User culprit, User victim, String title, String description) {
		if (!logger.DEBUG_MODE) log(LogMode.DEBUG, culprit, victim, title, description);
	}

    public void info(User culprit, User victim, String title, String description) {
		log(LogMode.INFO, culprit, victim, title, description);
    }

	public void warning(User culprit, User victim, String title, String description) {
		log(LogMode.WARNING, culprit, victim, title, description);
	}

	public void error(User culprit, User victim, String title, String description) {
		log(LogMode.ERROR, culprit, victim, title, description);
	}

	public void success(User culprit, User victim, String title, String description) {
		log(LogMode.SUCCESS, culprit, victim, title, description);
	}

	public void log(LogMode mode, User culprit, User victim, String title, String description) {
		sendLogMessage(mode, culprit, victim, title, description);
	}

	public void sendLogMessage(LogMode mode, User culprit, User victim, String title, String description) {
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle(title)
				.setDescription(description
						+ "\n"
						+ (culprit == null ? "" : "\nDone By: " + culprit.getMentionTag())
						+ (victim == null ? "" : "\nDone To: " + victim.getMentionTag())
						+ "\n" + getTime())
				.setFooter("MCO Bot | Logging ")
				.setColor(mode.color);

		channel.sendMessage(embed);
	}

	public static String getTime() {
		long epochTime = System.currentTimeMillis() / 1000L;
		return "<t:" + epochTime + ">";
	}

}
