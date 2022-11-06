package com.collidacube.bot.commands;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.entity.Attachment;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

import com.collidacube.bot.modals.EmbedModal;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;
import com.collidacube.javacordext.utils.specialized.Utils;
import com.collidacube.javacordext.utils.specialized.Utils.Pair;

public class EmbedCommand extends Command implements AutocompleteCreateListener {

    private static final HashMap<String, Color> presets = Utils.parseToMap(
        "Red:#FF0000,Orange:#FF8000,Yellow:#FFFF00,Lime:#80FF00,Green:#00FF00,Sea Green:#00FF80,Aqua:#00FFFF,Sky Blue:#0080FF,Blue:#0000FF,Purple:#8000FF,Pink:#FF00FF,Magenta:#FF0080,White:#FFFFFF,Gray:#808080,Grey:#808080,Black:#000000",
        ",", ":",
        (label, hex) -> {
            return new Pair<String,Color>(label, Color.decode(hex));
        });

    public EmbedCommand(DiscordBot bot) {
        super(bot, "embed");
    }

    private static final List<SlashCommandOptionChoice> choices = presets.keySet()
                                                            .stream()
                                                            .map(color -> SlashCommandOptionChoice.create(color, color))
                                                            .collect(Collectors.toList());

    @Override
    public SlashCommandBuilder initSlashCommand() {
        return SlashCommand.with(label, "Send an embed message", Arrays.asList(
            SlashCommandOption.createUserOption("author", "author of the embed", false),
            SlashCommandOption.createAttachmentOption("thumbnail", "thumbnail of the embed", false),
            SlashCommandOption.createAttachmentOption("image", "image of the embed", false),
            SlashCommandOption.createStringOption("color", "color of the left border of the embed", false, true)
        ));
    }

    @Override
    public void onCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        String sColor = interaction.getOptionStringValueByName("color").orElse(null);
        
        User author = interaction.getOptionUserValueByName("author").orElse(null);
        Attachment thumbnail = interaction.getOptionAttachmentValueByName("thumbnail").orElse(null);
        Attachment image = interaction.getOptionAttachmentValueByName("image").orElse(null);
        Color color = presets.get(sColor);
        if (color == null && sColor != null)
            color = Color.decode(sColor);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color);
        
        if (author != null) embed.setAuthor(author);

        try {
            embed.setThumbnail(thumbnail == null ? null : thumbnail.asInputStream())
                    .setImage(image == null ? null : image.asInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new EmbedModal(embed).respondTo(interaction);
    }

    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        AutocompleteInteraction interaction = event.getAutocompleteInteraction();
        if (interaction.getCommandId() == getCommand().getId())
            interaction.respondWithChoices(choices);
    }
    
}
