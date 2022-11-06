package com.collidacube.bot.applications;

import java.util.Arrays;

import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.javacordext.applications.Question;
import com.collidacube.javacordext.applications.ResponseType;
import com.collidacube.javacordext.applications.templates.ApplicationTemplate;

public class StaffApplication extends ApplicationTemplate {

    public StaffApplication() {
        super(
            "Staff",
            true,
            false,
            Question.of(
                "How old are you?",
                ResponseType.SELECT_MENU,
                SelectMenu.create(
                    "age-range",
                    "Please select your age range...",
                    Arrays.asList(
                        SelectMenuOption.create("1-12", "1-12"),
                        SelectMenuOption.create("13-18", "13-18"),
                        SelectMenuOption.create("19-30", "19-30"),
                        SelectMenuOption.create("31+", "31+")
                    )
                )
            )
        );
    }

    @Override
    public boolean onApplicationStart(User user, String applicationId) {
        return Bot.getVerifiedRole().hasUser(user);
    }
    
}
