package gh.datamodel;

import java.sql.Timestamp;

public class Review {
	public static final String ATTRIBUTE_NAME = "review";
	private String authorUsername;
	private Integer game_id;
	private Integer givenRating;
	private String reviewText;
	private Timestamp reviewDate;

	/**
	 * Constructs new Review object representing a short game review left by the
	 * user. None of the parameters should be null pointers.
	 * 
	 * @param authorUsername
	 *            Author of the review, represented by username - a unique user
	 *            (account) identifier (ID).
	 * @param game_id
	 *            Game's ID - a unique identifier for each game.
	 * @param givenRating
	 *            User's given percentage rating for the reviewed game.
	 * @param reviewText
	 *            The actual review content.
	 * @param reviewDate
	 *            Review submission date.
	 */
	public Review(String authorUsername, Integer game_id, Integer givenRating,
			String reviewText, Timestamp reviewDate) {
		this.authorUsername = authorUsername;
		this.game_id = game_id;
		this.givenRating = givenRating;
		this.reviewText = reviewText;
		this.reviewDate = reviewDate;
	}

	/**
	 * Returns the author's username of the review.
	 * 
	 * @return Author's username - a unique user (account) identifier (ID).
	 */
	public String getAuthorUsername() {
		return authorUsername;
	}

	/**
	 * Returns game's ID - a unique identifier for each game.
	 * 
	 * @return Game's ID - a unique identifier for each game.
	 */
	public Integer getGameID() {
		return game_id;
	}

	/**
	 * Returns user's given rating for the reviewed game.
	 * 
	 * @return Integer representing user's given percentage rating for the
	 *         reviewed game.
	 */
	public Integer getGivenRating() {
		return givenRating;
	}

	/**
	 * Returns actual review content.
	 * 
	 * @return String containing review content (text).
	 */
	public String getReviewText() {
		return reviewText;
	}

	/**
	 * Returns review submission date.
	 * 
	 * @return Date object representing review submission date (Timestamp
	 *         object) in 'yyyy-mm-dd hh:mm:ss' format.
	 */
	public Timestamp getReviewDate() {
		return reviewDate;
	}

	/**
	 * Returns Review object's String representation in format "Author:
	 * <code>authorUsername</code>; Given Rating: <code>givenRating</code>;
	 * Date: <code>reivewDate</code>; Text: <code>reviewText</code>;"
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Author: " + authorUsername + "; ");
		result.append("Given Rating: " + givenRating + "%; ");
		result.append("Text: " + reviewText + "; ");
		result.append("Date: " + reviewDate + ";");
		return result.toString();
	}

}
