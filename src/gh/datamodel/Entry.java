package gh.datamodel;

import java.sql.Timestamp;

public class Entry {
	public static final String ATTRIBUTE_NAME = "entry";
	private Integer id;
	private String username;
	private Integer game_id;
	private String platform;
	private GameplayTimes times;
	private Timestamp submissionDate;

	/**
	 * Constructs new Entry object representing details about game's playthrough
	 * for user.
	 * 
	 * @param id
	 *            Integer representing Entry object' ID - a unique identifier
	 *            for each Entry.
	 * @param username
	 *            A unique user (account) identifier (ID).
	 * @param game_id
	 *            Integer representing game's ID - a unique identifier for each
	 *            game.
	 * @param platform
	 *            String representing the platform the game was played on.
	 * @param times
	 *            GameplayTimes object holding the gameplay duration values.
	 * @param submissionDate
	 *            Date object representing entry submission date.
	 */
	public Entry(Integer id, String username, Integer game_id, String platform,
			GameplayTimes times, Timestamp submissionDate) {
		this.id = id;
		this.username = username;
		this.game_id = game_id;
		this.platform = platform;
		this.times = times;
		this.submissionDate = submissionDate;
	}

	/**
	 * Constructs new Entry object representing details about game's playthrough
	 * for user.
	 * 
	 * @param username
	 *            A unique user (account) identifier (ID).
	 * @param game_id
	 *            Integer representing game's ID - a unique identifier for each
	 *            game.
	 * @param platform
	 *            String representing the platform the game was played on.
	 * @param times
	 *            GameplayTimes object holding the gameplay duration values.
	 * @param submissionDate
	 *            Date object representing entry submission date.
	 */
	public Entry(String username, Integer game_id, String platform,
			GameplayTimes times, Timestamp submissionDate) {
		this(null, username, game_id, platform, times, submissionDate);
	}

	/**
	 * Returns Integer representing Entry object' ID - a unique identifier for
	 * each Entry.
	 * 
	 * @return Integer representing Entry object' ID - a unique identifier for
	 *         each Entry.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Returns account's username.
	 * 
	 * @return Username - a unique user (account) identifier (ID).
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns Game object's ID - a unique identifier for each game.
	 * 
	 * @return Integer representing Game object's ID - a unique identifier for
	 *         each game.
	 */
	public Integer getGameID() {
		return game_id;
	}

	/**
	 * Returns String representing the platform the game was played on.
	 * 
	 * @return String representing the platform the game was played on.
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * Returns GameplayTimes object holding the gameplay duration values.
	 * 
	 * @return GameplayTimes object holding the gameplay duration values.
	 */
	public GameplayTimes getGameplayTimes() {
		return times;
	}

	/**
	 * Returns Date object representing entry submission date.
	 * 
	 * @return Date object representing entry submission date.
	 */
	public Timestamp getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * Returns Entry object's String representation in format "Entry ID:
	 * <code>id</code>; Username: <code>username</code>; Game ID:
	 * <code>game_id</code>; Platform:
	 * <code>platform<code>; Gameplay: <code>times</code>; Submission Date
	 * <code>submissionDate</code>;"
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Entry ID: " + id + "; ");
		result.append("Username: " + username + "; ");
		result.append("Game ID: " + game_id + "; ");
		result.append("Platform: " + platform + "; ");
		result.append("Gameplay: " + times + "; ");
		result.append("Submision Date: " + submissionDate + ";");
		return result.toString();
	}

}
