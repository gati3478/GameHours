package gh.unittests;

import static org.junit.Assert.*;
import gh.datamodel.Game;
import gh.db.DBInfo;
import gh.db.managers.GameCatalog;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class SearchGameTest {

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

	@Test
	// only game_name
	public void searchGameTest1() {
		List<Game> games1 = gc.searchGame("Mark of the Ninja", null, null,
				null, 1, 10);
		assertEquals(1, games1.size());
	}

	@Test
	// only genre
	public void searchGameTest2() {
		List<Game> games1 = gc.searchGame(null, "Action", null, null, 1, 20);
		assertEquals(18, games1.size());
	}

	@Test
	// only startTime
	public void searchGameTes3() {
		Integer min = new Integer(5);
		List<Game> games = gc.searchGame(null, null, min, null, 1, 10);
		assertEquals(6, games.size());
	}

	@Test
	// only endTime
	public void searchGameTest4() {
		Integer max = new Integer(15);
		List<Game> games = gc.searchGame(null, null, null, max, 1, 10);
		assertEquals(7, games.size());
	}

	@Test
	// startTime and endTime
	public void searchGameTest5() {
		Integer min = new Integer(10);
		Integer max = new Integer(15);
		List<Game> games1 = gc.searchGame(null, null, min, max, 1, 10);
		assertEquals(2, games1.size());
	}

	@Test
	// genre and startTime
	public void searchGameTest6() {
		Integer min = new Integer(0);
		List<Game> games1 = gc.searchGame(null, "Action", min, null, 1, 10);
		assertEquals(3, games1.size());
	}

	@Test
	// genre and endTime
	public void searchGameTest7() {
		Integer max = new Integer(20);
		List<Game> games1 = gc.searchGame(null, "Action", null, max, 1, 10);
		assertEquals(2, games1.size());
	}

	@Test
	// name and startTime
	public void searchGameTest8() {
		Integer min = new Integer(0);
		List<Game> games1 = gc
				.searchGame("Machinarium", null, min, null, 1, 10);
		assertEquals(1, games1.size());

		Integer min1 = new Integer(20);
		List<Game> games = gc
				.searchGame("Machinarium", null, min1, null, 1, 10);
		assertEquals(0, games.size());

	}

	@Test
	// name and endTime
	public void searchGameTest9() {
		Integer max = new Integer(1);
		List<Game> games1 = gc
				.searchGame("Machinarium", null, null, max, 1, 10);
		assertEquals(0, games1.size());

		Integer max1 = new Integer(20);
		List<Game> games = gc
				.searchGame("Machinarium", null, null, max1, 1, 10);
		assertEquals(1, games.size());

	}

	@Test
	// name end genre
	public void searchGameTest10() {
		List<Game> games1 = gc.searchGame("Machinarium", "", null, null, 1, 10);
		assertEquals(1, games1.size());

		List<Game> games = gc.searchGame("Machinarium", "Action", null, null,
				1, 10);
		assertEquals(0, games.size());
	}

	@Test
	// no endTime
	public void searchGameTest11() {
		Integer min = new Integer(1);
		List<Game> games1 = gc.searchGame("Mass Effect 3", "Action", min, null,
				1, 10);
		assertEquals(1, games1.size());

		Integer min1 = new Integer(20);
		List<Game> games = gc.searchGame("Mass Effect 3", "Action", min1, null,
				1, 10);
		assertEquals(0, games.size());
	}

	@Test
	// no startTime
	public void searchGameTest12() {
		Integer min = new Integer(50);
		List<Game> games1 = gc.searchGame("Super Meat Boy", "Shooter", null,
				min, 1, 10);
		assertEquals(1, games1.size());

		Integer max1 = new Integer(20);
		List<Game> games = gc.searchGame("Mass Effect 3", "Action", null, max1,
				1, 10);
		assertEquals(0, games.size());
	}

	@Test
	// no genre
	public void searchGameTest13() {
		Integer min = new Integer(11);
		Integer max = new Integer(15);
		List<Game> games1 = gc.searchGame("System Shock 2", null, min, max, 1,
				10);
		assertEquals(1, games1.size());
	}

	@Test
	// no name
	public void searchGameTest14() {
		Integer min = new Integer(1);
		Integer max = new Integer(40);
		List<Game> games1 = gc.searchGame(null, "Racing", min, max, 1, 10);
		assertEquals(1, games1.size());
	}

	@Test
	// everything
	public void searchGameTest15() {
		Integer min = new Integer(1);
		Integer max = new Integer(40);
		List<Game> games1 = gc.searchGame("Carmageddon II: Carpocalypse Now",
				"Racing", min, max, 1, 10);
		assertEquals(1, games1.size());
	}

}
