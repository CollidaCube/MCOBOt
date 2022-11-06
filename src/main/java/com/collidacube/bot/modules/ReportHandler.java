package com.collidacube.bot.modules;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

import com.collidacube.bot.data.report.Report;

public class ReportHandler implements ButtonClickListener {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction interaction = event.getButtonInteraction();
        String customId = interaction.getCustomId();
        Report report = Report.getReportByReceiptId(interaction.getMessage().getIdAsString());
        if (report == null) return;
        
        if (customId.equals("delete")) {
            Message msg = report.getReportedMessage();
            if (msg != null)
                msg.delete("This message was deemed inappropriate.");
        }

        report.settle();
    }
    
}
