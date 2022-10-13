package com.collidacube.bot.applications;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Application {

	private static final HashMap<User, CompletableFuture<Application>> applications = new HashMap<>();
	public static CompletableFuture<Application> getApplication(User user) {
		return applications.get(user);
	}

	private final CompletableFuture<Application> appFuture;
	private final ResponseManager responses = new ResponseManager(this);
	private final User user;
	private final Question[] questions;
	private int questionNumber;
	public Application(User user, Question[] questions, CompletableFuture<Application> appFuture) {
		this.appFuture = appFuture;
		this.user = user;
		this.questions = questions;
		assessCompletion();
		appFuture.whenComplete((app, throwable) -> {
			System.out.println(appFuture.isCancelled());
			finish();
		});
	}

	public void sendNextQuestion() {
		if (appFuture.isDone()) return;

		while (responses.hasResponse(getQuestion())) {
			if (++questionNumber == questions.length) {
				assessCompletion();
				return;
			}
		}
		
		responses.ask(getQuestion(), "Question #" + (questionNumber + 1))
			.whenComplete((res, err) -> {
				if (err != null) return;
				sendNextQuestion();
			});
	}

	private Message confirmSubmissionMessage = null;
	private ButtonClickListener confirmListener = null;
	public void assessCompletion() {
		if (appFuture.isDone()) return;

		for (Question question : questions) {
			if (!responses.hasResponse(question)) {
				questionNumber = 0;
				sendNextQuestion();

				if (confirmSubmissionMessage != null) {
					confirmSubmissionMessage.removeListener(ButtonClickListener.class, confirmListener);
					confirmSubmissionMessage = null;
				}
				return;
			}
		}

		if (confirmSubmissionMessage == null)
			new MessageBuilder()
					.addActionRow(Button.create("submit", ButtonStyle.SUCCESS, "Submit"), Button.create("cancel", ButtonStyle.DANGER, "Cancel"))
					.addEmbed(new EmbedBuilder()
							.setTitle("Confirm Submit")
							.setDescription("Please check over your answers and make sure they are what you wish to submit. If you want to change your answer, you may edit your messages or select new options and your changes will be saved.\n**Once you are ready, please click 'Submit'.**")
							.setFooter("API | Applications")
							.setColor(Color.BLUE)
					).send(user)
					.whenComplete(this::requestCompletion);
		else {
			appFuture.complete(this);
			user.sendMessage(new EmbedBuilder()
					.setTitle("Submitted")
					.setDescription("Your application has been submitted! Please do not bother us about it! You will be notified when you are rejected/accepted.")
					.setFooter("API | Applications")
					.setColor(Color.GREEN));
		}
	}

	public void requestCompletion(Message msg, Throwable err) {
		if (err != null) throw new RuntimeException(err.getMessage());
		
		confirmSubmissionMessage = msg;
		confirmListener = event -> {
			if (appFuture.isDone()) return;
			ButtonInteraction interaction = event.getButtonInteraction();
			String id = interaction.getCustomId();
			if (id.equals("submit")) assessCompletion();
			else if (id.equals("cancel")) {
				appFuture.completeExceptionally(new Throwable("cancelled"));
				EmbedBuilder embed = new EmbedBuilder().setTitle("Cancelled").setDescription("Your application has been cancelled. You are free to open a new one using `/apply`.").setFooter("API | Applications").setColor(Color.RED);
				user.sendMessage(embed);
			}
			else return;

			interaction.acknowledge();
		};

		confirmSubmissionMessage.addButtonClickListener(confirmListener);
	}

	private boolean finished = false;
	public void finish() {
		if (finished) return;
		else appFuture.complete(this);
		finished = true;

		if (confirmSubmissionMessage != null) confirmSubmissionMessage.removeMessageAttachableListener(confirmListener);
		applications.remove(user);
		responses.clean();
	}

	public User getApplicant() {
		return user;
	}

	public Question getQuestion() {
		return getQuestion(questionNumber);
	}

	public Question getQuestion(int questionNumber) {
		if (questionNumber == -1) return null;
		return questions[questionNumber];
	}

	public static CompletableFuture<Application> startApplication(User user, Question... questions) {
		if (user == null) return null;
		if (getApplication(user) != null) return null;

		CompletableFuture<Application> appFuture = new CompletableFuture<>();
		new Application(user, questions, appFuture);
		applications.put(user, appFuture);
		return appFuture;
	}

	public ResponseManager getResponses() {
		return responses;
	}
}
