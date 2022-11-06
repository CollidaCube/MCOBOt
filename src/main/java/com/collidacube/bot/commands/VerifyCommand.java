package com.collidacube.bot.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.participant.Location;
import com.collidacube.bot.data.participant.Participant;
import com.collidacube.bot.data.participant.Position;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;

public class VerifyCommand extends Command {

	public VerifyCommand(DiscordBot bot) {
		super(bot, "verify");
	}

	@Override
	public SlashCommandBuilder initSlashCommand() {
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
				),
				SlashCommandOption.createStringOption("mcoId", "The number on the back of the nametag", true),
				SlashCommandOption.createStringOption("fullName", "Their first/last name separated by a space", true)
		);

		return SlashCommand.with("verify", "Verify a new MCO participant.", optionsList);
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

		String locationId 	= args.get(1).getStringValue().orElse(null);
		String positionId 	= args.get(2).getStringValue().orElse(null);
		String mcoId 		= args.get(3).getStringValue().orElse(null);
		String fullName		= args.get(4).getStringValue().orElse(null);

		Location location = Location.valueOf(locationId);
		Position position = Position.valueOf(positionId);

		int idx = fullName.indexOf(" ");
		String firstName = fullName.substring(0, idx);
		String lastName = fullName.substring(idx+1);

		int response = register(registrar, registrant, location, position, mcoId, firstName, lastName);
		if (response == 1) {
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Error Occurred")
							.setDescription("Uh Oh! An error occured. **Report this to an admin immediately.**")
							.setFooter("MCO Bot | Failed Verification")
							.setColor(Color.RED)
					).respond();
		}
		else if (response == 2) {
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Already Verified")
							.setDescription("This user is already verified! If they are not registered in the system, please revoke that role.")
							.setFooter("MCO Bot | Failed Verification")
							.setColor(Color.RED)
					).respond();
		}
		else if (response == 3) {
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("McoID Taken")
							.setDescription("There is already a user with this McoId!")
							.setFooter("MCO Bot | Failed Verification")
							.setColor(Color.RED)
					).respond();
		}
		else {
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Successful")
							.setDescription(registrant.getMentionTag() + " was successfully registered!")
							.setFooter("MCO Bot | Successful Verification")
							.setColor(Color.GREEN)
					).respond();
		}
	}

	
	public static final Role VERIFIED_ROLE = Bot.getVerifiedRole();
	public int register(User registrar, User registrant, Location location, Position position, String mcoId, String firstName, String lastName) {
		if (Bot.getHubServer().getRoles(registrant).contains(VERIFIED_ROLE))
			return 2;
		if (Participant.getByMcoId(mcoId) != null) return 3;

		try {
			registrant.addRole(VERIFIED_ROLE);
			registrant.addRole(location.role);
			registrant.addRole(position.role);

			new Participant(registrant, registrar, location, position)
					.setIdNumber(mcoId)
					.setFirstName(firstName)
					.setLastName(lastName);

			logRegistration(registrar, registrant, location, position, mcoId, firstName, lastName);
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

	public void logRegistration(User registrar, User registrant, Location location, Position position, String mcoId, String firstName, String lastName) {
		String desc = "Successfully Verified " + registrant.getMentionTag()
					+ "\n• LOCATION: `" + location.label + "`"
					+ "\n• POSITION: `" + position.label + "`"
					+ "\n• MCOID: `REDACTED`"
					+ "\n• FIRST_NAME: `" + firstName + "`"
					+ "\n• LAST_NAME: `" + lastName + "`";

		LoggingCategory.REGISTRATION_LOGS.success(registrar, registrant, "Verified User", desc);
	}

}
