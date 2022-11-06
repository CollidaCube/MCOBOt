package com.collidacube.bot.modules;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import com.collidacube.bot.Bot;
import com.collidacube.javacordext.applications.Application;

public class Verification implements ServerMemberJoinListener {

    public static final Role PRIVILEGED_ROLE = Bot.api.getRoleById(1034680379333939200L).orElse(null);

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        event.getUser().addRole(PRIVILEGED_ROLE);
        Application.startApplication(event.getUser(), "Verification");
    }

}
