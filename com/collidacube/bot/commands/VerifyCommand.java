package com.collidacube.bot.commands;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.impl.participant.Location;
import com.collidacube.bot.data.impl.participant.Participant;
import com.collidacube.bot.data.impl.participant.Position;
import com.collidacube.bot.utils.LoggingChannel;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static com.collidacube.bot.Bot.api;

public class VerifyCommand extends Command {

	public VerifyCommand() {

	}

	@Override
	public SlashCommand initSlashCommand() {
		List<SlashCommandOption> optionsList = Arrays.asList(
				SlashCommandOption.create(SlashCommandOptionType.USER, "User", "Verify a user", true),
				SlashCommandOption.createWithChoices(
						SlashCommandOptionType.STRING,
						"Location",
						"Where they are paricipating from",
						true,
						Location.getChoices()
				),
				SlashCommandOption.createWithChoices(
						SlashCommandOptionType.STRING,
						"Position",
						"What role they play in MCO",
						true,
						Position.getChoices()
				)
		);

		SlashCommandBuilder builder = SlashCommand.with("verify", "Verify a new MCO participant.", optionsList);
		return builder.createGlobal(api).join();
	}

	@Override
	public void onCommand(SlashCommandCreateEvent event) {
		SlashCommandInteraction interaction = event.getSlashCommandInteraction();
		User registrar = interaction.getUser();
		if (Bot.isAdmin(registrar)) processRegistrationRequest(interaction, registrar);
		else {
			interaction
					.createImmediateResponder()
					.addEmbed(new EmbedBuilder()
							.setTitle("Error! No Permission!")
							.setDescription("You must be an <@&1007657067248631809> to verify someone!")
							.setFooter("MCO Bot | Verification Failure")
							.setColor(Color.RED))
					.setFlags(MessageFlag.EPHEMERAL)
					.respond();
		}
	}

	public void processRegistrationRequest(SlashCommandInteraction interaction, User registrar) {
		List<SlashCommandInteractionOption> args = interaction.getArguments();
		User registrant = args.get(0).getUserValue().orElse(null);
		if (registrant == null) return;

		String locationId = args.get(1).getStringValue().orElse(null);
		String positionId = args.get(2).getStringValue().orElse(null);

		Location location = Location.valueOf(locationId);
		Position position = Position.valueOf(positionId);

		boolean successful = register(registrar, registrant, location, position);
		System.out.println(successful);
		if (successful) {
			System.out.println("Succeeded");
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Successful")
							.setDescription(registrant.getMentionTag() + " was successfully registered!")
							.setFooter("MCO Bot | Successful Verification")
							.setColor(Color.GREEN)
					).respond();
		} else {
			System.out.println("Failed");
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Failure")
							.setDescription("Is " + registrant.getMentionTag() + " already verified? If so, please use the `/register` command.\n"
									+ "If not, please contact <@319978824110571520>. This may be an **error**!"
							).setFooter("MCO Bot | Failed Verification")
							.setColor(Color.RED)
					).respond();
		}
	}

	
	public static final Role VERIFIED_ROLE = Bot.getVerifiedRole();
	public boolean register(User registrar, User registrant, Location location, Position position) {
		if (Bot.getHubServer().getRoles(registrant).contains(VERIFIED_ROLE))
			return false;

		try {
			registrant.addRole(VERIFIED_ROLE);
			registrant.addRole(location.role);
			registrant.addRole(position.role);

			new Participant(registrant, registrar, location, position);

			logRegistration(registrar, registrant, location, position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static final LoggingChannel logger = LoggingChannel.REGISTRATION_LOGS;
	public void logRegistration(User registrar, User registrant, Location location, Position position) {
		logger.log(registrar,
				"Verified User",
				 "Successfully Verified " + registrant.getMentionTag() + "\n"
						+ "• LOCATION: `" + location.label + "`\n"
					    + "• POSITION: `" + position.label + "`",
				Color.GREEN);
	}

}
