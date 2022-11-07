package com.collidacube.bot;

import java.util.Scanner;
import java.util.Set;

import com.collidacube.javacordext.Configuration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommand;

import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.DiscordBotBuilder;
import com.collidacube.javacordext.data.DataManager;
import com.collidacube.javacordext.utils.async.Scheduler;
import com.collidacube.javacordext.utils.log.LogMode;
import com.collidacube.javacordext.utils.log.Logger;
import com.collidacube.javacordext.utils.specialized.Utils;

public class Bot {

	public static Server getHubServer() {
		return api.getServerById(1007656307450466365L).orElse(null);
	}

	public static ServerTextChannel getTextChannelById(long id) {
		return getHubServer().getTextChannelById(id).orElse(null);
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

	public static final Configuration config = Configuration.GLOBAL.load("D:\\Projects\\MCO Discord\\Data\\config.txt");

	public static final DiscordApi api = new DiscordApiBuilder()
												.setAllIntents()
												.setToken(config.getProperty("discordToken2"))
												.login().join();

	public static final DiscordBot bot = new DiscordBotBuilder()
											.setDataPath("D:\\Projects\\MCO Discord\\Data\\")
											.setId("MCO Bot")
											.build(api);
	
	public static final Logger logger = bot.logger;
	public static final Scheduler scheduler = new Scheduler();

	public static void main(String[] args) {
		logger.log(LogMode.INFO, "Loading...");
		Initialize.all();
		scheduler.start();

		logger.log(LogMode.SUCCESS, "Ready!");
		
		waitForInput();
		exit();
	}

	private static void exit() {
		logger.log(LogMode.INFO, "Saving...");
		api.disconnect();
		DataManager.saveAll();
		scheduler.interrupt();
		System.exit(0);
	}

	public static void reloadSlashCommands() {
		Set<SlashCommand> commands = Utils.await(api.getGlobalSlashCommands());
		for (SlashCommand cmd : commands) cmd.deleteGlobal();
	}

	public static void waitForInput() {
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
	}

}
