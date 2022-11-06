package com.collidacube.bot.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.applications.Application;
import com.collidacube.javacordext.commands.command.Command;

public class ApplyCommand extends Command {

    public ApplyCommand(DiscordBot bot) {
        super(bot, "apply");
    }

    public SlashCommandBuilder initSlashCommand() {
        List<SlashCommandOption> options = Arrays.asList(
            SlashCommandOption.createWithOptions(
                SlashCommandOptionType.SUB_COMMAND,
                "start",
                "Cancel your current application",
                Arrays.asList(
                    SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "name", "What you are applying for", true, Arrays.asList(
                        SlashCommandOptionChoice.create("Verification", "Verification"),
                        SlashCommandOptionChoice.create("Staff", "Staff")
                    ))
                )
            ),
            SlashCommandOption.createWithOptions(
                SlashCommandOptionType.SUB_COMMAND,
                "cancel",
                "Cancel your current application"
            )
        );

		return SlashCommand.with(label, "Apply for verification!", options);
    }

    @Override
    public void onCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> options = interaction.getOptions();

        if      (options.get(0).getName().equals("start" ))  startApplication(interaction);
        else if (options.get(0).getName().equals("cancel")) cancelApplication(interaction);
    }

    public void startApplication(SlashCommandInteraction interaction) {
        String applicationId = interaction.getArguments().get(0).getStringValue().orElse(null);
        CompletableFuture<Application> appFuture = Application.startApplication(interaction.getUser(), applicationId);
        if (appFuture == null) interaction.createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Failed to Start Application")
                                .setDescription("Unfortunately, we could not start the application. This may be because you already have one open. If so, please close it with `/apply cancel`. If not, feel free to open a ticket.")
                                .setFooter("MCO Bot | Applications")
                                .setColor(Color.RED)
                ).respond();
        else {
            interaction.createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Successfully started application")
                                .setDescription("You should recieve a DM. If one does not appear, please check to make sure you allow messages from server members. If you have further issues, please open a ticket. Feel free to close this at any time with `/apply cancel`.")
                                .setFooter("MCO Bot | Applications")
                                .setColor(Color.BLUE)
                ).respond();
        }
    }

    public void cancelApplication(SlashCommandInteraction interaction) {
        CompletableFuture<Application> appFuture = Application.getApplication(interaction.getUser());
        if (appFuture == null) {
            interaction.createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Failed to Cancel Application")
                                .setDescription("You don't currently have any applications open! Feel free to open one with `/apply start`.")
                                .setFooter("MCO Bot | Applications")
                                .setColor(Color.RED)
                ).respond();
        }
        else {
            appFuture.cancel(true);
            interaction.createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Successfully cancelled application")
                                .setDescription("Your application has been cancelled. You are now able to open a new application with `/apply start`.")
                                .setFooter("MCO Bot | Applications")
                                .setColor(Color.BLUE)
                ).respond();
        }
    }
    
}
