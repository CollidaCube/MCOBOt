package com.collidacube.bot.applications;

import com.collidacube.bot.applications.responses.Response;
import com.collidacube.bot.utils.specialized.Utils;

import org.javacord.api.entity.message.Message;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ResponseManager {
    
    private final Application app;
    private final HashMap<Question, Response> answers = new HashMap<>();
    public ResponseManager(Application app) {
        this.app = app;
    }

    public Application getApplication() {
        return app;
    }

    public CompletableFuture<Response> ask(Question question, String title) {
        CompletableFuture<Message> cf_msg = question.ask(app.getApplicant(), title);
        Message msg = Utils.await(cf_msg);
        return answers.computeIfAbsent(question, q -> Response.awaitResponse(this)).expect(msg);
    }

    public Response getResponse(Question q) {
        return answers.get(q);
    }

    public boolean hasResponse(Question q) {
        Response r = getResponse(q);
        return r != null && (r.getValue() != null);
    }

    public void clean() {
        for (Response response : answers.values()) response.finish();
    }

}
