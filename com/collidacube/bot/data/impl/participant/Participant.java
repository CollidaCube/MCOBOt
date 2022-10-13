package com.collidacube.bot.data.impl.participant;

import com.collidacube.bot.Bot;
import com.collidacube.bot.data.DataManager;
import com.collidacube.bot.data.DataPackage;
import org.javacord.api.entity.user.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Participant extends DataPackage<Participant> {

	private static final HashMap<String, Participant> participantsByMcoId = new HashMap<>();
	private static final HashMap<String, Participant> participantsByDiscordId = new HashMap<>();

	public static final DataManager<Participant> DATA_MANAGER = new DataManager<>(Participant.class, Participant::loadFrom, "D:\\NewDrive\\Projects\\MCO Discord\\Data\\Participants.db",
																		"id",
																		"discordId",
																		"registrarId",
																		"firstName",
																		"lastName",
																		"age",
																		"ageLastUpdated",
																		"currentLocation",
																		"currentPosition",
																		"allLocations",
																		"allPositions",
																		"alibi",
																		"avatarUrl",
																		"participatedEvents");

	private static Participant loadFrom(HashMap<String, String> data) {
		Participant participant = new Participant(
				Bot.api.getUserById(data.get("discordId")).join(),
				null,
				Location.valueOf(data.get("currentLocation")),
				Position.valueOf(data.get("currentPosition"))
		)
				.setFirstName(data.get("firstName"))
				.setLastName(data.get("lastName"))
				.setAge(data.get("age"));

		String asciiArrAsStr = data.get("id");
		if (!isNull(asciiArrAsStr)) {
			String[] asciiArr = asciiArrAsStr.split(",");
			StringBuilder idNumber = new StringBuilder();
			for (String ascii : asciiArr) {
				idNumber.append((char) Integer.parseInt(ascii));
			}
			participant.idNumber = idNumber.toString();
		}

		String registrarId = data.get("registrarId");
		if (!isNull(registrarId)) participant.setRegistrar(Bot.api.getUserById(registrarId).join());

		String alibiId = data.get("alibi");
		if (!isNull(alibiId)) participant.setAlibi(Bot.api.getUserById(alibiId).join());

		participant.lastUpdated = Long.parseLong(data.get("ageLastUpdated"));

		String allLocations = data.get("allLocations");
		if (!isNull(allLocations)) for (String loc : allLocations.split(",")) {
				participant.addLocation(Location.valueOf(loc));
		}

		String allPositions = data.get("allPositions");
		if (!isNull(allPositions)) for (String pos : allPositions.split(",")) {
			participant.addPosition(Position.valueOf(pos));
		}

		String url = data.get("avatarUrl");
		if (!isNull(url)) participant.setAvatarUrl(url);

		String participatedEvents = data.get("participatedEvents");
		if (!isNull(participatedEvents)) for (String event : data.get("participatedEvents").split(","))
			participant.addParticipation(event);

		return participant;
	}

	private static MessageDigest getHashingAlgorithm() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} return null;
	}

	public static String hash(String str) {
		MessageDigest hash = getHashingAlgorithm();
		if (hash == null) return null;

		hash.update(str.getBytes());
		return new String(hash.digest());
	}

	public static Participant getByMcoId(String mcoId) {
		return participantsByMcoId.get(mcoId);
	}

	public static Participant getByDiscordId(String discordId) {
		return participantsByDiscordId.get(discordId);
	}

	private User user;
	private User registrar;
	private User alibi = null;
	private String idNumber = null;
	private String firstName = "", lastName = "";
	private long lastUpdated = -1L;
	private int age = -1;
	private final List<Location> locations = new ArrayList<>();
	private final List<Position> positions = new ArrayList<>();
	private Location currentLocation;
	private Position currentPosition;
	private String avatarUrl = null;
	private List<String> participatedEvents = new ArrayList<>();

	public Participant(User user, User registrar, Location loc, Position pos) {
		super(Participant.class, DATA_MANAGER);
		setUser(user);
		setRegistrar(registrar);
		setCurrentPosition(pos);
		setCurrentLocation(loc);
	}

	private static boolean isNull(String str) {
		return str == null || str.equals("null") || str.length() == 0;
	}

	public Participant setUser(User user) {
		if (this.user != null) participantsByDiscordId.remove(this.user.getIdAsString());
		this.user = user;

		if (user != null) participantsByDiscordId.put(user.getIdAsString(), this);
		return this;
	}

	public Participant setRegistrar(User registrar) {
		this.registrar = registrar;
		return this;
	}

	public Participant setAvatarUrl(String url) {
		this.avatarUrl = url;
		return this;
	}

	public Participant setIdNumber(String newId) {
		if (!isNull(idNumber)) participantsByMcoId.remove(idNumber);
		idNumber = isNull(newId) ? null : hash(newId);
		
		if (!isNull(idNumber)) participantsByMcoId.put(idNumber, this);
		return this;
	}

	public void updateNickname() {
		Bot.getHubServer().updateNickname(user, getFullName());
	}

	public Participant setFirstName(String firstName) {
		this.firstName = firstName;
		updateNickname();
		return this;
	}

	public Participant setLastName(String lastName) {
		this.lastName = lastName;
		updateNickname();
		return this;
	}

	public Participant setAge(int age) {
		this.age = age;
		this.lastUpdated = new Date().getTime();
		return this;
	}

	public Participant setAge(String age) {
		if (!isNull(age))
			setAge(Integer.parseInt(age));
		return this;
	}

	public Participant addLocation(Location loc) {
		if (!locations.contains(loc))
			locations.add(loc);
			return this;
	}

	public Participant addPosition(Position pos) {
		if (!positions.contains(pos))
			positions.add(pos);
			return this;
	}

	public Participant setCurrentLocation(Location loc) {
		addLocation(loc);
		currentLocation = loc;
		return this;
	}

	public Participant setCurrentPosition(Position pos) {
		addPosition(pos);
		currentPosition = pos;
		return this;
	}

	public Participant setAlibi(User alibi) {
		this.alibi = alibi;
		return this;
	}

	public Participant addParticipation(String event) {
		participatedEvents.add(event);
		return this;
	}

	public User getUser() {
		return user;
	}

	public User getRegistrar() {
		return registrar;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		if (isNull(firstName)) return user.getName();
		String nickname = firstName;
		if (!isNull(lastName)) nickname = nickname + " " + lastName;
		return nickname;
	}

	private static final long millisecondsPerYear = 1000  // seconds
									* 60    // minutes
									* 60    // hours
									* 24    // days
									* 365L;  // years

	public int getAge() {
		if (lastUpdated >= 0) {
			long time = new Date().getTime() - lastUpdated;
			long years = time / millisecondsPerYear;
			if (years < 1) return age;

			this.age += (int)years;
			this.lastUpdated += years * millisecondsPerYear;
		}
		return age;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	public String getLocationsString() {
		return locations.stream()
				.map(loc -> loc == currentLocation ? "**" + loc.label + "**" : loc.label)
				.collect(Collectors.joining(", "));
	}

	public String getPositionsString() {
		return positions.stream()
				.map(pos -> pos == currentPosition ? "**" + pos.label + "**" : pos.label)
				.collect(Collectors.joining(", "));
	}

	public User getAlibi() {
		return alibi;
	}

	public List<String> getParticipatedEvents() {
		return participatedEvents;
	}

	@Override
	public HashMap<String, String> getData() {
		HashMap<String, String> data = new HashMap<>();

		if (!isNull(idNumber)) {
			String[] asciiArr = new String[idNumber.length()];
			for (int i = 0; i < idNumber.length(); i++)
				asciiArr[i] = "" + ((int) idNumber.toCharArray()[i]);
			data.put("id", String.join(",", asciiArr));
		} else data.put("id", "");

		data.put("discordId", user.getIdAsString());
		if (registrar != null) data.put("registrarId", registrar.getIdAsString());
		data.put("firstName", firstName);
		data.put("lastName", lastName);
		data.put("age", String.valueOf(age));
		data.put("ageLastUpdated", "" + lastUpdated);
		data.put("currentLocation", currentLocation.name());
		data.put("currentPosition", currentPosition.name());
		data.put("allLocations", locations.stream()
				.map(Enum::name)
				.collect(Collectors.joining(","))
		);
		data.put("allPositions", positions.stream()
				.map(Enum::name)
				.collect(Collectors.joining(","))
		);
		if (alibi != null) data.put("alibi", alibi.getIdAsString());
		if (avatarUrl != null) data.put("avatarUrl", avatarUrl);
		if (participatedEvents.size() > 0) data.put("participatedEvents", String.join(",", participatedEvents));
		return data;
	}

}
