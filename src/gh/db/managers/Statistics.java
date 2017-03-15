package gh.db.managers;

import gh.datamodel.Account;
import gh.datamodel.Game;
import gh.datamodel.GameplayTimes;
import gh.datamodel.Account.AccountBuilder;
//import gh.datamodels.Game.GameBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class Statistics extends Manager {
	public static final String ATTRIBUTE_NAME = "statistics";

	/**
	 * Constructs Statistics object with provided DataSource object.
	 * 
	 * @param dataSource
	 *            DataSource object representing connection pool.
	 */

	public Statistics(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * 
	 * Gets average rating for given game, by its unique id.
	 * 
	 * @param game
	 *            Object game which stores game_id.
	 * @return integer, average rating for game_id.
	 */
	public Integer getAverageRating(Game game) {
		int rating = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT IFNULL(average_rating, 0) FROM GameplayStats WHERE game_id = "
					+ game.getID();
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				rating = result.getInt(1);
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rating;
	}

	/**
	 * 
	 * Creates object that contains average main, extra and complete gameplay
	 * times for given game.
	 * 
	 * @param game
	 *            Object game which stores game_id.
	 * @return GameplayTime object with average gameplay times.
	 */
	public GameplayTimes getAverageGameplayTimes(Game game) {
		GameplayTimes avTime = null;
		try {

			Connection con = dataSource.getConnection();
			String query = "SELECT IFNULL(average_main, 0), IFNULL(average_extra, 0), IFNULL(average_complete, 0) FROM GameplayStats WHERE game_id = "
					+ game.getID();
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				avTime = new GameplayTimes(result.getInt(1), result.getInt(2),
						result.getInt(3));

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return avTime;
	}

	/**
	 * 
	 * Creates object that contains minimum main, extra and complete gameplay
	 * times for given game.
	 * 
	 * @param game
	 *            Object game which stores game_id.
	 * @return GameplayTime object with minimum gameplay times.
	 */

	public GameplayTimes getMinGameplayTimes(Game game) {
		GameplayTimes minTime = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT IFNULL(min_main, 0), IFNULL(min_extra, 0), IFNULL(min_complete, 0) FROM GameplayStats WHERE game_id = "
					+ game.getID();
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				minTime = new GameplayTimes(result.getInt(1), result.getInt(2),
						result.getInt(3));
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return minTime;
	}

	/**
	 * 
	 * Creates object that contains maximum main, extra and complete gameplay
	 * times for given game.
	 * 
	 * @param game
	 *            Object game which stores game_id.
	 * @return GameplayTime object with maximum gameplay times.
	 */
	public GameplayTimes getMaxGameplayTimes(Game game) {
		GameplayTimes maxTime = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT IFNULL(max_main, 0),  IFNULL(max_extra, 0), "
					+ "IFNULL(max_complete, 0) FROM GameplayStats WHERE game_id = "
					+ game.getID();
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				maxTime = new GameplayTimes(result.getInt(1), result.getInt(2),
						result.getInt(3));
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxTime;
	}

	/**
	 * 
	 * Method counts the number of submissions for a certain platform and game.
	 * 
	 * @param game
	 *            Object game which stores game_id.
	 * @return <String, Integer> type map platforms and amount of submission on
	 *         this platform for a certain game.
	 */
	public Map<String, Integer> getPlatformStats(Game game) {
		Map<String, Integer> platformStats = new HashMap<String, Integer>();
		int n = 0;

		try {
			Connection con = dataSource.getConnection();
			List<String> platforms = game.getPlatforms();

			for (int i = 0; i < platforms.size(); i++) {
				String query = "SELECT COUNT(platform) FROM Entries WHERE game_id = "
						+ game.getID()
						+ " AND "
						+ "platform = \""
						+ platforms.get(i) + "\"";
				PreparedStatement st = con.prepareStatement(query);
				ResultSet result = st.executeQuery();

				if (result.next())
					n = result.getInt(1);
				platformStats.put(platforms.get(i), n);
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return platformStats;
	}

	/**
	 * 
	 * Most popular games(games with most submission in last month).
	 * 
	 * 
	 * @param page
	 *            # of page.
	 * 
	 * @param limit
	 *            number of popular games user wants.
	 * 
	 * @return list<Game> of popular games.
	 */
	public List<Game> getPopularGames(int page, int limit) {
		List<Game> popularGames = new ArrayList<Game>();
		Game temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT game_id, COUNT(game_id) AS games FROM Entries WHERE submission_date >="
					+ " CURDATE() - INTERVAL DAY(CURDATE())-1 DAY GROUP BY game_id ORDER BY games DESC limit "
					+ limit * (page - 1) + ", " + limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getGame(result.getInt(1), con);
				popularGames.add(temp);
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return popularGames;
	}

	/**
	 * 
	 * Private method for creating game by connecting database.
	 * 
	 * @param int1
	 *            game_id for searching game info
	 * @param con
	 *            connection for connecting database.
	 * @return Game object.
	 */
	private Game getGame(int int1, Connection con) {
		Game game = null;
		try {
			String query = "SELECT * FROM Games WHERE id = " + int1;
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();

			query = "SElECT genre FROM Genres WHERE game_id = " + int1;
			List<String> platforms = getList(int1, con, query);
			query = "SElECT platform FROM Platforms WHERE game_id = " + int1;
			List<String> genres = getList(int1, con, query);
			if (result.next()) {
				game = new Game.GameBuilder(result.getInt(1),
						result.getString(2)).imageName(result.getString(3))
						.developers(result.getString(4))
						.publishers(result.getString(5))
						.releaseDate(result.getDate(6))
						.shortDescription(result.getString(7)).genres(genres)
						.platforms(platforms).build();
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return game;
	}

	/**
	 * 
	 * Private method for creating List by connecting database.
	 * 
	 * @param int1
	 *            game_id for searching game info
	 * @param con
	 *            connection for connecting database.
	 * @param query
	 *            line that will be executed for getting information.
	 * @return List<String> object.
	 */
	private List<String> getList(int int1, Connection con, String query)
			throws SQLException {

		List<String> list = new ArrayList<String>();
		PreparedStatement st = con.prepareStatement(query);
		ResultSet result = st.executeQuery();

		while (result.next())
			list.add(result.getString(1));

		return list;
	}

	/**
	 * 
	 * Games with most submission in last month
	 * 
	 * @param limit
	 *            number of most submitted games user wants.
	 * @return Map<Game, integer> of most submitted games and submission number.
	 */
	public Map<Game, Integer> getMostSubmittedGames(int limit) {
		Map<Game, Integer> submittedGames = new HashMap<Game, Integer>();
		Game temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT game_id, COUNT(game_id) as games FROM Entries  "
					+ "GROUP BY game_id ORDER BY games DESC LIMIT " + limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();

			while (result.next()) {
				temp = getGame(result.getInt(1), con);
				submittedGames.put(temp, result.getInt(2));
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return submittedGames;
	}

	/**
	 * 
	 * Games with most rating. rating represents average of users' submissions.
	 * 
	 * @param limit
	 *            number of top rated games user wants.
	 * @return Map<Game, integer> of top rated games and rating.
	 */
	public Map<Game, Integer> getTopRatedGames(int limit) {
		Map<Game, Integer> topRatedGames = new HashMap<Game, Integer>();
		Game temp = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT game_id, MAX(IFNULL(average_rating, 0))  FROM GameplayStats "
					+ " GROUP BY average_rating ORDER BY average_Rating DESC LIMIT "
					+ limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getGame(result.getInt(1), con);
				topRatedGames.put(temp, result.getInt(2));
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return topRatedGames;

	}

	/**
	 * 
	 * Games with shortest time to beat.
	 * 
	 * @param limit
	 *            number of shortest games user wants.
	 * @return Map<Game, integer> of shortest games and amount of time to beat
	 *         them.
	 */
	public Map<Game, Integer> getShortestGames(int limit) {
		Map<Game, Integer> shortestGames = new HashMap<Game, Integer>();
		Game temp = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "select game_id, ((coalesce(average_complete, 0) + coalesce(average_extra, 0) + coalesce(average_main, 0)) /((average_complete is not null) + (average_extra is not null) + (average_main is not null)))as average from gameplaystats where average_main is not null or average_extra is not "
					+ "null or average_complete is not null GROUP BY game_id ORDER BY average asc LIMIT "
					+ limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getGame(result.getInt(1), con);
				shortestGames.put(temp, result.getInt(2));
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shortestGames;
	}

	/**
	 * 
	 * Games with longest time to beat.
	 * 
	 * @param limit
	 *            number of longest games user wants.
	 * @return Map<Game, integer> of longest games and amount of time to beat
	 *         them.
	 */
	public Map<Game, Integer> getLongestGames(int limit) {
		Map<Game, Integer> longestGames = new HashMap<Game, Integer>();
		Game temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "select game_id, ((coalesce(average_complete, 0) + coalesce(average_extra, 0) + coalesce(average_main, 0)) /"
					+ "((average_complete is not null) + (average_extra is not null) + (average_main is not null)))as average from gameplaystats GROUP BY game_id ORDER BY average desc LIMIT "
					+ limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getGame(result.getInt(1), con);
				longestGames.put(temp, result.getInt(2));
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return longestGames;
	}

	/**
	 * 
	 * Method for getting users with the most number of submissions
	 * 
	 * @param limit
	 *            number of accounts user wants.
	 * @return Map<Game, integer> of accounts and number of submission they have
	 *         made.
	 */

	public Map<Account, Integer> getUsersBySubmissions(int limit) {
		Map<Account, Integer> users = new HashMap<Account, Integer>();
		Account temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT username, COUNT(username) FROM Entries "
					+ "GROUP BY username ORDER BY username DESC LIMIT " + limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getAccount(result.getString(1), con);
				users.put(temp, result.getInt(2));
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 
	 * Private method for creating account by connecting database.
	 * 
	 * @param username
	 *            username for account game info
	 * @param con
	 *            connection for connecting database.
	 * @return Account object.
	 */
	private Account getAccount(String username, Connection con)
			throws SQLException {
		Account acc = null;
		String query = "SELECT * FROM Accounts WHERE username = \"" + username
				+ "\"";
		PreparedStatement st = con.prepareStatement(query);
		ResultSet result = st.executeQuery();
		if (result.next()) {
			Account.AccountBuilder b = new AccountBuilder(result.getString(1),
					result.getString(2), result.getString(3));
			b.nickname(result.getString(4));
			b.firstName(result.getString(5));
			b.lastName(result.getString(6));
			b.birthdate(result.getDate(7));
			b.gender(result.getString(8));
			b.country(result.getString(9));
			b.avatarName(result.getString(10));
			b.steamID(result.getString(11));
			b.playStationNetworkID(result.getString(12));
			b.xboxLiveGamertag(result.getString(13));
			acc = b.build();
		}

		return acc;
	}

	/**
	 * 
	 * Getting users who have spent the most time for beating a game.
	 * 
	 * @param limit
	 *            number of accounts user wants.
	 * @return Map<Game, integer> of accounts and time for beating a game.
	 */
	public Map<Account, Integer> getUsersByHoursSpent(int limit) {
		Map<Account, Integer> users = new HashMap<Account, Integer>();
		Account temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT username, sum(ifnull(complete_gameplay_time, 0))+sum(ifnull(extra_gameplay_time, 0))+sum(ifnull(main_gameplay_time, 0)) as average"
					+ " FROM Entries group by username ORDER BY average desc LIMIT "
					+ limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getAccount(result.getString(1), con);
				users.put(temp, result.getInt(2));
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 
	 * Average age of users.
	 * 
	 * 
	 * @return average age.
	 */

	public Integer getAverageAge() {
		Integer age = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT  avg(TIMESTAMPDIFF(YEAR,birthdate,CURDATE())) FROM Accounts";
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				age = result.getInt(1);
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return age;
	}

	/**
	 * 
	 * Number of male users.
	 *
	 * 
	 * @return # of male users.
	 */
	public Integer getMaleQuantity() {
		Integer count = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT  COUNT(username) FROM Accounts WHERE gender = \"male\"";

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				count = result.getInt(1);
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 
	 * Number of female users.
	 *
	 * 
	 * @return # of female users.
	 */

	public Integer getFemaleQuantity() {
		Integer count = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT  COUNT(username) FROM Accounts WHERE gender = \"female\"";
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				count = result.getInt(1);
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 
	 * Method returns statistics about platforms(number of submission for a
	 * certain platform).
	 * 
	 * 
	 * @return Map<String, Integer> platform and amount of submissions.
	 */

	public Map<String, Integer> getPlatformStatistics() {
		Map<String, Integer> platformStats = new HashMap<String, Integer>();

		try {
			Connection con = dataSource.getConnection();

			String query = "SELECT platform, count(platform) as plat from Entries group by platform order by plat desc";
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();

			while (result.next()) {
				platformStats.put(result.getString(1), result.getInt(2));
			}
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return platformStats;
	}

	/**
	 * 
	 * Getting users who have played the most number of games.
	 * 
	 * @param limit
	 *            number of accounts user wants.
	 * @return Map<Game, integer> of accounts and number of games.
	 */

	public Map<Account, Integer> getUsersByGamesPlayed(int limit) {
		Map<Account, Integer> users = new HashMap<Account, Integer>();
		Account temp = null;

		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT username, COUNT(username) FROM Entries Group by username order by username desc limit "
					+ limit;

			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				temp = getAccount(result.getString(1), con);
				users.put(temp, result.getInt(2));
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;

	}

	/**
	 * 
	 * User who spent the least time playing the game .
	 * 
	 * @param game
	 *            Object game containing game_id.
	 * @return Account who spent least time.
	 */

	public Account getFastestUserByGame(Game game) {
		Account user = null;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT username, min(IFNULL(main_gameplay_time, IFNULL(extra_gameplay_time, complete_gameplay_time))) as gametime FROM Entries  where game_id ="
					+ game.getID()
					+ " group by username order by gametime asc limit 1 ";
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				user = getAccount(result.getString(1), con);
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;

	}

	/**
	 * 
	 * User who spent the most time playing the game .
	 * 
	 * @param game
	 *            Object game containing game_id.
	 * @return Account who spent most time.
	 */

	public Account getSlowestUserByGame(Game game) {
		Account user = null;
		try {
			Connection con = dataSource.getConnection();
			String query = " SELECT username, GREATEST(max(IFNULL(main_gameplay_time, 0)), max(IFNULL(extra_gameplay_time, 0)), max(ifnull(complete_gameplay_time, 0))) as gametime FROM Entries  where game_id = "
					+ game.getID()
					+ " group by username order by gametime desc limit 1 ";
			PreparedStatement st = con.prepareStatement(query);
			ResultSet result = st.executeQuery();
			if (result.next())
				user = getAccount(result.getString(1), con);

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;

	}
}
