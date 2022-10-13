package com.collidacube.bot.data.impl.participant;

import com.collidacube.bot.Bot;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.ArrayList;
import java.util.List;

public enum Position {

	DIRECTOR            ("Director",            1007723893697560596L, "DIR"),
	STAFF               ("Staff",               1007803268820377600L, "STF"),
	HELPER              ("Helper",              1020187740148613131L, "HLP"),
	ORCHESTRA           ("Orchestra",           1007724130889646150L, "ORC"),
	GRAND_CHORUS        ("Grand Chorus",        1007803133445013594L, "GCH"),
	CONCERT_CHOIR_WOMEN ("Concert Choir Women", 1007802313748004904L, "CCM"),
	CONCERT_CHOIR_MEN   ("Concert Choir Men",   1007658214210424862L, "CCW"),
	YOUTH_CHORUS        ("Youth Chorus",        1007803021201264784L, "YCH");

	public final String label;
	public final String abbr;
	public final Role role;
	Position(String label, long roleId, String abbr) {
		this.label = label;
		this.abbr = abbr;
		this.role = Bot.getHubServer().getRoleById(roleId).orElse(null);
	}

	public static List<SlashCommandOptionChoice> getChoices() {
		List<SlashCommandOptionChoice> choices = new ArrayList<>();
		for (Position loc : values()) {
			SlashCommandOptionChoice choice = SlashCommandOptionChoice.create(loc.label, loc.name());
			choices.add(choice);
		} return choices;
	}

}
