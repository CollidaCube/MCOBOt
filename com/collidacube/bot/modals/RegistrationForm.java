package com.collidacube.bot.modals;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.awt.*;
import java.util.Arrays;

@Deprecated
public class RegistrationForm extends ModalForm<SlashCommandInteraction> {

	public final User registrant;
	public final User registrar;
	public RegistrationForm(SlashCommandInteraction interaction, User registrar, User registrant) {
		super(
				interaction,
				"Registration Form",
				ActionRow.of(
						TextInput.create(TextInputStyle.SHORT, "full-name", "Full Name", "John Doe", "", true),
						SelectMenu.create(
								"campus",
								"Campus",
								Arrays.asList(
										SelectMenuOption.create("Arizona", "Arizona"),
										SelectMenuOption.create("California", "California"),
										SelectMenuOption.create("Idaho", "Idaho"),
										SelectMenuOption.create("Austin, Texas", "Texas-Austin"),
										SelectMenuOption.create("Dallas, Texas", "Texas-Dallas"),
										SelectMenuOption.create("Utah", "Utah")
								)
						),
						SelectMenu.create(
								"occupation",
								"Occupation",
								Arrays.asList(
										SelectMenuOption.create("Millennial Chorus", "MC"),
										SelectMenuOption.create("Youth Chorus", "YC"),
										SelectMenuOption.create("Concert Choir Women", "CCW"),
										SelectMenuOption.create("Concert Choir Men", "CCM"),
										SelectMenuOption.create("Grand Chorus", "GC"),
										SelectMenuOption.create("Orchestra", "ORCHESTRA"),
										SelectMenuOption.create("Volunteer", "VOLUNTEER"),
										SelectMenuOption.create("Staff", "STAFF"),
										SelectMenuOption.create("Director", "DIRECTOR")
								)
						)
				)
		);

		this.registrant = registrant;
		this.registrar = registrar;
	}

	private String fullName = null;
	// private String campus = null;
	// private String occupation = null;

	@Override
	public void onSubmitForm(ModalSubmitEvent event) {
		ModalInteraction interaction = event.getModalInteraction();
		User user = interaction.getUser();
		if (user.getId() != registrar.getId()) return;

		fullName = interaction.getTextInputValueByCustomId("full-name").orElse(null);
		for (String s : interaction.getTextInputValues()) {
			System.out.println(s);
		}

		InteractionImmediateResponseBuilder response = interaction.createImmediateResponder();
		if (fullName == null || fullName.length() < 5)  {
			System.out.println("Could not process form!");
			response.setContent("Sorry! We could not process this form.");
		} else onSuccess(response);
		response.setFlags(MessageFlag.EPHEMERAL).respond();
	}

	public void sendSuccessfulResponse(InteractionImmediateResponseBuilder response) {
		response.addEmbed(
				new EmbedBuilder()
						.setTitle("Registration Form")
						.setAuthor(registrar)
						.setDescription("You have successfully registered " + registrant.getMentionTag() + "! Here is the recorded info:")
						.addField("Full Name", fullName)
						.setFooter("Millennial Choirs & Orchestras")
						.setColor(Color.GREEN)
		);
	}

	public void onSuccess(InteractionImmediateResponseBuilder response) {
		sendSuccessfulResponse(response);
		finish();
	}

}
