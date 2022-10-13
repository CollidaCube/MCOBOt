package com.collidacube.bot.applications.responses;

import com.collidacube.bot.applications.ResponseManager;
import org.javacord.api.entity.message.MessageAttachment;

import java.util.List;
import java.util.stream.Collectors;

public class AttachmentResponse extends MessageResponse {

    protected AttachmentResponse(ResponseManager manager) {
        super(manager);
    }

    @Override
    public String getValue() {
        if (message == null) return null;

        List<MessageAttachment> attachments = message.getAttachments();
        if (attachments == null || attachments.size() == 0) return null;

        return attachments.stream()
                .map(attachment -> attachment.getUrl().toExternalForm())
                .collect(Collectors.joining("\n"));
    }
    
}
