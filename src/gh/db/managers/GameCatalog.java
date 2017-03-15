package gh.db.managers;

import gh.datamodel.Account;
import gh.datamodel.Entry;
import gh.datamodel.Game;
import gh.datamodel.GameplayTimes;
import gh.datamodel.Review;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class GameCatalog extends Manager {
	public static final String ATTRIBUTE_NAME = "gamecatalog";

	/**
	 * Constructs GameCatalog object with provided DataSource object.
	 * 
	 * @param dataSource
	 *            DataSource object representing connection pool.
	 */
	public GameCatalog(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Adds game object in database, also fills Genres and Platforms data.
	 * 
	 * @param game
	 *            Game object to be added.
	 * @return Newly added game's unique ID if operation was successful, -1
	 *         otherwise.
	 */
	public int addGame(Game game) {
		int lastGameId = -1;
		// gameName is mandatory field
		if (game.getName() == null)
			return lastGameId;

		try {
			Connection connection = dataSource.getConnection();
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO Games(");
			sb.append("game_name,");
			sb.append("image_filename,");
			sb.append("developer_s,");
			sb.append("publisher_s,");
			sb.append("release_date,");
			sb.append("short_description)");
			sb.append("VALUES(?, ?, ?, ?, ?, ?);");
			PreparedStatement st = connection.prepareStatement(sb.toString(),
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, game.getName());
			st.setString(2, game.getImageName());
			st.setString(3, game.getDevelopers());
			st.setString(4, game.getPublishers());
			if (game.getReleaseDate() == null) {
				st.setNull(5, Types.NULL);
			} else {
				st.setDate(5, game.getReleaseDate());
			}
			st.setString(6, game.getShortDescription());
			st.executeUpdate();
			ResultSet result = st.getGeneratedKeys();
			if (result.next())
				lastGameId = result.getInt(1);

			insertMultipleInfo(game.getGenres(), "Genres", lastGameId,
					connection);
			insertMultipleInfo(game.getPlatforms(), "Platforms", lastGameId,
					connection);

			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastGameId;
	}

	/*
	 * Helper function for game creation - inserts information from the list
	 * into the database.
	 */
	private void insertMultipleInfo(List<String> info, String tableName,
			int lastGameId, Connection con) throws SQLException {
		if (info != null) {
			PreparedStatement stmt = null;
			for (int i = 0; i < info.size(); i++) {
				String query = "INSERT INTO " + tableName + " VALUES(?, ?);";
				stmt = con.prepareStatement(query);
				stmt.setInt(1, lastGameId);
				stmt.setString(2, info.get(i));
				stmt.executeUpdate();
			}
		}
	}

	/**
	 * Returns game object from the database by given ID.
	 * 
	 * @param id
	 *            Unique game identifier.
	 * @return Game object from the database by given ID.
	 */
	public Game getGame(Integer id) {
		Game game = null;
		String gameName = null;
		String imageFile = null;
		String developers = null;
		String publishers = null;
		Date releaseDate = null;
		String shortDescription = null;
		try {
			// getting main values
			Connection connection = dataSource.getConnection();
			String query = "SELECT * FROM Games WHERE id = " + id;
			PreparedStatement st = connection.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next()) {
				gameName = result.getString(2);
				imageFile = result.getString(3);
				developers = result.getString(4);
				publishers = result.getString(5);
				releaseDate = result.getDate(6);
				shortDescription = result.getString(7);
			}

			// getting genres
			String genresQuery = "SELECT genre FROM Genres WHERE game_id = "
					+ id;
			PreparedStatement genresSt = connection
					.prepareStatement(genresQuery);
			ResultSet genresRs = genresSt.executeQuery();
			List<String> genres = new ArrayList<String>();
			while (genresRs.next()) {
				genres.add(genresRs.getString(1));
			}

			// gettting platforms
			String platformsQuery = "SELECT platform FROM Platforms WHERE game_id = "
					+ id;
			PreparedStatement platformsSt = connection
					.prepareStatement(platformsQuery);
			ResultSet platformsRs = platformsSt.executeQuery();
			List<String> platforms = new ArrayList<String>();
			while (platformsRs.next()) {
				platforms.add(platformsRs.getString(1));
			}

			if (gameName != null) {
				game = new Game.GameBuilder(id, gameName).genres(genres)
						.platforms(platforms).releaseDate(releaseDate)
						.imageName(imageFile).developers(developers)
						.publishers(publishers)
						.shortDescription(shortDescription).build();
			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return game;
	}

	/**
	 * Returns the List of Game objects within the given range.
	 * 
	 * @param page
	 *            The page user is browsing.
	 * @param limit
	 *            The limit of results per page.
	 * @return The list of Game objects within the given range.
	 */
	public List<Game> getGames(int page, int limit) {
		List<Game> games = new ArrayList<Game>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT id FROM Games ");
			builder.append("ORDER BY game_name ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, (page - 1) * limit);
			statement.setInt(2, limit);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Game game = getGame(rs.getInt(1));
				games.add(game);
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return games;
	}

	/**
	 * Checks if the given game object exists in the database.
	 * 
	 * @param game
	 *            Game object.
	 * @return true if game exists in the database, false otherwise.
	 */
	public boolean containsGame(Game game) {
		if (game == null)
			return false;
		try {
			Connection con = dataSource.getConnection();
			String query = generateSimpleSelectQuery("games",
					new ArrayList<String>(), "game_name", game.getName());
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			boolean contains = result.next();
			con.close();
			return contains;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns the list of filtered Game objects with criteria passed as
	 * arguments. Method can search using every combinations of parameters, but
	 * at least one parameter is required for searching. Filters - Only name,
	 * Only genre, Only startTime, Only endTime, name and genre, name and
	 * startTime, name and endTime, genre and startTime, genre and endTime,
	 * startTIme and endTime, all except name, all except genre, all except
	 * startTime and all except endTime.
	 * 
	 * @param name
	 *            Game's name as a search criteria.
	 * @param genre
	 *            Game's genre as a search criteria.
	 * @param startTime
	 *            Minimum gameplay length as a search criteria.
	 * @param endTime
	 *            Maximum gameplay length as a search criteria.
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of results per page.
	 * @return The list of games according to specified parameters.
	 */
	public List<Game> searchGame(String name, String genre, Integer startTime,
			Integer endTime, int page, int limit) {
		List<Game> games = new ArrayList<Game>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder sb = new StringBuilder();
			PreparedStatement st = null;
			// only name
			if (genre == null && startTime == null
					&& endTime == null && name != null) {
				sb.append("SELECT id FROM Games WHERE ");
				sb.append("game_name LIKE " + "\"%" + name + "%\"");
				sb.append(" GROUP BY games.id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, (page - 1) * limit);
				st.setInt(2, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only genre
			if (name == null && startTime == null
					&& endTime == null && genre != null) {
				sb.append("SELECT game_id FROM genres WHERE ");
				sb.append("genre = ?");
				sb.append(" GROUP BY game_id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, (page - 1) * limit);
				st.setInt(3, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only startTime
			if (name == null && genre == null
					&& endTime == null && startTime != null) {
				sb.append("SELECT game_id FROM  gameplaystats WHERE ");
				sb.append("average_main >= ? OR average_extra >= ? OR average_complete >= ?");
				sb.append(" GROUP BY game_id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, startTime);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, (page - 1) * limit);
				st.setInt(5, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only endTime
			if (name == null && genre == null
					&& startTime == null && endTime != null) {
				sb.append("SELECT game_id FROM  gameplaystats WHERE ");
				sb.append("average_main <= ? OR average_extra <= ? OR average_complete <= ?");
				sb.append(" GROUP BY game_id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, endTime);
				st.setInt(2, endTime);
				st.setInt(3, endTime);
				st.setInt(4, (page - 1) * limit);
				st.setInt(5, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only startTime and endTime
			if (name == null && genre == null
					&& startTime != null && endTime != null) {
				sb.append("SELECT game_id FROM  gameplaystats WHERE ");
				sb.append("(average_main >= ? AND average_main <= ?) OR (average_extra >= ? AND average_extra <= ?) OR (average_complete >= ? AND average_complete <= ?)");
				sb.append(" GROUP BY game_id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, startTime);
				st.setInt(2, endTime);
				st.setInt(3, startTime);
				st.setInt(4, endTime);
				st.setInt(5, startTime);
				st.setInt(6, endTime);
				st.setInt(7, (page - 1) * limit);
				st.setInt(8, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only genre and startTime
			if (name == null && endTime == null
					&& genre != null && startTime != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE (average_main >= ? OR average_extra >= ? OR average_complete >= ?) AND genre = ?");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, startTime);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setString(4, genre);
				st.setInt(5, (page - 1) * limit);
				st.setInt(6, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only genre and endTime
			if (name == null && startTime == null
					&& genre != null && endTime != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE (average_main <= ? OR average_extra <= ? OR average_complete <= ?) AND genre = ?");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, endTime);
				st.setInt(2, endTime);
				st.setInt(3, endTime);
				st.setString(4, genre);
				st.setInt(5, (page - 1) * limit);
				st.setInt(6, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only name and startTime
			if (genre == null && endTime == null
					&& name != null && startTime != null) {
				sb.append("SELECT id FROM  games JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE (average_main >= ? OR average_extra >= ? OR average_complete >= ?) AND game_name LIKE " + "\"%"
						+ name + "%\"");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, startTime);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, (page - 1) * limit);
				st.setInt(5, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only name and endTime
			if (genre == null && startTime == null
					&& name != null && endTime != null) {
				sb.append("SELECT id FROM  games JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE (average_main <= ? OR average_extra <= ? OR average_complete <= ?) AND game_name LIKE " + "\"%"
						+ name + "%\"");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, endTime);
				st.setInt(2, endTime);
				st.setInt(3, endTime);
				st.setInt(4, (page - 1) * limit);
				st.setInt(5, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// only name and genre
			if (startTime == null && endTime == null
					&& name != null
					&& genre != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("WHERE genre = ? AND game_name LIKE " + "\"%" + name
						+ "%\"");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, (page - 1) * limit);
				st.setInt(3, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// no endTime
			if (endTime == null && name != null
					&& genre != null && startTime != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE game_name LIKE " + "\"%" + name
						+ "%\" AND genre = ? AND (average_main >= ? OR average_extra >= ? OR average_complete >= ?)");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, startTime);
				st.setInt(5, (page - 1) * limit);
				st.setInt(6, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// no startTime
			if (startTime == null && name != null
					&& genre != null && endTime != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE game_name LIKE " + "\"%" + name
						+ "%\" AND genre = ? AND (average_main <= ? OR average_extra <= ? OR average_complete <= ?)");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, endTime);
				st.setInt(3, endTime);
				st.setInt(4, endTime);
				st.setInt(5, (page - 1) * limit);
				st.setInt(6, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// no genre
			if (genre == null && startTime != null
					&& endTime != null && name != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE game_name LIKE " + "\"%" + name
						+ "%\" AND (average_main >= ? AND average_main <= ?) OR (average_extra >= ? AND average_extra <= ?) OR (average_complete >= ? AND average_complete <= ?)");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setInt(1, startTime);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, endTime);
				st.setInt(5, endTime);
				st.setInt(6, endTime);
				st.setInt(7, (page - 1) * limit);
				st.setInt(8, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// no name
			if (name == null && startTime != null
					&& endTime != null && genre != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE genre = ? AND (average_main >= ? AND average_main <= ?) OR (average_extra >= ? AND average_extra <= ?) OR (average_complete >= ? AND average_complete <= ?)");
				sb.append(" GROUP BY id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, startTime);
				st.setInt(5, endTime);
				st.setInt(6, endTime);
				st.setInt(7, endTime);
				st.setInt(8, (page - 1) * limit);
				st.setInt(9, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			// all values are entered
			if (name != null && genre != null && startTime != null
					&& endTime != null) {
				sb.append("SELECT id FROM  games JOIN genres ON genres.game_id = id ");
				sb.append("JOIN gameplaystats ON gameplaystats.game_id = id ");
				sb.append("WHERE game_name LIKE " + "\"%" + name
						+ "%\" AND genre = ? AND (average_main >= ? AND average_main <= ?) OR (average_extra >= ? AND average_extra <= ?) OR (average_complete >= ? AND average_complete <= ?)");
				sb.append(" GROUP BY games.id ");
				sb.append("LIMIT ?,?");
				st = con.prepareStatement(sb.toString());
				st.setString(1, genre);
				st.setInt(2, startTime);
				st.setInt(3, startTime);
				st.setInt(4, startTime);
				st.setInt(5, endTime);
				st.setInt(6, endTime);
				st.setInt(7, endTime);
				st.setInt(8, (page - 1) * limit);
				st.setInt(9, limit);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Game game = getGame(rs.getInt(1));
					games.add(game);
				}
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return games;
	}

	/**
	 * Updates game's name in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newName
	 *            Game's new name.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGameName(Game game, String newName) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "game_name", newName, "id",
				"" + game.getID());
		return true;
	}

	/**
	 * Updates game's image (cover) filename in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newImageFile
	 *            Game's image (cover) filename.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGameImage(Game game, String newImageFile) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "image_filename", newImageFile, "id", ""
				+ game.getID());
		return true;
	}

	/**
	 * Updates game's developers in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newDevelopers
	 *            Game's new developers.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGameDevelopers(Game game, String newDevelopers) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "developer_s", newDevelopers, "id", ""
				+ game.getID());
		return true;
	}

	/**
	 * Updates game's publishers in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newPublishers
	 *            Game's new publishers.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGamePublishers(Game game, String newPublishers) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "publisher_s", newPublishers, "id", ""
				+ game.getID());
		return true;
	}

	/**
	 * Updates game's release date in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newDate
	 *            Game's corrected release date.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGameReleaseDate(Game game, Date newDate) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "release_date", newDate.toString(), "id",
				"" + game.getID());
		return true;
	}

	/**
	 * Updates game's short description in the database.
	 * 
	 * @param game
	 *            Game object to be updated.
	 * @param newShortDescription
	 *            Game's new short description.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean changeGameShortDescription(Game game,
			String newShortDescription) {
		if (game == null)
			return false;
		executeSimpleUpdate("Games", "short_description", newShortDescription,
				"id", "" + game.getID());
		return true;
	}

	/**
	 * Adds game's genre into the database.
	 * 
	 * @param game
	 *            Game object.
	 * @param genre
	 *            Genre to be added for game.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean addGameGenre(Game game, String genre) {
		if (game == null || genre == null)
			return false;
		// preparing insert fields
		List<String> values = new ArrayList<String>();
		values.add("" + game.getID());
		values.add(genre);
		// executing
		executeInsert("Genres", values);
		return true;
	}

	/**
	 * Removes game's genre from the database.
	 * 
	 * @param game
	 *            Game object.
	 * @param genre
	 *            Genre to be removed.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         given genre is not associated with the given game or illegal
	 *         arguments were passed.
	 */
	public boolean removeGameGenre(Game game, String genre) {
		if (game == null || genre == null)
			return false;
		if (!game.getGenres().contains(genre))
			return false;
		// preparing delete fields
		String tableName = "Genres";
		List<String> whereCols = new ArrayList<String>();
		whereCols.add("game_id");
		whereCols.add("genre");
		List<String> whereVals = new ArrayList<String>();
		whereVals.add("" + game.getID());
		whereVals.add(genre);
		List<MatchType> matchTypes = new ArrayList<MatchType>();
		matchTypes.add(MatchType.EXACT);
		matchTypes.add(MatchType.EXACT);
		// executing
		executeDelete(tableName, whereCols, whereVals, matchTypes);
		return true;
	}

	/**
	 * Adds game's platform into the database.
	 * 
	 * @param game
	 *            Game object.
	 * @param platform
	 *            Platform to be added for game.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean addGamePlatform(Game game, String platform) {
		if (game == null || platform == null)
			return false;
		// preparing insert fields
		List<String> vals = new ArrayList<String>();
		vals.add("" + game.getID());
		vals.add(platform);
		// executing
		executeInsert("Platforms", vals);
		return true;
	}

	/**
	 * Removes game's platform from the database.
	 * 
	 * @param game
	 *            Game object.
	 * @param platform
	 *            Platform to be removed.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         given platform is not associated with the given game or illegal
	 *         arguments were passed.
	 */
	public boolean removeGamePlatform(Game game, String platform) {
		if (game == null || platform == null)
			return false;
		if (!game.getPlatforms().contains(platform))
			return false;
		// preparing delete fields
		String tableName = "Platforms";
		List<String> whereCols = new ArrayList<String>();
		whereCols.add("game_id");
		whereCols.add("platform");
		List<String> whereVals = new ArrayList<String>();
		whereVals.add("" + game.getID());
		whereVals.add(platform);
		List<MatchType> matchTypes = new ArrayList<MatchType>();
		matchTypes.add(MatchType.EXACT);
		matchTypes.add(MatchType.EXACT);
		// executing
		executeDelete(tableName, whereCols, whereVals, matchTypes);
		return true;
	}

	/**
	 * Returns the total number of games in database.
	 * 
	 * @return The total number of games in database.
	 */
	public int getCatalogSize() {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT COUNT(*) FROM games";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Removes game from the database.
	 * 
	 * @param game
	 *            Game object to be removed.
	 */
	public void removeGame(Game game) {
		executeSimpleDelete("Games", "id", "" + game.getID());
	}

	/**
	 * Adds Entry object into the database.
	 * 
	 * @param entry
	 *            Entry object to be added.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         illegal parameters were passed.
	 */
	public boolean addEntry(Entry entry) {
		// platform is mandatory field
		if (entry.getPlatform() == null)
			return false;
		// gamePlayTimes are mandatory fields as well, at least one of them
		// should not be null
		if (entry.getGameplayTimes().getCompleteGameplayTime() == null
				&& entry.getGameplayTimes().getExtraGameplayTime() == null
				&& entry.getGameplayTimes().getMainGameplayTime() == null)
			return false;
		try {
			Connection connection = dataSource.getConnection();
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO Entries(");
			sb.append("username, ");
			sb.append("game_id, ");
			sb.append("main_gameplay_time, ");
			sb.append("extra_gameplay_time, ");
			sb.append("complete_gameplay_time, ");
			sb.append("platform, ");
			sb.append("submission_date)");
			sb.append(" VALUES(?, ?, ?, ?, ?, ?, ?);");
			PreparedStatement st = connection.prepareStatement(sb.toString());
			st.setString(1, entry.getUsername());
			st.setInt(2, entry.getGameID());
			GameplayTimes times = entry.getGameplayTimes();
			if (times.getMainGameplayTime() != null)
				st.setInt(3, times.getMainGameplayTime());
			else
				st.setNull(3, Types.NULL);
			if (times.getExtraGameplayTime() != null)
				st.setInt(4, times.getExtraGameplayTime());
			else
				st.setNull(4, Types.NULL);
			if (times.getCompleteGameplayTime() != null)
				st.setInt(5, times.getCompleteGameplayTime());
			else
				st.setNull(5, Types.NULL);
			st.setString(6, entry.getPlatform());
			st.setTimestamp(7, entry.getSubmissionDate());
			st.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns Entry object associated with the given id.
	 * 
	 * @param entry_id
	 *            Entry's unique id.
	 * @return Entry object associated with the given id.
	 * @throws SQLException
	 */
	public Entry getEntry(int entry_id) throws SQLException {
		Entry result = null;
		Connection con = dataSource.getConnection();
		String query = "SELECT * FROM Entries WHERE id = ?;";
		PreparedStatement statement = con.prepareStatement(query);
		statement.setInt(1, entry_id);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			Integer id = rs.getInt(1);
			String username = rs.getString(2);
			Integer game_id = rs.getInt(3);
			Integer mainGameplay = rs.getInt(4);
			Integer extraGameplay = rs.getInt(5);
			Integer completeGameplay = rs.getInt(6);
			String platform = rs.getString(7);
			Timestamp submissionDate = rs.getTimestamp(8);
			GameplayTimes times = new GameplayTimes(mainGameplay,
					extraGameplay, completeGameplay);
			result = new Entry(id, username, game_id, platform, times,
					submissionDate);
		}
		con.close();
		return result;
	}

	/**
	 * Returns Entry object associated with given credentials.
	 * 
	 * @param acc
	 *            Account model.
	 * @param game
	 *            Game object.
	 * @return Entry object associated with given credentials.
	 * @throws SQLException
	 */
	public Entry getEntry(Account acc, Game game) throws SQLException {
		Entry result = null;
		Connection con = dataSource.getConnection();
		StringBuilder builder = new StringBuilder("SELECT * FROM Entries ");
		builder.append("WHERE game_id = ? ");
		builder.append("AND username = ?;");
		PreparedStatement statement = con.prepareStatement(builder.toString());
		statement.setInt(1, game.getID());
		statement.setString(2, acc.getUsername());
		ResultSet rs = statement.executeQuery();
		// building Review objects
		if (rs.next()) {
			Integer id = rs.getInt(1);
			String username = rs.getString(2);
			Integer game_id = rs.getInt(3);
			Integer mainGameplay = rs.getInt(4);
			Integer extraGameplay = rs.getInt(5);
			Integer completeGameplay = rs.getInt(6);
			String platform = rs.getString(7);
			Timestamp submissionDate = rs.getTimestamp(8);
			GameplayTimes times = new GameplayTimes(mainGameplay,
					extraGameplay, completeGameplay);
			result = new Entry(id, username, game_id, platform, times,
					submissionDate);
		}
		con.close();
		return result;
	}

	/**
	 * Returns the List of Entry objects - all entries in the database within
	 * the given range.
	 * 
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of number of elements on single page.
	 * 
	 * @return The list of Entry objects within the given range.
	 */
	public List<Entry> getEntries(int page, int limit) {
		List<Entry> entries = new ArrayList<Entry>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT * FROM Entries ");
			builder.append("ORDER BY ");
			builder.append("(coalesce(main_gameplay_time, 0) + ");
			builder.append("coalesce(extra_gameplay_time,0) + ");
			builder.append("coalesce(complete_gameplay_time, 0)) / ");
			builder.append("((main_gameplay_time is not null) + ");
			builder.append("(extra_gameplay_time is not null) + ");
			builder.append("(complete_gameplay_time is not null)) DESC ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, (page - 1) * limit);
			statement.setInt(2, limit);
			ResultSet rs = statement.executeQuery();
			// building Entry objects
			while (rs.next()) {
				Integer id = rs.getInt(1);
				String username = rs.getString(2);
				Integer game_id = rs.getInt(3);
				Integer mainGameplay = rs.getInt(4);
				Integer extraGameplay = rs.getInt(5);
				Integer completeGameplay = rs.getInt(6);
				String platform = rs.getString(7);
				Timestamp submissionDate = rs.getTimestamp(8);
				GameplayTimes times = new GameplayTimes(mainGameplay,
						extraGameplay, completeGameplay);
				Entry entry = new Entry(id, username, game_id, platform, times,
						submissionDate);
				entries.add(entry);
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entries;
	}

	/**
	 * Returns the total number of entries.
	 * 
	 * @return The total number of entries.
	 */
	public int getEntriesQuantity() {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT COUNT(*) FROM Entries";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Updates Entry information in the database using given entry object's
	 * information.
	 * 
	 * @param updatedEntry
	 *            Entry object containing updated information.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed as arguments.
	 */
	public boolean updateEntry(Entry updatedEntry) {
		if (updatedEntry == null)
			return false;
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("UPDATE Entries ");
			builder.append("SET main_gameplay_time = ?, ");
			builder.append("extra_gameplay_time = ?, ");
			builder.append("complete_gameplay_time = ?, ");
			builder.append("platform = ? ");
			builder.append("WHERE id = ?;");
			PreparedStatement st = con.prepareStatement(builder.toString());
			GameplayTimes times = updatedEntry.getGameplayTimes();
			if (times.getMainGameplayTime() != null)
				st.setInt(1, times.getMainGameplayTime());
			else
				st.setNull(1, Types.NULL);
			if (times.getExtraGameplayTime() != null)
				st.setInt(2, times.getExtraGameplayTime());
			else
				st.setNull(2, Types.NULL);
			if (times.getCompleteGameplayTime() != null)
				st.setInt(3, times.getCompleteGameplayTime());
			else
				st.setNull(3, Types.NULL);
			st.setString(4, updatedEntry.getPlatform());
			st.setInt(5, updatedEntry.getID());
			st.executeUpdate();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Removes given entry from the database.
	 * 
	 * @param entry
	 *            Entry object to be removed.
	 */
	public void removeEntry(Entry entry) {
		executeSimpleDelete("Entries", "id", "" + entry.getID());
	}

	/**
	 * Adds Review object into the database.
	 * 
	 * @param review
	 *            Review object.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed as arguments.
	 */
	public boolean addReview(Review review) {
		if (review.getGivenRating() == null || review.getReviewText() == null)
			return false;
		// preparing insert fields
		List<String> values = new ArrayList<String>();
		values.add(review.getAuthorUsername());
		values.add("" + review.getGameID());
		values.add("" + review.getGivenRating());
		values.add(review.getReviewText());
		values.add(review.getReviewDate().toString());
		// executing
		executeInsert("Reviews", values);
		return true;
	}

	/**
	 * Returns Review object associated with given credentials.
	 * 
	 * @param acc
	 *            Account model.
	 * @param game
	 *            Game object.
	 * @return Review object associated with given credentials.
	 * @throws SQLException
	 */
	public Review getReview(Account acc, Game game) throws SQLException {
		Review result = null;
		Connection con = dataSource.getConnection();
		StringBuilder builder = new StringBuilder("SELECT * FROM Reviews ");
		builder.append("WHERE game_id = ? ");
		builder.append("AND username = ?;");
		PreparedStatement statement = con.prepareStatement(builder.toString());
		statement.setInt(1, game.getID());
		statement.setString(2, acc.getUsername());
		ResultSet rs = statement.executeQuery();
		// building Review objects
		if (rs.next()) {
			String username = rs.getString(1);
			int game_id = rs.getInt(2);
			Integer rating = rs.getInt(3);
			String reviewText = rs.getString(4);
			Timestamp time = rs.getTimestamp(5);
			result = new Review(username, game_id, rating, reviewText, time);
		}
		con.close();
		return result;
	}

	/**
	 * Returns the list of Review objects - all reviews associated with the
	 * given game, result is limited by the page user is on and the limit of
	 * number of elements on single page.
	 *
	 * @param game
	 *            Game object.
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of number of elements on single page.
	 * 
	 * @return The list of Review objects within the given range.
	 */
	public List<Review> getReviews(Game game, int page, int limit) {
		List<Review> reviews = new ArrayList<Review>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT * FROM Reviews ");
			builder.append("WHERE game_id = ? ");
			builder.append("ORDER BY review_date DESC ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, game.getID());
			statement.setInt(2, (page - 1) * limit);
			statement.setInt(3, limit);
			ResultSet rs = statement.executeQuery();
			// building Review objects
			while (rs.next()) {
				String username = rs.getString(1);
				int game_id = rs.getInt(2);
				Integer rating = rs.getInt(3);
				String reviewText = rs.getString(4);
				Timestamp time = rs.getTimestamp(5);
				Review review = new Review(username, game_id, rating,
						reviewText, time);
				reviews.add(review);
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reviews;
	}

	/**
	 * Returns all reviews from the database.
	 * 
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of number of elements on single page.
	 * @return The list of Review objects within the given range.
	 */
	public List<Review> getReviews(int page, int limit) {
		List<Review> reviews = new ArrayList<Review>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT * FROM Reviews ");
			builder.append("ORDER BY review_date DESC ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, (page - 1) * limit);
			statement.setInt(2, limit);
			ResultSet rs = statement.executeQuery();
			// building Review objects
			while (rs.next()) {
				String username = rs.getString(1);
				int game_id = rs.getInt(2);
				Integer rating = rs.getInt(3);
				String reviewText = rs.getString(4);
				Timestamp time = rs.getTimestamp(5);
				Review review = new Review(username, game_id, rating,
						reviewText, time);
				reviews.add(review);
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reviews;
	}

	/**
	 * Returns the total number of reviews.
	 * 
	 * @return The total number of reviews.
	 */
	public int getReviewsQuantity() {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT COUNT(*) FROM Reviews";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Returns the total number of reviews per game.
	 * 
	 * @param game
	 *            Game object.
	 * @return The total number of reviews per game.
	 */
	public int getReviewsQuantity(Game game) {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT COUNT(*) FROM Reviews WHERE game_id = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, game.getID());
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Updates old review information in the database using given review
	 * object's information.
	 * 
	 * @param updatedReview
	 *            Review object with updated information.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointer was passed as an argument.
	 */
	public boolean updateReview(Review updatedReview) {
		if (updatedReview == null)
			return false;
		// preparing update fields
		String tableName = "Reviews";
		List<String> cols = new ArrayList<String>();
		cols.add("rating");
		cols.add("review_text");
		List<String> vals = new ArrayList<String>();
		vals.add("" + updatedReview.getGivenRating());
		vals.add(updatedReview.getReviewText());
		List<String> whereCols = new ArrayList<String>();
		whereCols.add("username");
		whereCols.add("game_id");
		List<String> whereVals = new ArrayList<String>();
		whereVals.add(updatedReview.getAuthorUsername());
		whereVals.add("" + updatedReview.getGameID());
		List<MatchType> matchTypes = new ArrayList<MatchType>();
		matchTypes.add(MatchType.EXACT);
		matchTypes.add(MatchType.EXACT);
		// executing
		executeUpdate(tableName, cols, vals, whereCols, whereVals, matchTypes);
		return true;
	}

	/**
	 * Removes given review object from the database.
	 * 
	 * @param review
	 *            Review object.
	 */
	public void removeReview(Review review) {
		List<String> cols = new ArrayList<String>();
		cols.add("username");
		cols.add("game_id");
		List<String> vals = new ArrayList<String>();
		vals.add(review.getAuthorUsername());
		vals.add("" + review.getGameID());
		List<MatchType> matchTypes = new ArrayList<MatchType>();
		matchTypes.add(MatchType.EXACT);
		matchTypes.add(MatchType.EXACT);
		executeDelete("Reviews", cols, vals, matchTypes);
	}

	/**
	 * Adds screenshot filename in the database for the given game.
	 * 
	 * @param game
	 *            Game object.
	 * @param imageName
	 *            Image filename.
	 * @return true if operation was successful, false otherwise, namely, if
	 *         null pointers were passed.
	 */
	public boolean addImage(Game game, String imageName) {
		if (game == null || imageName == null)
			return false;
		// preparing insert fields
		List<String> vals = new ArrayList<String>();
		vals.add("" + game.getID());
		vals.add(imageName);
		// executing
		executeInsert("GameImages", vals);
		return true;
	}

	/**
	 * Returns the list of all images associated with the given game.
	 * 
	 * @param game
	 *            Game object.
	 * @return The list of all images associated with the given game.
	 */
	public List<String> getImages(Game game) {
		List<String> images = new ArrayList<String>();
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT screenshot_file FROM GameImages WHERE game_id = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, game.getID());
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				images.add(rs.getString(1));
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return images;
	}

	/**
	 * Returns the number of images associated with the given game.
	 * 
	 * @param game
	 *            Game object.
	 * @return The number of images associated with the given game.
	 */
	public int getImagesQuantity(Game game) {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT COUNT(game_id) ");
			builder.append("FROM GameImages WHERE game_id = ?");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, game.getID());
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Removes image with given filename.
	 * 
	 * @param imageFilename
	 *            Filename of image.
	 */
	public void removeImage(String imageFilename) {
		executeSimpleDelete("GameImages", "screenshot_file", imageFilename);
	}

	/**
	 * Adds new video link for the given game.
	 * 
	 * @param game
	 *            Game object.
	 * @param videoLink
	 *            Video link to be added.
	 * @return true if operation is successful, false otherwise, namely if null
	 *         pointers were passed.
	 */
	public boolean addVideoLink(Game game, String videoLink) {
		if (game == null || videoLink == null)
			return false;
		// preparing insert fields
		List<String> vals = new ArrayList<String>();
		vals.add("" + game.getID());
		vals.add(videoLink);
		// executing
		executeInsert("VideoLinks", vals);
		return true;
	}

	/**
	 * Returns the list of video links - all video links associated with the
	 * given game.
	 * 
	 * @param game
	 *            Game object.
	 * @return The list of video links.
	 */
	public List<String> getVideoLinks(Game game) {
		List<String> videoLinks = new ArrayList<String>();
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT video_link FROM VideoLinks WHERE game_id = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, game.getID());
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				videoLinks.add(rs.getString(1));
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoLinks;
	}

	/**
	 * Returns the number of video links associated with the given game.
	 * 
	 * @param game
	 *            Game object.
	 * @return The number of video links associated with the given game.
	 */
	public int getVideoLinksQuantity(Game game) {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT COUNT(game_id) ");
			builder.append("FROM VideoLinks WHERE game_id = ?");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setInt(1, game.getID());
			ResultSet rs = statement.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Removes the given video link from the database.
	 * 
	 * @param videoLink
	 *            The link of the video.
	 */
	public void removeVideoLink(String videoLink) {
		executeSimpleDelete("VideoLinks", "video_link", videoLink);
	}

}
