package com.collidacube.bot.listeners.application;

import org.javacord.api.entity.user.User;

import com.collidacube.javacordext.listeners.Listener;

public interface ApplicationStartListener extends Listener {
    public boolean onApplicationStart(User user, String applicationId);
}
