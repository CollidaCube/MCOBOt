package com.collidacube.bot.commands;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.impl.event.Event;
import com.collidacube.bot.data.impl.participant.Participant;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventCommand extends Command implements AutocompleteCreateListener {

    @Override
    public SlashCommand initSlashCommand() {
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
							SlashCommandOption.create(SlashCommandOptionType.STRING,
                                    "name", "The name of the event")
                    )
            )
        );

        SlashCommandBuilder builder = SlashCommand.with("event", "show participation in an event!", optionsList);
		return builder.createGlobal(Bot.api).join();
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
        List<String> args = interaction.getArguments()
                .stream()
                .map(t -> t.getStringValue().orElse(null))
                .collect(Collectors.toList());
        System.out.println(String.join(", ", args));
        System.out.println(interaction.getFocusedOption());

        interaction.respondWithChoices(
                Arrays.asList(
                        SlashCommandOptionChoice.create("one", 1),
                        SlashCommandOptionChoice.create("two", 2)
                )
        );
    }
}
