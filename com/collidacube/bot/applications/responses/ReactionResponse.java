package com.collidacube.bot.applications.responses;

import com.collidacube.bot.applications.ResponseManager;

import org.javacord.api.entity.message.Reaction;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ReactionResponse extends Response {

    protected ReactionResponse(ResponseManager manager) {
        super(manager);
    }

    @Override
    public void init() {
        // TODO: add initial reactions
    }

    @Override
    public String getValue() {
        List<Reaction> reactions = requestMessage.getReactions();
        if (reactions == null || reactions.size() == 0) return null;

        return reactions.stream()
                .filter(reaction -> {
                    try {
                        return reaction.getUsers().get().contains(user);
                    } catch (InterruptedException | ExecutionException | CancellationException e) {
                        return false;
                    }
                })
                .map(reaction -> reaction.getEmoji().getMentionTag())
                .collect(Collectors.joining(", "));
    }

    @Override
    public void finish() {}
    
}
