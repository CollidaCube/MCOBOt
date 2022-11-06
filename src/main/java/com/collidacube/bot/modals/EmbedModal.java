package com.collidacube.bot.modals;

import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.ModalInteraction;

public class EmbedModal extends ModalLegacy {

    private final EmbedBuilder embed;
    public EmbedModal(EmbedBuilder embed) {
        super("Embed Builder", "embed-builder",
                TextInput.create(TextInputStyle.SHORT, "title", "Title", true),
                TextInput.create(TextInputStyle.SHORT, "url", "URL"),
                TextInput.create(TextInputStyle.PARAGRAPH, "description", "Description"),
                TextInput.create(TextInputStyle.PARAGRAPH, "fields", "Fields", "Separate fields with newlines, inline fields with commas. Fields should be formatted like name:desc", ""),
                TextInput.create(TextInputStyle.SHORT, "footer", "Footer")
        );
        this.embed = embed;
    }

    @Override
    public void onSubmit(ModalInteraction interaction) {
        String title = interaction.getTextInputValueByCustomId("title").orElse(null);
        String url = interaction.getTextInputValueByCustomId("url").orElse(null);
        String description = interaction.getTextInputValueByCustomId("description").orElse(null);
        String fields = interaction.getTextInputValueByCustomId("fields").orElse(null);
        String footer = interaction.getTextInputValueByCustomId("footer").orElse(null);

        for (String field : fields.split("\n")) {
            boolean inline = false;
            for (String inlineField : field.split(",")) {
                int idx = inlineField.indexOf(":");
                if (idx == -1) continue;
                String key = inlineField.substring(0, idx);
                String value = inlineField.substring(idx+1);
                embed.addField(key, value, inline);
                inline = true;
            }
        }

        embed.setTitle(title);
        embed.setUrl(url);
        embed.setDescription(description);
        embed.setFooter(footer);

        interaction.createImmediateResponder().addEmbed(embed).respond();
    }
    
}
