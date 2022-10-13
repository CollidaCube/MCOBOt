package com.collidacube.bot.applications.responses;

import com.collidacube.bot.applications.Application;
import com.collidacube.bot.applications.Question;
import com.collidacube.bot.applications.ResponseManager;
import com.collidacube.bot.applications.ResponseType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

import java.util.concurrent.CompletableFuture;

public abstract class Response {

	protected CompletableFuture<Response> expectancy = null;
	protected Message requestMessage = null;
	protected final ResponseManager manager;
	protected final Application app;
	protected final Question question;
	protected final User user;
	protected Response(ResponseManager manager) {
		this.manager = manager;
		
		this.app = manager.getApplication();
		this.question = app.getQuestion();
		this.user = app.getApplicant();
	}

	public final void completeExpectancy() {
		if (!expectancy.isDone()) expectancy.complete(this);
	}

	public abstract void init();

	public abstract String getValue();

	public abstract void finish();

	public final Question getQuestion() {
		return question;
	}

	public final CompletableFuture<Response> expect(Message requestMessage) {
		finish();
		init();
		this.requestMessage = requestMessage;
		return expectancy = new CompletableFuture<>();
	}

	public static Response awaitResponse(ResponseManager manager) {
		ResponseType type = manager.getApplication().getQuestion().getExpectedResponse();
		if (type == ResponseType.BUTTON) 			return new ButtonResponse		(manager);
		else if (type == ResponseType.REACTION) 	return new ReactionResponse		(manager);
		else if (type == ResponseType.SELECT_MENU) 	return new SelectMenuResponse	(manager);
		else if (type == ResponseType.ATTACHMENT) 	return new AttachmentResponse	(manager);
		else 										return new MessageResponse		(manager);
	}

}
