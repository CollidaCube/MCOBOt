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
		// reloadSlashCommands();

		new VerifyCommand();
		new UpdateInfoCommand();
		new InfoCommand();
		new ApplyCommand();
		new EventCommand();

		api.addServerMemberJoinListener(new Verification());
		api.addMessageCreateListener(new ProfanityPrevention());
		api.addAutocompleteCreateListener(new EventCommand());

		Participant.DATA_MANAGER.toString();
		Event.DATA_MANAGER.toString();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Saving");
			api.disconnect();
			Participant.DATA_MANAGER.save();
			Event.DATA_MANAGER.save();
		}));

		System.out.println("Ready");
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
