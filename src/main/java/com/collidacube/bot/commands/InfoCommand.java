package com.collidacube.bot.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.participant.Participant;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;

public class InfoCommand extends Command {

    public InfoCommand(DiscordBot bot) {
        super(bot, "info");
    }

    @Override
    public SlashCommandBuilder initSlashCommand() {
		return SlashCommand.with("info", "Displays info on a given participant", Arrays.asList(
            SlashCommandOption.createUserOption("Participant", "the participant you would like to see info of", false)
        ));
    }

    @Override
    public void onCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> args = interaction.getArguments();
        
        User user = args.size() == 1 ? args.get(0).getUserValue().orElse(null) : interaction.getUser();
        if (!Bot.getHubServer().isMember(user)) {
            interaction.createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Invalid User!")
                            .setDescription("This user is not a member of this discord server, therefore we have no information on them. Sorry!")
                            .setFooter("MCO Bot | Info")
                            .setColor(Color.RED)
                    ).respond();
            return;
        }

        if (!Bot.getHubServer().getRoles(user).contains(Bot.getVerifiedRole())) {
            interaction.createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Invalid User!")
                            .setDescription("This user is not verified, therefore we have no information on them. Sorry!")
                            .setFooter("MCO Bot | Info")
                            .setColor(Color.RED)
                    ).respond();
            return;
        }

        Participant participant = Participant.getByDiscordId(user.getIdAsString());
        sendInfoMessage(interaction, participant);
    }

    public void sendInfoMessage(SlashCommandInteraction interaction, Participant participant) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(participant.getFullName() + "'s Info")
                .setDescription("Disclaimer: The id number of MCO participants are encrypted in our system and aren't shared under any circumstances.")
                .setFooter("MCO Bot | Info")
                .setColor(Color.BLUE);
        
        embed.addField("Positions", participant.getPositionsString());
        embed.addField("Locations", participant.getLocationsString());

        if (participant.getAvatarUrl() != null) embed.setThumbnail(participant.getAvatarUrl());
        
        if (participant.getAlibi() != null) embed.addField("Alibi", participant.getAlibi().getMentionTag());
        if (participant.getAge() >= 0) embed.addField("Age", participant.getAge() + " as of <t:" + (participant.getLastUpdated() / 1000L) + ">");

        if (participant.getParticipatedEvents().size() > 0) embed.addField("Parcipated In:", String.join(", ", participant.getParticipatedEvents()));

        interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).addEmbed(embed).respond();
    }
    
}
