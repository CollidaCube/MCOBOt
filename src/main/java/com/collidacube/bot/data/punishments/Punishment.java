package com.collidacube.bot.data.punishments;

import java.awt.Color;
import java.time.Duration;
import java.util.HashMap;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.bot.modules.Verification;
import com.collidacube.javacordext.data.DataManager;
import com.collidacube.javacordext.data.DataPackage;
import com.collidacube.javacordext.utils.specialized.Utils;

public class Punishment extends DataPackage<Punishment> {

	public static final DataManager<Punishment> DATA_MANAGER = new DataManager<>(
			Punishment.class,
			Punishment::loadFrom,
			Bot.bot.dataPath + "Punishments.db",
			"time",
			"type",
			"issuerId",
			"offenderId",
			"reason",
			"duration",
			"msgLink"
	);

	private static Punishment loadFrom(HashMap<String, String> data) {
		String time = data.get("time");
		String type = data.get("type");
		String issuerId = data.get("issuerId");
		String offenderId = data.get("offenderId");
		String reason = data.get("reason");
		String duration = data.get("duration");
		String msgLink = data.get("msgLink");
		return new Punishment(Long.parseLong(time), type, issuerId, offenderId, reason, Long.parseLong(duration), msgLink);
	}

	private final long time;
	private final String type;
	private final String issuerId;
	private final String offenderId;
	private final String reason;
	private final long duration;
	private final String msgLink;
	public Punishment(String type, User issuer, User offender, String reason, long duration, String msgLink) {
		this(
				System.currentTimeMillis(),
				type,
				issuer.getIdAsString(),
				offender.getIdAsString(),
				reason,
				duration,
				msgLink
		);

		if (offender == null || type == null || reason == null) return;

		EmbedBuilder receipt = new EmbedBuilder()
				.setAuthor(issuer)
				.setTitle(type)
				.setDescription("You have been issued a formal warning for the following:\n`" + reason + "`")
				.setFooter("MCO Bot | Punishment")
				.setColor(Color.RED);

		if (msgLink != null) receipt.setUrl(msgLink);

		offender.sendMessage(receipt);
		inflictPunishment();
	}

	private Punishment(long time, String type, String issuerId, String offenderId, String reason, long duration, String msgLink) {
		super(Punishment.class, DATA_MANAGER);
		this.time = time;
		this.type = type;
		this.issuerId = issuerId;
		this.offenderId = offenderId;
		this.reason = reason;
		this.duration = duration;
		this.msgLink = msgLink;

		if (duration > -1)
			Bot.scheduler.scheduleAt(time + duration * 1000, this::repealPunishment);
	}

	public void inflictPunishment() {
		if (!DATA_MANAGER.isRegistered(this)) return;

		User user = Utils.await(Bot.api.getUserById(offenderId));
		if (user == null) return;

		if (type.equals("Temp Mute") || type.equals("Perm Mute")) user.removeRole(Verification.PRIVILEGED_ROLE);
		if (type.equals("Temp Ban") || type.equals("Perm Ban")) Bot.getHubServer().banUser(offenderId, Duration.ZERO, reason);
	}

	public void repealPunishment() {
		if (!DATA_MANAGER.isRegistered(this)) return;

		User user = Utils.await(Bot.api.getUserById(offenderId));
		if (user == null) return;

		if (type.equals("Temp Mute") || type.equals("Perm Mute")) user.addRole(Verification.PRIVILEGED_ROLE);
		if (type.equals("Temp Ban") || type.equals("Perm Ban")) Bot.getHubServer().unbanUser(user);
		DATA_MANAGER.unregister(this);
	}

	public boolean isTimeFulfilled() {
		if (duration == -1) return false;
		long now = System.currentTimeMillis() / 1000;
		return now >= (time + duration);
	}

	@Override
	public HashMap<String, String> getData() {
		HashMap<String, String> data = new HashMap<>();
		data.put("time", String.valueOf(time));
		data.put("type", type);
		data.put("issuerId", issuerId);
		data.put("offenderId", offenderId);
		data.put("reason", reason);
		data.put("duration", String.valueOf(duration));
		data.put("msgLink", msgLink);
		return data;
	}

}
