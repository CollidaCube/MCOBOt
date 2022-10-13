package com.collidacube.bot.data.impl.participant;

import com.collidacube.bot.Bot;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.ArrayList;
import java.util.List;

public enum Location {

	ARIZONA         ("Arizona",         1007723725371744357L, "AZ"),
	CALIFORNIA      ("California",      1007802426562183288L, "CA"),
	IDAHO           ("Idaho",           1007802824282869831L, "ID"),
	AUSTIN_TEXAS    ("Austin, Texas",   1007802688337092719L, "TX-A"),
	DALLAS_TEXAS    ("Dallas, Texas",   1007802366193586317L, "TX-D"),
	UTAH            ("Utah",            1007802753411723274L, "UT");

	public final String label;
	public final String abbr;
	public final Role role;
	Location(String label, long roleId, String abbr) {
		this.label = label;
		this.abbr = abbr;
		role = Bot.getHubServer().getRoleById(roleId).orElse(null);
	}

	public static List<SlashCommandOptionChoice> getChoices() {
		List<SlashCommandOptionChoice> choices = new ArrayList<>();
		for (Location loc : values()) {
			SlashCommandOptionChoice choice = SlashCommandOptionChoice.create(loc.label, loc.name());
			choices.add(choice);
		} return choices;
	}

}
