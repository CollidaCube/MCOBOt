package com.collidacube.bot.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.event.Event;
import com.collidacube.bot.data.participant.Participant;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;

public class EventCommand extends Command implements AutocompleteCreateListener {

	public EventCommand(DiscordBot bot) {
		super(bot, "event");
	}

    @Override
    public SlashCommandBuilder initSlashCommand() {
        List<SlashCommandOption> optionsList = Arrays.asList(
            SlashCommandOption.createWithOptions(
				SlashCommandOptionType.SUB_COMMAND,
				"code",
				"The code to verify participation in an mco event.",
				Arrays.asList(
					SlashCommandOption.createStringOption("code", "The code to verify participation", true)
				)
            ),
            SlashCommandOption.createWithOptions(
				SlashCommandOptionType.SUB_COMMAND,
				"start",
				"Start an mco event. Admins only",
				Arrays.asList(
					SlashCommandOption.createStringOption("name", "The name of the event", true)
				)
            ),
            SlashCommandOption.createWithOptions(
				SlashCommandOptionType.SUB_COMMAND,
				"close",
				"Close an mco event. Admins only",
				Arrays.asList(
					new SlashCommandOptionBuilder()
						.setType(SlashCommandOptionType.STRING)
						.setName("name")
						.setDescription("The name of the event")
						.setRequired(true)
						.setAutocompletable(true)
						.build()
				)
            )
        );

        return SlashCommand.with("event", "show participation in an event!", optionsList);
    }

    @Override
    public void onCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> options = interaction.getOptions();
        List<SlashCommandInteractionOption> args = interaction.getArguments();
        
        User user = interaction.getUser();
        Participant participant = Participant.getByDiscordId(user.getIdAsString());

        String mode = options.get(0).getName();
        String value = args.get(0).getStringValue().orElse(null);
        if (mode.equals("code")) assessParticipation(interaction, participant, value);
        else if (mode.equals("start")) startEvent(interaction, user, value);
        else if (mode.equals("close")) closeEvent(interaction, user, value);
    }

    public void assessParticipation(SlashCommandInteraction interaction, Participant participant, String code) {
        Event event = Event.getEventByCode(code);
        if (event == null) {
            interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("Invalid Code")
					.setDescription("Unfortunately, no events correspond to that code at the moment! Please check for any typos.")
					.setFooter("MCO Bot | Events")
					.setColor(Color.RED)
				).respond();
        }
        else if (participant.getParticipatedEvents().contains(event.getLabel())) {
            interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("Already Registered")
					.setDescription("You have already been verified for this event! You don't need to verify again, silly!")
					.setFooter("MCO Bot | Events")
					.setColor(Color.RED)
				).respond();
        }
        else {
            participant.addParticipation(event.getLabel());
            interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("Success!")
					.setDescription("You have successfully been verified for the `" + event + "` event!")
					.setFooter("MCO Bot | Events")
					.setColor(Color.GREEN)
				).respond();
        }
    }

    private void startEvent(SlashCommandInteraction interaction, User user, String label) {
        if (!Bot.isAdmin(user)) {
            interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("No Permission!")
					.setDescription("Unfortunately, you are not allowed to use this command!")
					.setFooter("MCO Bot | Events")
					.setColor(Color.RED)
				).respond();
            return;
        }
        
        Event event = Event.getEventByLabel(label);
        if (event == null) event = new Event(label);

        interaction.createImmediateResponder()
			.setFlags(MessageFlag.EPHEMERAL)
			.addEmbed(new EmbedBuilder()
				.setTitle("Success!")
				.setDescription("Successfully started the `" + event + "` event! The code is ||" + event.getCode() + "||.")
				.setFooter("MCO Bot | Events")
				.setColor(Color.GREEN)
			).respond();
    }

    private void closeEvent(SlashCommandInteraction interaction, User user, String label) {
        if (!Bot.isAdmin(user)) {
            interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("No Permission!")
					.setDescription("Unfortunately, you are not allowed to use this command!")
					.setFooter("MCO Bot | Events")
					.setColor(Color.RED)
				).respond();
            return;
        }

        Event event = Event.getEventByLabel(label);
        
        if (event == null) interaction.createImmediateResponder()
			.setFlags(MessageFlag.EPHEMERAL)
			.addEmbed(new EmbedBuilder()
				.setTitle("Doesn't Exist!")
				.setDescription("Sorry! There is no active event called `" + label + "`. Please check for any typos!")
				.setFooter("MCO Bot | Events")
				.setColor(Color.RED)
			).respond();
        
        else {
			event.close();
			interaction.createImmediateResponder()
				.setFlags(MessageFlag.EPHEMERAL)
				.addEmbed(new EmbedBuilder()
					.setTitle("Success!")
					.setDescription("Successfully closed the `" + event + "` event!")
					.setFooter("MCO Bot | Events")
					.setColor(Color.GREEN)
				).respond();
		}
    }

    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        AutocompleteInteraction interaction = event.getAutocompleteInteraction();
        if (interaction.getCommandId() == getCommand().getId())
			interaction.respondWithChoices(Event.getChoices());
    }

}
