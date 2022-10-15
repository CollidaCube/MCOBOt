package com.collidacube.bot;

import com.collidacube.bot.commands.UpdateInfoCommand;
import com.collidacube.bot.commands.ApplyCommand;
import com.collidacube.bot.commands.EventCommand;
import com.collidacube.bot.commands.InfoCommand;
import com.collidacube.bot.commands.VerifyCommand;
import com.collidacube.bot.data.impl.event.Event;
import com.collidacube.bot.data.impl.participant.Participant;
import com.collidacube.bot.modules.ProfanityPrevention;
import com.collidacube.bot.modules.Verification;
import com.collidacube.bot.utils.log.Logger;
import com.collidacube.bot.utils.log.LogMode;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommand;

import java.util.List;
import java.util.Scanner;

public class Bot {

	public static Server getHubServer() {
		return api.getServerById(1007656307450466365L).orElse(null);
	}

	public static Role getAdminRole() {
		return getHubServer().getRoleById(1007657067248631809L).orElse(null);
	}

	public static Role getVerifiedRole() {
		return Bot.getHubServer().getRoleById(1007657533370024056L).orElse(null);
	}

	public static boolean isAdmin(User user) {
		return user.getRoles(getHubServer()).contains(getAdminRole());
	}

	public static final DiscordApi api = new DiscordApiBuilder()
			.setAllIntents()
			.setToken(Properties.DISCORD_TOKEN)
			.login().join();

	public static void main(String[] args) {
		Logger.log(LogMode.INFO, "Loading...");
		// reloadSlashCommands();

		Logger.log(LogMode.INFO, "Initializing commands...");
		new VerifyCommand();
		new UpdateInfoCommand();
		new InfoCommand();
		new ApplyCommand();
		new EventCommand();

		Logger.DEBUG_MODE = true;

		Logger.log(LogMode.INFO, "Initializing modules...");
		Logger.log(LogMode.DEBUG, "*:: Verification");
		api.addServerMemberJoinListener(new Verification());
		Logger.log(LogMode.DEBUG, "*:: ProfanityPrevention");
		api.addMessageCreateListener(new ProfanityPrevention());
		Logger.log(LogMode.DEBUG, "*:: EventCommand");
		api.addAutocompleteCreateListener(new EventCommand());
		Logger.log(LogMode.DEBUG, "");
		
		Logger.DEBUG_MODE = false;

		Logger.log(LogMode.INFO, "Loading data...");
		Participant.DATA_MANAGER.toString();
		Event.DATA_MANAGER.toString();

		Logger.log(LogMode.INFO, "Registering shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Logger.log(LogMode.INFO, "Saving...");
			api.disconnect();
			Participant.DATA_MANAGER.save();
			Event.DATA_MANAGER.save();
		}));

		Logger.log(LogMode.SUCCESS, "Ready!");
		waitForInput();
		System.exit(1);
	}

	public static void reloadSlashCommands() {
		List<SlashCommand> commands = api.getGlobalSlashCommands().join();
		for (SlashCommand cmd : commands) cmd.deleteGlobal();
	}

	public static void waitForInput() {
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
	}

}
