package com.collidacube.bot.commands;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;
import com.collidacube.javacordext.data.DataManager;

public class SaveAllCommand extends Command {

    public SaveAllCommand(DiscordBot bot) {
        super(bot, "saveall");
    }

    @Override
    public SlashCommandBuilder initSlashCommand() {
        return SlashCommand.with("saveall", "save all data")
            .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public void onCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        DataManager.saveAll();
        interaction.createImmediateResponder()
            .setFlags(MessageFlag.EPHEMERAL)
            .setContent("Successfully saved everything")
            .respond();
    }
    
}
