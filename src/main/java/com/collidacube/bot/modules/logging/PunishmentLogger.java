package com.collidacube.bot.modules.logging;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.utils.log.LogMode;
import com.collidacube.javacordext.utils.specialized.SqlUtils;
import com.collidacube.javacordext.utils.specialized.Utils;

public class PunishmentLogger {

	private static final List<String> fields = Arrays.asList("time", "type", "issuer", "offender", "reason", "msgLink");

	private static Connection getConnection() {
		Connection conn = SqlUtils.getConnection(Bot.bot.dataPath + "Punishments.db");
		SqlUtils.addTable(conn, "history", fields);
		return conn;
	}

	public static void log(String type, User issuer, User offender, String reason, Message msg) {
		String issuerId = issuer == null ? null : issuer.getIdAsString();
		String offenderId = offender == null ? null : offender.getIdAsString();
		String msgLink = msg == null ? null : msg.getLink().toExternalForm();

		log(
				type,
				issuerId,
				offenderId,
				reason,
				msgLink
		);

		LoggingCategory.PUNISHMENT_LOGS.sendLogMessage(LogMode.ERROR, issuer, offender, type, "`" + reason + (msgLink == null ? "`" : "`\n" + msgLink));
	}

	public static void log(String type, String issuerId, String offenderId, String reason, String msgLink) {
		String time = Utils.getFormattedTime("yyyy-MM-dd HH:mm:ss");
		SqlUtils.insert(getConnection(), "history", fields, Arrays.asList(time, type, issuerId, offenderId, reason, msgLink));
	}

}
