package com.collidacube.bot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

import com.collidacube.bot.data.punishments.Punishment;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;
import com.collidacube.javacordext.utils.time.Clock;
import com.collidacube.javacordext.utils.time.TimeUnit;

public class PunishCommand extends Command implements AutocompleteCreateListener {

	public PunishCommand(DiscordBot bot) {
		super(bot, "punish");
	}

	private static final List<SlashCommandOptionChoice> choices = Arrays.asList(
			SlashCommandOptionChoice.create("Warning", "Warning"),
			SlashCommandOptionChoice.create("Temp Mute", "Temp Mute"),
			SlashCommandOptionChoice.create("Perm Mute", "Perm Mute"),
			SlashCommandOptionChoice.create("Temp Ban", "Temp Ban"),
			SlashCommandOptionChoice.create("Perm Ban", "Perm Ban")
	);

	@Override
	public SlashCommandBuilder initSlashCommand() {
		return SlashCommand.with(
				"punish",
				"Punish a user for misbehavior", Arrays.asList(
						SlashCommandOption.createUserOption("offender", "The offender", true),
						SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "type", "type of punishment", true, choices),
						SlashCommandOption.createStringOption("reason", "Reason for the warning", true),
						SlashCommandOption.createStringOption("duration", "time while punishment is effective", false, true),
						SlashCommandOption.createStringOption("msg-link", "Link to the offending message", false)
				))
				.setDefaultDisabled()
				.setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
	}

	@Override
	public void onCommand(SlashCommandCreateEvent event) {
		SlashCommandInteraction interaction = event.getSlashCommandInteraction();
		User issuer = interaction.getUser();
		User offender = interaction.getOptionUserValueByName("offender").orElse(null);
		String type = interaction.getOptionStringValueByName("type").orElse(null);
		String reason = interaction.getOptionStringValueByName("reason").orElse(null);
		String sDuration = interaction.getOptionStringValueByName("duration").orElse(null);
		String msgLink = interaction.getOptionStringValueByName("msg-link").orElse(null);

		if (offender == null || type == null || reason == null) return;
		
		long seconds = -1;
		if (sDuration != null) seconds = Clock.parse(sDuration).getValue(TimeUnit.SECONDS);

		new Punishment(type, issuer, offender, reason, seconds, msgLink);
	}

	public static final List<TimeUnit> commonUnits = Arrays.asList(
		TimeUnit.SECONDS,
		TimeUnit.MINUTES,
		TimeUnit.HOURS,
		TimeUnit.DAYS,
		TimeUnit.WEEKS,
		TimeUnit.MONTHS,
		TimeUnit.YEARS
	);

	@Override
	public void onAutocompleteCreate(AutocompleteCreateEvent event) {
		AutocompleteInteraction interaction = event.getAutocompleteInteraction();
		String dur = interaction.getOptionStringRepresentationValueByName("duration").orElse(null);
		if (dur == null) return;

		interaction.respondWithChoices(
			commonUnits.stream()
					.map(unit -> SlashCommandOptionChoice.create(dur + unit.abbr, dur + unit.abbr))
					.collect(Collectors.toList())
		);
	}

}
