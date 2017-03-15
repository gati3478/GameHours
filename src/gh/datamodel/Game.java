package gh.datamodel;

import java.sql.Date;
import java.util.List;

public class Game {
	public static final String ATTRIBUTE_NAME = "game";
	private Integer id;
	private String name; // mandatory
	private String imageName; // optional
	private String developers; // optional
	private String publishers; // optional
	private List<String> genres; // optional
	private List<String> platforms; // optional
	private Date releaseDate; // optional
	private String shortDescription; // optional

	/*
	 * Private constructor. Returns new Game object.
	 */
	private Game(Integer id, String name, String imageName, String developers,
			String publishers, List<String> genres, List<String> platforms,
			Date releaseDate, String shortDescription) {
		this.id = id;
		this.name = name;
		this.imageName = imageName;
		this.developers = developers;
		this.publishers = publishers;
		this.genres = genres;
		this.platforms = platforms;
		this.releaseDate = releaseDate;
		this.shortDescription = shortDescription;
	}

	/*
	 * Private constructor required for GameBuilder. Takes GameBuilder object as
	 * a parameter. Returns new Game object.
	 */
	private Game(GameBuilder builder) {
		this(builder.id, builder.name, builder.imageName, builder.developers,
				builder.publishers, builder.genres, builder.platforms,
				builder.releaseDate, builder.shortDescription);
	}

	/**
	 * Returns Game object's ID - a unique identifier for each game.
	 * 
	 * @return Integer representing Game object's ID - a unique identifier for
	 *         each game.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Returns the name of the game.
	 * 
	 * @return String representing the name of the game.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the filename of the game's cover image.
	 * 
	 * @return String representing the filename of the game's cover image.
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Returns the information about the game's developers.
	 * 
	 * @return String representing information about the game's developers.
	 */
	public String getDevelopers() {
		return developers;
	}

	/**
	 * Returns the information about the game's publishers.
	 * 
	 * @return String representing information about the game's publishers.
	 */
	public String getPublishers() {
		return publishers;
	}

	/**
	 * Returns the list containing all genres associated with the game.
	 * 
	 * @return The list containing all genres associated with the game.
	 */
	public List<String> getGenres() {
		return genres;
	}

	/**
	 * Returns the list containing all platforms the game was released on.
	 * 
	 * @return The list containing all platforms the game was released on.
	 */
	public List<String> getPlatforms() {
		return platforms;
	}

	/**
	 * Returns game's release date.
	 * 
	 * @return Date object representing game's releae date.
	 */
	public Date getReleaseDate() {
		return releaseDate;
	}

	/**
	 * Returns a short description of the game.
	 * 
	 * @return String representing a short description of the game.
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Using Builder pattern as the Game class hold many optional parameters.
	 */
	public static class GameBuilder {
		private Integer id;
		private String name;
		private String imageName;
		private String developers;
		private String publishers;
		private List<String> genres;
		private List<String> platforms;
		private Date releaseDate;
		private String shortDescription;

		/**
		 * Constructs initial GameBuilder object.
		 * 
		 * @param id
		 *            Integer representing game's ID - a unique identifier for
		 *            each game.
		 * @param name
		 *            String representing the name of the game.
		 */
		public GameBuilder(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		/**
		 * Constructs initial GameBuilder object.
		 * 
		 * @param name
		 *            String representing the name of the game.
		 */
		public GameBuilder(String name) {
			this(null, name);
		}

		/**
		 * Adds the filename of the game's cover image.
		 * 
		 * @param imageName
		 *            String representing the filename of the game's cover
		 *            image.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder imageName(String imageName) {
			this.imageName = imageName;
			return this;
		}

		/**
		 * Adds information about the game's developers.
		 * 
		 * @param developers
		 *            String representing information about the game's
		 *            developers.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder developers(String developers) {
			this.developers = developers;
			return this;
		}

		/**
		 * Adds information about the game's publishers.
		 * 
		 * @param publishers
		 *            String representing information about the game's
		 *            publishers.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder publishers(String publishers) {
			this.publishers = publishers;
			return this;
		}

		/**
		 * Adds the list containing all genres associated with the game.
		 * 
		 * @param genres
		 *            The list containing all genres associated with the game.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder genres(List<String> genres) {
			this.genres = genres;
			return this;
		}

		/**
		 * Adds the list containing all platforms the game was released on.
		 * 
		 * @param platforms
		 *            The list containing all platforms the game was released
		 *            on.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder platforms(List<String> platforms) {
			this.platforms = platforms;
			return this;
		}

		/**
		 * Adds game's release date.
		 * 
		 * @param releaseDate
		 *            Date object representing game's release date.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder releaseDate(Date releaseDate) {
			this.releaseDate = releaseDate;
			return this;
		}

		/**
		 * Adds a String representing a short description of the game.
		 * 
		 * @param shortDescription
		 *            String representing a short description of the game.
		 * @return <code>this</code> (GameBuilder) object.
		 */
		public GameBuilder shortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
			return this;
		}

		/**
		 * Returns new Game object.
		 * 
		 * @return Game object based on accumulated information.
		 */
		public Game build() {
			return new Game(this);
		}

	}

}
