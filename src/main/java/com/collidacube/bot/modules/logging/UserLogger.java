package com.collidacube.bot.modules.logging;

import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.event.server.role.UserRoleRemoveEvent;
import org.javacord.api.listener.server.member.ServerMemberBanListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;
import org.javacord.api.listener.server.role.UserRoleAddListener;
import org.javacord.api.listener.server.role.UserRoleRemoveListener;

import com.collidacube.bot.Bot;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.utils.specialized.DiscordUtils;

public class UserLogger
		implements ServerMemberJoinListener,
		ServerMemberLeaveListener,
		ServerMemberBanListener,
		UserRoleAddListener,
		UserRoleRemoveListener {

	public static void init() {
		UserLogger inst = new UserLogger();
		Bot.api.addServerMemberJoinListener(inst);
		Bot.api.addServerMemberLeaveListener(inst);
		Bot.api.addServerMemberBanListener(inst);
		Bot.api.addUserRoleAddListener(inst);
		Bot.api.addUserRoleRemoveListener(inst);
	}

	@Override
	public void onServerMemberBan(ServerMemberBanEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.MEMBER_BAN_ADD);
		delete(
				"Banned User",
				event.getUser().getMentionTag() + " was banned.",
				user);
	}

	@Override
	public void onServerMemberJoin(ServerMemberJoinEvent event) {
		create("User Joined", event.getUser().getMentionTag() + " joined the server!", null);
	}

	@Override
	public void onServerMemberLeave(ServerMemberLeaveEvent event) {
		delete("User Left", event.getUser().getMentionTag() + " left the server!", null);
	}

	@Override
	public void onUserRoleAdd(UserRoleAddEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.MEMBER_ROLE_UPDATE);
		create(
				"Role Given",
				event.getUser().getMentionTag() + " was given " + event.getRole().getMentionTag(),
				user);
	}

	@Override
	public void onUserRoleRemove(UserRoleRemoveEvent event) {
		User user = DiscordUtils.getCulprit(event.getServer(), AuditLogActionType.MEMBER_ROLE_UPDATE);
		delete(
				"Role Revoked",
				event.getUser().getMentionTag() + " was revoked " + event.getRole().getMentionTag(),
				user);
	}

	public void create(String title, String description, User culprit) {
		LoggingCategory.USER_LOGS.success(culprit, null, title, description);
	}

	public void update(String title, String description, User culprit) {
		LoggingCategory.USER_LOGS.info(culprit, null, title, description);
	}

	public void delete(String title, String description, User culprit) {
		LoggingCategory.USER_LOGS.error(culprit, null, title, description);
	}

}
