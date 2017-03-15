package gh.unittests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gh.datamodel.Game;
import gh.datamodel.GameplayTimes;
import gh.db.DBInfo;
import gh.db.managers.Statistics;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class StatisticsTest {
	private static DataSource dataSource;
	private static Statistics stats;

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

		// Initializes DataSource and Statistics classes based on injected
		// resources
		dataSource = (DataSource) webContext.lookup("jdbc/" + DBInfo.DB_NAME);
		stats = new Statistics(dataSource);

	}

	@Test
	public void testAverageRating() {
		Game gameA = new Game.GameBuilder(3, "System Shock 2").build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").build();
		Integer ratingA = new Integer(75);
		Integer ratingB = new Integer(84);

		assertEquals(ratingA, stats.getAverageRating(gameA));
		assertEquals(ratingB, stats.getAverageRating(gameB));

	}

	@Test
	public void averageGameplayTimestest() {

		Game gameA = new Game.GameBuilder(2, "Half-life").build();
		Game gameB = new Game.GameBuilder(1, "Carmageddon II: Carpocalypse Now")
				.build();

		GameplayTimes tempA = new GameplayTimes(12, 0, 19);
		GameplayTimes tempB = new GameplayTimes(4, 0, 35);

		assertTrue(tempA.equals(stats.getAverageGameplayTimes(gameA)));
		assertTrue(tempB.equals(stats.getAverageGameplayTimes(gameB)));
	}

	@Test
	public void minGameplayTimestest() {

		Game gameA = new Game.GameBuilder(4, "Machinarium").build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").build();

		GameplayTimes tempA = new GameplayTimes(4, 4, 4);
		GameplayTimes tempB = new GameplayTimes(10, 30, 60);

		assertTrue(tempA.equals(stats.getMinGameplayTimes(gameA)));
		assertTrue(tempB.equals(stats.getMinGameplayTimes(gameB)));
	}

	@Test
	public void maxGameplayTimestest() {

		Game gameA = new Game.GameBuilder(8, "Braid").build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").build();

		GameplayTimes tempA = new GameplayTimes(6, 0, 0);
		GameplayTimes tempB = new GameplayTimes(20, 40, 60);

		assertTrue(tempA.equals(stats.getMaxGameplayTimes(gameA)));
		assertTrue(tempB.equals(stats.getMaxGameplayTimes(gameB)));
	}

	@Test
	public void platformStatsTest() {
		List<String> platfromsA = new ArrayList<String>();
		List<String> platfromsB = new ArrayList<String>();
		platfromsA.add("PC");
		platfromsB.add("PC");
		platfromsB.add("Xbox 360");
		platfromsB.add("Playstation 3");

		Game gameA = new Game.GameBuilder(8, "Braid").platforms(platfromsA)
				.build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").platforms(
				platfromsB).build();

		Map<String, Integer> tempA = new HashMap<String, Integer>();
		Map<String, Integer> tempB = new HashMap<String, Integer>();
		tempA.put("PC", 1);
		tempB.put("PC", 2);
		tempB.put("Xbox 360", 1);
		tempB.put("Playstation 3", 1);

		assertEquals(tempA, stats.getPlatformStats(gameA));
		assertEquals(tempB, stats.getPlatformStats(gameB));
	}

	@Test
	public void popularGamesTest() {

		Game gameA = new Game.GameBuilder(1, "Carmageddon II: Carpocalypse Now")
				.build();
		Game gameB = new Game.GameBuilder(2, "Half-Life").build();
		Game gameC = new Game.GameBuilder(3, "System Shock 2").build();
		Game gameD = new Game.GameBuilder(5, "Mass Effect 3").build();
		Game gameE = new Game.GameBuilder(4, "Machinarium").build();

		assertEquals(gameA.getName(), stats.getPopularGames(1, 5).get(0)
				.getName());
		assertEquals(gameB.getName(), stats.getPopularGames(1, 5).get(1)
				.getName());
		assertEquals(gameC.getName(), stats.getPopularGames(1, 5).get(2)
				.getName());
		assertEquals(gameD.getName(), stats.getPopularGames(1, 5).get(3)
				.getName());
		assertEquals(gameE.getName(), stats.getPopularGames(1, 5).get(4)
				.getName());

	}

	@Test
	public void testAverageAge() {
		Integer averageAge = 20;
		assertEquals(averageAge, stats.getAverageAge());
	}

	@Test
	public void testMaleQuantity() {
		Integer averageAge = 2;
		assertEquals(averageAge, stats.getMaleQuantity());
	}

	@Test
	public void testFemaleQuantity() {
		Integer averageAge = 2;
		assertEquals(averageAge, stats.getFemaleQuantity());
	}

	@Test
	public void fastetUserTest() {
		Game gameA = new Game.GameBuilder(3, "System Shock 2").build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").build();
		assertEquals("vano", stats.getFastestUserByGame(gameA).getUsername());
		assertEquals("vano", stats.getFastestUserByGame(gameB).getUsername());
	}

	@Test
	public void slowestUserTest() {
		Game gameA = new Game.GameBuilder(3, "System Shock 2").build();
		Game gameB = new Game.GameBuilder(5, "Mass Effect 3").build();
		assertEquals("gati3478", stats.getSlowestUserByGame(gameA)
				.getUsername());
		assertEquals("gati3478", stats.getSlowestUserByGame(gameB)
				.getUsername());
	}

	@Test
	public void sample() {

		stats.getLongestGames(5);
		stats.getMostSubmittedGames(5);
		stats.getShortestGames(5);
		stats.getTopRatedGames(5);
		stats.getUsersByGamesPlayed(5);
		stats.getUsersByHoursSpent(5);
		stats.getUsersBySubmissions(5);

	}

}
