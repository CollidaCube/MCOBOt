package com.collidacube.bot.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.Attachment;
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
import com.collidacube.bot.exceptions.CouldntUpdateInfoException;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.DiscordBot;
import com.collidacube.javacordext.commands.command.Command;
import com.collidacube.javacordext.utils.Detect;

public class UpdateInfoCommand extends Command {

	public UpdateInfoCommand(DiscordBot bot) {
		super(bot, "updateinfo");
	}

	@Override
	public SlashCommandBuilder initSlashCommand() {
		List<SlashCommandOption> optionsList = Arrays.asList(
				SlashCommandOption.createWithOptions(
						SlashCommandOptionType.SUB_COMMAND,
						"id_number",
						"Their MCO Id Number",
						Arrays.asList(
								SlashCommandOption.createStringOption("id_number", "Their MCO Id Number", true),
								SlashCommandOption.createUserOption("User", "Participant to update info for", false)
						)
				),
				SlashCommandOption.createWithOptions(
						SlashCommandOptionType.SUB_COMMAND,
						"age",
						"Their age",
						Arrays.asList(
								SlashCommandOption.createStringOption("age", "Their age", true),
								SlashCommandOption.createUserOption("User", "Participant to update info for", false)
						)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"location",
					"The location they are participating from.",
					Arrays.asList(
						SlashCommandOption.createWithChoices(
								SlashCommandOptionType.STRING,
								"Location",
								"Where they are paricipating from",
								true,
								Location.getChoices()
						),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"position",
					"What role they play in MCO",
					Arrays.asList(
						SlashCommandOption.createWithChoices(
								SlashCommandOptionType.STRING,
								"Position",
								"What role they play in MCO",
								true,
								Position.getChoices()
						),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"first_name",
					"Their first name",
					Arrays.asList(
						SlashCommandOption.createStringOption("first_name", "Their first name", true),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"last_name",
					"Their last name",
					Arrays.asList(
						SlashCommandOption.createStringOption("last_name", "Their last name", true),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"alibi",
					"Their alibi",
					Arrays.asList(
						SlashCommandOption.createUserOption("alibi", "Their alibi", true),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"registrar",
					"The user who verified this person",
					Arrays.asList(
						SlashCommandOption.createUserOption("registrar", "The user who verified this person", true),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND,
					"avatar",
					"The image to show up for `/info`",
					Arrays.asList(
						SlashCommandOption.createAttachmentOption("Avatar", "The image to show up for `/info`", true),
						SlashCommandOption.createUserOption("User", "Participant to update info for", false)
					)
				)
		);

		return SlashCommand.with("updateinfo", "Update your `/info`", optionsList);
	}

	@Override
	public void onCommand(SlashCommandCreateEvent event) {
		SlashCommandInteraction interaction = event.getSlashCommandInteraction();
		User registrar = interaction.getUser();
		if (Bot.getHubServer().getRoles(registrar).contains(Bot.getVerifiedRole())) processUpdateInfoRequest(interaction, registrar);
		else {
			interaction.createImmediateResponder()
					.addEmbed(new EmbedBuilder()
							.setTitle("Error! No Permission!")
							.setDescription("You must be " + Bot.getVerifiedRole().getMentionTag() + " to update your info!")
							.setFooter("MCO Bot | Info Update")
							.setColor(Color.RED))
					.setFlags(MessageFlag.EPHEMERAL)
					.respond();
		}
	}

	public static final List<String> publicInfoTypes = Arrays.asList("age", "first_name", "last_name", "avatar");
	public void processUpdateInfoRequest(SlashCommandInteraction interaction, User registrar) {
		String infoType = interaction.getOptions().get(0).getName();
		if (!publicInfoTypes.contains(infoType) && !Bot.isAdmin(registrar)) {
			interaction.createImmediateResponder()
					.addEmbed(new EmbedBuilder()
							.setTitle("Error! No Permission!")
							.setDescription("You must be an " + Bot.getAdminRole().getMentionTag() + " to update this information! Please open a ticket.")
							.setFooter("MCO Bot | Info Update")
							.setColor(Color.RED))
					.setFlags(MessageFlag.EPHEMERAL)
					.respond();
			return;
		}

		List<SlashCommandInteractionOption> args = interaction.getArguments();
		User registrant = registrar;
		if (args.size() >= 2) {
			if (Bot.isAdmin(registrar)) registrant = args.get(1).getUserValue().orElse(null);
			else {
				interaction.createImmediateResponder()
					.addEmbed(new EmbedBuilder()
							.setTitle("Error! No Permission!")
							.setDescription("You must be an " + Bot.getAdminRole().getMentionTag() + " to update other's info!")
							.setFooter("MCO Bot | Info Update")
							.setColor(Color.RED))
					.setFlags(MessageFlag.EPHEMERAL)
					.respond();
				
				return;
			}
		}
		
		Participant participant = Participant.getByDiscordId(registrant.getIdAsString());
		if (participant == null) return;

		try {
			String response = updateInfoAndGetResponse(infoType, participant, interaction.getArguments().get(0));
			String desc = participant.getUser().getMentionTag() + "'s information has been successfully updated as follows:\n" + "• " + response;

			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Successful")
							.setDescription(desc)
							.setFooter("MCO Bot | Info Update")
							.setColor(Color.GREEN)
					).respond();
			
			LoggingCategory.REGISTRATION_LOGS.info(registrar, registrant, "Updated Information", desc);
		} catch (CouldntUpdateInfoException e) {
			interaction.createImmediateResponder()
					.setFlags(MessageFlag.EPHEMERAL)
					.addEmbed(new EmbedBuilder()
							.setTitle("Uh Oh! Something went wrong.")
							.setDescription(e.getMessage())
							.setFooter("MCO Bot | Info Update")
							.setColor(Color.RED)
					).respond();
		}
	}

	private String updateInfoAndGetResponse(String infoType, Participant participant, SlashCommandInteractionOption value) throws CouldntUpdateInfoException {
		String stringValue = value.getStringValue().orElse(null);
		User userValue = value.getUserValue().orElse(null);
		Attachment attachmentValue = value.getAttachmentValue().orElse(null);
		if      (infoType.equals("id_number")) return updateId(participant, stringValue);
		else if (infoType.equals("age")) return updateAge(participant, stringValue);
		else if (infoType.equals("location")) return updateLocation(participant, stringValue);
		else if (infoType.equals("position")) return updatePosition(participant, stringValue);
		else if (infoType.equals("first_name")) return updateFirstName(participant, stringValue);
		else if (infoType.equals("last_name")) return updateLastName(participant, stringValue);
		else if (infoType.equals("alibi")) return updateAlibi(participant, userValue);
		else if (infoType.equals("registrar")) return updateRegistrar(participant, userValue);
		else if (infoType.equals("avatar")) return updateAvatar(participant, attachmentValue);
		return "N/A";
	}

	private String updateLocation(Participant registrant, String locationId) throws CouldntUpdateInfoException {
		try {
			Location loc = Location.valueOf(locationId);
			registrant.setCurrentLocation(loc);
			return "LOCATION: `" + loc.label + "`";
		} catch (Exception e) {
			throw new CouldntUpdateInfoException("`" + locationId + "` is not a valid location!");
		}
	}

	private String updatePosition(Participant registrant, String positionId) throws CouldntUpdateInfoException {
		try {
			Position pos = Position.valueOf(positionId);
			registrant.setCurrentPosition(pos);
			return "POSITION: `" + pos.label + "`";
		} catch (Exception e) {
			throw new CouldntUpdateInfoException("`" + positionId + "` is not a valid position!");
		}
	}

	private String updateId(Participant registrant, String mcoId) throws CouldntUpdateInfoException {
		registrant.setIdNumber(mcoId);
		return "ID: `" + mcoId + "`";
	}

	private String updateAge(Participant registrant, String age) throws CouldntUpdateInfoException {
		try {
			int parsedAge = Integer.parseInt(age);
			registrant.setAge(parsedAge);
			return "AGE: `" + age + "`";
		} catch (NumberFormatException e) {
			throw new CouldntUpdateInfoException("The value you entered is invalid (`" + age + "`)! Please try again.");
		}
	}

	private String updateFirstName(Participant registrant, String firstName) throws CouldntUpdateInfoException {
		if (Detect.profanity(firstName)) throw new CouldntUpdateInfoException("The value entered contains profanity! If you believe this is an error, please open a ticket.");

		registrant.setFirstName(firstName);
		return "FIRST_NAME: `" + firstName + "`";
	}

	private String updateLastName(Participant registrant, String lastName) throws CouldntUpdateInfoException {
		if (Detect.profanity(lastName)) throw new CouldntUpdateInfoException("The value entered contains profanity! If you believe this is an error, please open a ticket.");
		
		registrant.setLastName(lastName);
		return "LAST_NAME: `" + lastName + "`";
	}
	
	public static final Role VERIFIED_ROLE = Bot.getVerifiedRole();
	private String updateAlibi(Participant registrant, User alibi) throws CouldntUpdateInfoException {
		if (!Bot.getHubServer().isMember(alibi)) throw new CouldntUpdateInfoException(alibi.getMentionTag() + " is not a member of this discord!");
		if (!Bot.getHubServer().getRoles(alibi).contains(Bot.getVerifiedRole())) throw new CouldntUpdateInfoException(alibi.getMentionTag() + " is not " + Bot.getVerifiedRole().getMentionTag() + "!");
		
		registrant.setAlibi(alibi);
		return "ALIBI: " + alibi.getMentionTag();
	}

	private String updateRegistrar(Participant registrant, User registrar) throws CouldntUpdateInfoException {
		if (!Bot.getHubServer().isMember(registrar)) throw new CouldntUpdateInfoException(registrar.getMentionTag() + " is not a member of this discord!");
		if (!Bot.getHubServer().getRoles(registrar).contains(Bot.getAdminRole())) throw new CouldntUpdateInfoException(registrar.getMentionTag() + " is not " + Bot.getAdminRole().getMentionTag() + "!");
		
		registrant.setRegistrar(registrar);
		return "REGISTRAR: " + registrar.getMentionTag();
	}

	private String updateAvatar(Participant registrant, Attachment avatar) throws CouldntUpdateInfoException {
		if (!avatar.isImage()) throw new CouldntUpdateInfoException("This attachment is not an image!");

		String url = avatar.getUrl().toExternalForm();
		registrant.setAvatarUrl(url);
		return "AVATAR: " + url;
	}

}
