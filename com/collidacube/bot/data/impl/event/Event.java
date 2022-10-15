package com.collidacube.bot.data.impl.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.DataManager;
import com.collidacube.bot.data.DataPackage;
import com.collidacube.bot.utils.specialized.Utils;

public class Event extends DataPackage<Event> {

    private static final HashMap<String, Event> eventsByCode = new HashMap<>();
    private static final HashMap<String, Event> eventsByLabel = new HashMap<>();
    private static final ChannelCategory notificationsCategory = Bot.getHubServer().getChannelCategoryById("1007671607982030979").orElse(null);

    public static final DataManager<Event> DATA_MANAGER = new DataManager<>(Event.class, Event::loadFrom, "D:\\NewDrive\\Projects\\MCO Discord\\Data\\Events.db",
                                                                "label",
                                                                "code",
                                                                "channelId");

    public static Event loadFrom(HashMap<String, String> data) {
        String label = data.get("label");
        String code = data.get("code");
        String channelId = data.get("channelId");
        return new Event(label, code, channelId);
    }

    public static Event getEventByCode(String code) {
        return eventsByCode.get(code);
    }

    public static Event getEventByLabel(String label) {
        return eventsByLabel.get(label);
    }

    public static Set<String> getActiveEvents() {
        return eventsByLabel.keySet();
    }

    public static List<SlashCommandOptionChoice> getChoices() {
        List<SlashCommandOptionChoice> choices = new ArrayList<>();
        for (Event event : eventsByLabel.values()) choices.add(SlashCommandOptionChoice.create(event.label, event.label));
        return choices;
    }

    private String label = null;
    private String code = null;
    private ServerTextChannel channel = null;
    private Event(String label, String code, String channelId) {
        super(Event.class, DATA_MANAGER);

        setLabel(label);
        setCode(code);
        setChannel(channelId);
        
        if (channel == null) createChannel();
    }

    public Event(String label) {
        this(label, Utils.generateCode(6), null);
    }
    
    public void setLabel(String label) {
        eventsByLabel.remove(this.label);
        this.label = label;
        eventsByLabel.put(label, this);
    }

    public void regenerateCode() {
        setCode(Utils.generateCode(6));
    }

    private void setCode(String code) {
        Utils.changeKey(eventsByCode, this.code, code);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setChannel(String channelId) {
        Bot.getHubServer()
                .getTextChannelById(channelId)
                .ifPresent(this::setChannel);
    }

    public CompletableFuture<ServerTextChannel> createChannel() {
        return createChannel("📣" + label.toLowerCase().replaceAll(" ", "-"));
    }

    public CompletableFuture<ServerTextChannel> createChannel(String title) {
        return Bot.getHubServer().createTextChannelBuilder()
                .setCategory(notificationsCategory)
                .setName(title)
                .create()
                .whenComplete(this::setChannel);
    }

    public void setChannel(ServerTextChannel channel, Throwable error) {
        if (error == null) setChannel(channel);
        else error.printStackTrace();
    }

    public void setChannel(ServerTextChannel channel) {
        this.channel = channel;
        channel.updateRawPosition(1);
    }

    public void close() {
        eventsByCode.remove(code);
        eventsByLabel.remove(label);
        DATA_MANAGER.unregister(this);
        channel.delete("Event is closed!");
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }

    public ServerTextChannel getChannel() {
        return channel;
    }

    @Override
    public HashMap<String, String> getData() {
        if (!eventsByCode.containsKey(code)) return null;
        return Utils.parseToMap("label:" + label + ",code:" + code + ",channelId:" + channel.getIdAsString(), ",", ":");
    }
    
}
