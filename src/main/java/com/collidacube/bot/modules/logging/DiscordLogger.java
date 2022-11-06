package com.collidacube.bot.modules.logging;

import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelCreateEvent;
import org.javacord.api.event.channel.server.ServerChannelDeleteEvent;
import org.javacord.api.event.server.emoji.KnownCustomEmojiCreateEvent;
import org.javacord.api.event.server.emoji.KnownCustomEmojiDeleteEvent;
import org.javacord.api.event.server.role.RoleCreateEvent;
import org.javacord.api.event.server.role.RoleDeleteEvent;
import org.javacord.api.event.server.sticker.StickerCreateEvent;
import org.javacord.api.event.server.sticker.StickerDeleteEvent;
import org.javacord.api.listener.channel.server.ServerChannelCreateListener;
import org.javacord.api.listener.channel.server.ServerChannelDeleteListener;
import org.javacord.api.listener.server.emoji.KnownCustomEmojiCreateListener;
import org.javacord.api.listener.server.emoji.KnownCustomEmojiDeleteListener;
import org.javacord.api.listener.server.role.RoleCreateListener;
import org.javacord.api.listener.server.role.RoleDeleteListener;
import org.javacord.api.listener.server.sticker.StickerCreateListener;
import org.javacord.api.listener.server.sticker.StickerDeleteListener;

import com.collidacube.bot.Bot;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.utils.specialized.DiscordUtils;

public class DiscordLogger
		implements ServerChannelCreateListener,
		ServerChannelDeleteListener,
		RoleCreateListener,
		RoleDeleteListener,
		KnownCustomEmojiCreateListener,
		KnownCustomEmojiDeleteListener,
		StickerCreateListener,
		StickerDeleteListener {

	public static void init() {
		DiscordLogger inst = new DiscordLogger();
		Bot.api.addServerChannelCreateListener(inst);
		Bot.api.addServerChannelDeleteListener(inst);
		Bot.api.addRoleCreateListener(inst);
		Bot.api.addRoleDeleteListener(inst);
		Bot.api.addKnownCustomEmojiCreateListener(inst);
		Bot.api.addKnownCustomEmojiDeleteListener(inst);
		Bot.api.addStickerCreateListener(inst);
		Bot.api.addStickerDeleteListener(inst);
	}

	@Override
	public void onServerChannelCreate(ServerChannelCreateEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.CHANNEL_CREATE);
		create(
				"Created Channel",
				"<#" + event.getChannel().getIdAsString() + "> was created.",
				user);
	}

	@Override
	public void onServerChannelDelete(ServerChannelDeleteEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.CHANNEL_DELETE);
		delete(
				"Deleted Channel",
				"<#" + event.getChannel().getIdAsString() + "> was deleted.",
				user);
	}

	@Override
	public void onKnownCustomEmojiCreate(KnownCustomEmojiCreateEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.EMOJI_CREATE);
		create(
				"Created Emoji",
				event.getEmoji().getMentionTag() + " was created.",
				user);
	}

	@Override
	public void onKnownCustomEmojiDelete(KnownCustomEmojiDeleteEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.EMOJI_DELETE);
		delete(
				"Deleted Emoji",
				event.getEmoji().getMentionTag() + " was deleted.",
				user);
	}

	@Override
	public void onRoleCreate(RoleCreateEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.ROLE_CREATE);
		delete(
				"Created Role",
				event.getRole().getMentionTag() + " was created.",
				user);
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.ROLE_DELETE);
		delete(
				"Deleted Role",
				event.getRole().getMentionTag() + " was deleted.",
				user);
	}

	@Override
	public void onStickerCreate(StickerCreateEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.STICKER_CREATE);
		create(
				"Created Sticker",
				"A sticker was created.",
				user);
	}

	@Override
	public void onStickerDelete(StickerDeleteEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.STICKER_DELETE);
		delete(
				"Deleted Sticker",
				"A sticker was deleted.",
				user);
	}

	public void create(String title, String description, User culprit) {
		LoggingCategory.DISCORD_LOGS.success(culprit, null, title, description);
	}

	public void update(String title, String description, User culprit) {
		LoggingCategory.DISCORD_LOGS.info(culprit, null, title, description);
	}

	public void delete(String title, String description, User culprit) {
		LoggingCategory.DISCORD_LOGS.error(culprit, null, title, description);
	}

}
