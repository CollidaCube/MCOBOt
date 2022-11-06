package com.collidacube.bot;

import com.collidacube.bot.applications.StaffApplication;
import com.collidacube.bot.applications.VerificationApplication;
import com.collidacube.bot.commands.ApplyCommand;
import com.collidacube.bot.commands.EmbedCommand;
import com.collidacube.bot.commands.EventCommand;
import com.collidacube.bot.commands.InfoCommand;
import com.collidacube.bot.commands.PunishCommand;
import com.collidacube.bot.commands.SaveAllCommand;
import com.collidacube.bot.commands.UpdateInfoCommand;
import com.collidacube.bot.commands.VerifyCommand;
import com.collidacube.bot.contextmenu.ReportMessage;
import com.collidacube.bot.data.event.Event;
import com.collidacube.bot.data.participant.Participant;
import com.collidacube.bot.data.punishments.Punishment;
import com.collidacube.bot.data.report.Report;
import com.collidacube.bot.modules.ProfanityPrevention;
import com.collidacube.bot.modules.ReportHandler;
import com.collidacube.bot.modules.Verification;
import com.collidacube.bot.modules.logging.DiscordLogger;
import com.collidacube.bot.modules.logging.UserLogger;
import com.collidacube.javacordext.utils.log.LogMode;

public class Initialize {

    public static void all() {
        Initialize.applicationTemplates();
		Initialize.commands();
		Initialize.contextMenuCommands();
		Initialize.modules();
		Initialize.data();
    }

    public static void applicationTemplates() {
        Bot.logger.log(LogMode.INFO, "*:: Loading application templates...");
        new StaffApplication();
        new VerificationApplication();
    }

    public static void commands() {
        Bot.logger.log(LogMode.INFO, "*:: Loading commands...");
        new ApplyCommand(Bot.bot);
        new EmbedCommand(Bot.bot);
        new EventCommand(Bot.bot);
        new InfoCommand(Bot.bot);
        new PunishCommand(Bot.bot);
        new SaveAllCommand(Bot.bot);
        new UpdateInfoCommand(Bot.bot);
        new VerifyCommand(Bot.bot);
    }

    public static void contextMenuCommands() {
        Bot.logger.log(LogMode.INFO, "*:: Loading context menu commands...");
        new ReportMessage(Bot.bot);
    }

    public static void modules() {
        Bot.logger.log(LogMode.INFO, "Initializing modules...");
		Bot.api.addServerMemberJoinListener(new Verification());
		Bot.api.addMessageCreateListener(new ProfanityPrevention());
		Bot.api.addButtonClickListener(new ReportHandler());

		DiscordLogger.init();
		UserLogger.init();
    }

    public static void data() {
        Bot.logger.log(LogMode.INFO, "*:: Loading data...");
        Event.DATA_MANAGER.load();
        Participant.DATA_MANAGER.load();
        Punishment.DATA_MANAGER.load();
        Report.DATA_MANAGER.load();
    }
    
}
