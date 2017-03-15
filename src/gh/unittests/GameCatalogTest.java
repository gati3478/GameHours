package gh.unittests;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import gh.datamodel.Entry;
import gh.datamodel.Game;
import gh.datamodel.GameplayTimes;
import gh.datamodel.Review;
import gh.db.DBInfo;
import gh.db.managers.GameCatalog;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class GameCatalogTest {
	private static DataSource dataSource;
	private static GameCatalog gc;

	/*
	 * Do not forget to include tomcat-juli.jar file in classpath so that JUnit
	 * can see InitialContext object. Also include
	 * mysql-connector-java-x.x.x-bin.jar to have access to
	 * MysqlConnectionPoolDataSource object.
	 */

	/**
	 * Injects JNDI datasources for JUnit Tests outside of a container.
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		// Create initial context
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		Context ic = new InitialContext();

		ic.createSubcontext("java:");
		ic.createSubcontext("java:/comp");
		ic.createSubcontext("java:/comp/env");
		ic.createSubcontext("java:/comp/env/jdbc");

		// Construct DataSource
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setURL("jdbc:mysql://localhost:3306/" + DBInfo.DB_NAME
				+ "?characterEncoding=UTF-8");
		ds.setUser(DBInfo.USERNAME); // use your username
		ds.setPassword(DBInfo.PASSWORD); // use your password

		ic.bind("java:/comp/env/jdbc/" + DBInfo.DB_NAME, ds);

		Context initContext = new InitialContext();
		Context webContext = (Context) initContext.lookup("java:/comp/env");

		// Initializes DataSource and AccountManager classes based on injected
		// resources
		dataSource = (DataSource) webContext.lookup("jdbc/" + DBInfo.DB_NAME);
		gc = new GameCatalog(dataSource);
	}

	public void testAddGame() {
		// Test0
		List<String> genres = new ArrayList<String>();
		genres.add("Action");
		genres.add("Indie");
		List<String> platforms = new ArrayList<String>();
		platforms.add("PSP");
		platforms.add("PC");
		platforms.add("XBOX");
		Date date = Date.valueOf("1994-01-06");
		Game game = new Game.GameBuilder("anano").genres(genres)
				.platforms(platforms).releaseDate(date).imageName("bla.jpg")
				.developers("asd").publishers("gati").shortDescription("kaia")
				.build();
		assertTrue(gc.addGame(game) != -1);

		// Test1
		List<String> genres1 = new ArrayList<String>();
		List<String> platforms1 = new ArrayList<String>();
		Date date1 = null;
		Game game1 = new Game.GameBuilder(13, "anano").genres(genres1)
				.platforms(platforms1).releaseDate(date1).build();
		assertTrue(gc.addGame(game1) != -1);

		// Test2
		List<String> genres2 = new ArrayList<String>();
		List<String> platforms2 = new ArrayList<String>();
		platforms2.add("PSP");
		platforms2.add("PC");
		platforms2.add("XBOX");
		Date date2 = null;
		Game game2 = new Game.GameBuilder("anano").genres(genres2)
				.platforms(platforms2).releaseDate(date2).imageName("bla.jpg")
				.build();
		assertTrue(gc.addGame(game2) != -1);

		// Test3
		List<String> genres3 = new ArrayList<String>();
		List<String> platforms3 = new ArrayList<String>();
		Date date3 = null;
		Game game3 = new Game.GameBuilder(null, null).genres(genres3)
				.platforms(platforms3).releaseDate(date3).build();
		assertFalse(gc.addGame(game3) == -1);
	}

	@Test
	public void getGameTest() {
		Game game = gc.getGame(4);
		assertEquals(game.getName(), "Machinarium");

		List<String> genres = new ArrayList<String>();
		genres.add("Action");
		genres.add("RPG");
		genres.add("Survival Horror");
		assertEquals(game.getGenres(), genres);

		List<String> platforms = new ArrayList<String>();
		platforms.add("PC");
		assertEquals(game.getPlatforms(), platforms);

		Date date = Date.valueOf("1999-08-11");
		assertEquals(game.getReleaseDate(), date);

		assertEquals(game.getImageName(), "ss2.jpg");
		assertEquals(game.getDevelopers(),
				"Irrational Games, Looking Glass Studios");
		assertEquals(game.getPublishers(), "Electronic Arts");
		assertEquals(
				game.getShortDescription(),
				"The game takes place on board a starship in a cyberpunk depiction of 2114. The player assumes the role of a lone soldier trying to stem the outbreak of a genetic infection that has devastated the ship. Like System Shock, gameplay consists of first-person shooting and exploration. It also incorporates role-playing system elements, in which the player can develop unique skills and traits, such as hacking and psionic abilities.");

	}

	@Test
	public void containsGameTest() {
		Game game = gc.getGame(3);
		assertTrue(gc.containsGame(game));
		assertFalse(gc.containsGame(null));
	}

	@Test
	public void updateGameImageTest() {
		Game game = gc.getGame(2);
		assertTrue(gc.changeGameImage(game, "bla.jpg"));

	}

	@Test
	public void getCatalogSizeTest() {
		assertEquals(34, gc.getCatalogSize());

	}

	@Test
	public void getImagesQuantityTest() {
		Game game = gc.getGame(4);
		assertEquals(1, gc.getImagesQuantity(game));

	}

	@Test
	public void getVideoLinksQuantityTest() {
		Game game = gc.getGame(4);
		assertEquals(2, gc.getVideoLinksQuantity(game));

	}

	@Test
	public void addRemoveEntryTest() throws SQLException {
		Integer mainTime = new Integer(5);
		Integer extraTime = new Integer(5);
		Integer completeTIme = new Integer(5);
		GameplayTimes times = new GameplayTimes(mainTime, extraTime,
				completeTIme);
		Timestamp subDate = Timestamp.valueOf("2009-10-02 16:52:30");
		Entry entry = new Entry("anano", 1, "opa", times, subDate);
		assertTrue(gc.addEntry(entry));
	}

	@Test
	public void updateEntryTest() {
		Integer mainTime = new Integer(5);
		Integer extraTime = new Integer(5);
		Integer completeTIme = new Integer(5);
		GameplayTimes times = new GameplayTimes(mainTime, extraTime,
				completeTIme);
		Timestamp subDate = Timestamp.valueOf("1111-10-02 16:52:30");
		Entry entry = new Entry(1, "anano", 1, "opa", times, subDate);
		assertTrue(gc.updateEntry(entry));
	}

	@Test
	public void updateReviewTest() {
		Timestamp time = Timestamp.valueOf("1111-10-02 16:52:30");
		Review r = new Review("anano", 1, 10, "bla", time);
		assertTrue(gc.updateReview(r));
	}

	@Test
	public void addRemoveReviewTest() {
		Timestamp time = Timestamp.valueOf("2009-10-02 16:52:30");
		Review r = new Review("vano", 3, 10, "bla", time);
		assertTrue(gc.addReview(r));
		gc.removeReview(r);
	}

	@Test
	public void getGamesTest() {
		List<Game> games = gc.getGames(1, 3);
		assertEquals(3, games.size());
	}

	@Test
	public void getEntriesTest() {
		List<Entry> entries = gc.getEntries(1, 3);
		assertEquals(3, entries.size());
	}

	@Test
	public void getReviewsTest() {
		Game game = gc.getGame(4);
		List<Review> reviews = gc.getReviews(game, 1, 1000);
		assertEquals(4, reviews.size());
	}

	@Test
	public void getImagesTest() {
		Game game = gc.getGame(4);
		assertEquals(1, gc.getImages(game).size());
	}

	@Test
	public void getVideoLinksTest() {
		Game game = gc.getGame(4);
		assertEquals(2, gc.getVideoLinks(game).size());
	}

	@Test
	public void changeGameNameTest() {
		Game game = gc.getGame(3);
		gc.changeGameName(game, "anano");
		assertEquals("anano", gc.getGame(3).getName());
		gc.changeGameName(game, game.getName());
		assertEquals(game.getName(), gc.getGame(3).getName());
	}

	@Test
	public void changeGameImagefileTest() {
		Game game = gc.getGame(3);
		gc.changeGameImage(game, "anano");
		assertEquals("anano", gc.getGame(3).getImageName());
		gc.changeGameImage(game, game.getImageName());
		assertEquals(game.getImageName(), gc.getGame(3).getImageName());
	}

	@Test
	public void changeGameDevelopersTest() {
		Game game = gc.getGame(3);
		gc.changeGameDevelopers(game, "anano");
		assertEquals("anano", gc.getGame(3).getDevelopers());
		gc.changeGameDevelopers(game, game.getDevelopers());
		assertEquals(game.getDevelopers(), gc.getGame(3).getDevelopers());
	}

	@Test
	public void changeGamePublishersTest() {
		Game game = gc.getGame(3);
		gc.changeGamePublishers(game, "anano");
		assertEquals("anano", gc.getGame(3).getPublishers());
		gc.changeGamePublishers(game, game.getPublishers());
		assertEquals(game.getPublishers(), gc.getGame(3).getPublishers());
	}

	@Test
	public void changeGameReleaseDateTest() {
		Game game = gc.getGame(3);
		Date date = Date.valueOf("1111-11-28");
		gc.changeGameReleaseDate(game, date);
		assertEquals(date, gc.getGame(3).getReleaseDate());
		gc.changeGameReleaseDate(game, game.getReleaseDate());
		assertEquals(game.getReleaseDate(), gc.getGame(3).getReleaseDate());
	}

	@Test
	public void changeGameShortDescriptionTest() {
		Game game = gc.getGame(3);
		gc.changeGameShortDescription(game, "anano");
		assertEquals("anano", gc.getGame(3).getShortDescription());
		gc.changeGameShortDescription(game, game.getShortDescription());
		assertEquals(game.getShortDescription(), gc.getGame(3)
				.getShortDescription());
	}

	@Test
	public void addRemoveGenreTest() {
		// testing adding genre
		Game game = gc.getGame(9);
		assertFalse(game.getGenres().contains("newgenre"));
		assertTrue(gc.addGameGenre(game, "newgenre"));
		game = gc.getGame(game.getID());
		assertTrue(game.getGenres().contains("newgenre"));

		// testing removing genre
		assertTrue(game.getGenres().contains("newgenre"));
		assertTrue(gc.removeGameGenre(game, "newgenre"));
		game = gc.getGame(game.getID());
		assertFalse(game.getGenres().contains("newgenre"));
	}

	@Test
	public void addRemovePlatformTest() {
		// testing adding platform
		Game game = gc.getGame(9);
		assertFalse(game.getPlatforms().contains("newplatform"));
		assertTrue(gc.addGamePlatform(game, "newplatform"));
		game = gc.getGame(game.getID());
		assertTrue(game.getPlatforms().contains("newplatform"));

		// testing removing platform
		assertTrue(game.getPlatforms().contains("newplatform"));
		assertTrue(gc.removeGamePlatform(game, "newplatform"));
		game = gc.getGame(game.getID());
		assertFalse(game.getPlatforms().contains("newplatform"));
	}

}
