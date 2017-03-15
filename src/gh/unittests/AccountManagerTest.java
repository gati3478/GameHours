package gh.unittests;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.List;

import gh.datamodel.Account;
import gh.datamodel.Entry;
import gh.datamodel.Review;
import gh.db.DBInfo;
import gh.db.managers.AccountManager;
import gh.util.Hash;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class AccountManagerTest {
	private static DataSource dataSource;
	private static AccountManager am;

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
		am = new AccountManager(dataSource);

	}

	/**
	 * Checks <code>usernameExists(String username)</code> method using actual
	 * usernames in database and non-existent ones.
	 */
	@Test
	public void testUsernameExists() {
		assertTrue(am.usernameExists("anano"));
		assertTrue(am.usernameExists("vano"));
		assertTrue(am.usernameExists("gati3478"));
		assertTrue(am.usernameExists("slutandthefalcon"));
		assertFalse(am.usernameExists("randombeing"));
		assertFalse(am.usernameExists("notinourdatabase"));
		assertFalse(am.usernameExists(null));
		assertFalse(am.usernameExists(""));
	}

	/**
	 * Checks <code>createAccount(Account acc)</code> and
	 * <code>removeAccount(Account acc)</code> methods using actual user
	 * accounts in database and non-existent ones.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testCreateRemoveAccount() throws NoSuchAlgorithmException {
		// checking if method handles illegal parameters
		Account acc1 = new Account.AccountBuilder(null, null, null).build();
		Account acc2 = new Account.AccountBuilder(null, null, "hoe@hoe.ge")
				.build();
		Account acc3 = new Account.AccountBuilder(null, Hash.hashText("smthn"),
				null).build();
		Account acc4 = new Account.AccountBuilder(null, Hash.hashText("smthn"),
				"hoe@hoe.ge").build();
		Account acc5 = new Account.AccountBuilder("newguy", null, null).build();
		Account acc6 = new Account.AccountBuilder("newguy", null, "hoe@hoe.ge")
				.build();
		Account acc7 = new Account.AccountBuilder("newguy",
				Hash.hashText("smthn"), null).build();

		assertFalse(am.createAccount(acc1));
		assertFalse(am.createAccount(acc2));
		assertFalse(am.createAccount(acc3));
		assertFalse(am.createAccount(acc4));
		assertFalse(am.createAccount(acc5));
		assertFalse(am.createAccount(acc6));
		assertFalse(am.createAccount(acc7));

		// checking if method handles accounts which already exist
		Account acc8 = new Account.AccountBuilder("gati3478",
				Hash.hashText("smthn"), "hoe@hoe.ge").build();
		Account acc9 = new Account.AccountBuilder("anano",
				Hash.hashText("smthn"), "numb@junk.ge").build();
		Account acc10 = new Account.AccountBuilder("vano",
				Hash.hashText("smthn"), "zobe@zu.ge").build();

		assertFalse(am.createAccount(acc8));
		assertFalse(am.createAccount(acc9));
		assertFalse(am.createAccount(acc10));

		// checking actual registration
		Account acc11 = new Account.AccountBuilder("newguy",
				Hash.hashText("paroli"), "sh@sh.com").build();
		assertTrue(am.createAccount(acc11));
		assertTrue(am.usernameExists(acc11.getUsername()));
		assertFalse(am.createAccount(acc11));

		Account acc12 = new Account.AccountBuilder("coolguy",
				Hash.hashText("april"), "b@a.ge").nickname("nickname")
				.firstName("firstname").lastName("lastname")
				.birthdate(Date.valueOf("1994-03-03")).gender("male")
				.country("Georgia").avatarName("life.ge").steamID("gio?")
				.playStationNetworkID("none").xboxLiveGamertag("none").build();
		assertTrue(am.createAccount(acc12));
		assertTrue(am.usernameExists(acc12.getUsername()));
		assertFalse(am.createAccount(acc12));

		// testing with actual accounts in database
		Account acc13 = new Account.AccountBuilder("coolguy", null, null)
				.build();
		assertTrue(am.usernameExists(acc13.getUsername()));
		am.removeAccount(acc13);
		assertFalse(am.usernameExists(acc13.getUsername()));

		Account acc14 = new Account.AccountBuilder("newguy", null, null)
				.build();
		assertTrue(am.usernameExists(acc14.getUsername()));
		am.removeAccount(acc14);
		assertFalse(am.usernameExists(acc14.getUsername()));

		// testing with non-existent accounts
		Account acc15 = new Account.AccountBuilder("doesnt_exist", null, null)
				.build();
		assertFalse(am.usernameExists(acc15.getUsername()));
		am.removeAccount(acc15);
		assertFalse(am.usernameExists(acc15.getUsername()));
	}

	/**
	 * Checks <code>getAccount(String username)</code> method using actual
	 * usernames in database and non-existent ones.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testGetAccount() throws NoSuchAlgorithmException {
		// passing actual username
		Account acc1 = am.getAccount("gati3478");
		assertEquals("gati3478", acc1.getUsername());
		assertEquals(Hash.hashText("gati3478"), acc1.getHashedPassword());
		assertEquals("gpetr12@freeuni.edu.ge", acc1.getEmail());

		// passing non-existent username
		Account acc2 = am.getAccount("non_existent");
		assertEquals(null, acc2);

		// passing illegal null pointer
		Account acc3 = am.getAccount(null);
		assertEquals(null, acc3);
	}

	/**
	 * Checks <code>authenticateAccount(Account acc)</code> method with actual
	 * accounts in database and non-existent ones.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testAuthenticateAccount() throws NoSuchAlgorithmException {
		for (int i = 0; i < 1000; ++i) {
			assertFalse(am.authenticateUser(null, null));
			assertTrue(am.authenticateUser("gati3478",
					Hash.hashText("gati3478")));
			assertFalse(am.authenticateUser("gati3478",
					Hash.hashText("incorrect_password")));
			assertFalse(am.authenticateUser("non-existent", null));
		}
	}

	/**
	 * Checks <code>isAdmin(Account acc)</code>,
	 * <code>addAdmin(Account acc)</code> and
	 * <code>removeAdmin(Account acc)</code> methods using actual user accounts
	 * in database and non-existent ones.
	 */
	@Test
	public void testAdminMethods() {
		// made up account
		Account acc1 = new Account.AccountBuilder("non-existent", null, null)
				.build();
		// actual admin account
		Account acc2 = am.getAccount("gati3478");
		// actual non-admin account
		Account acc3 = am.getAccount("slutandthefalcon");

		// testing isAdmin
		assertFalse(am.isAdmin(acc1));
		assertTrue(am.isAdmin(acc2));
		assertFalse(am.isAdmin(acc3));

		// testing addAdmin
		assertFalse(am.addAdmin(acc1));
		assertFalse(am.isAdmin(acc1));
		assertFalse(am.addAdmin(acc2));
		assertTrue(am.isAdmin(acc2));
		assertTrue(am.addAdmin(acc3));
		assertTrue(am.isAdmin(acc3));

		// testing removeAdmin
		am.removeAdmin(acc1);
		assertFalse(am.isAdmin(acc1));
		am.removeAdmin(acc3);
		assertFalse(am.isAdmin(acc3));
	}

	/**
	 * Checks <code>isBanned(Account acc)</code>,
	 * <code>banAccount(Account acc)</code> and
	 * <code>unbanAccount(Account acc)</code> methods using actual user accounts
	 * in database and non-existent ones.
	 */
	@Test
	public void testBanMethods() {
		// made up account
		Account acc1 = new Account.AccountBuilder("non-existent", null, null)
				.build();
		// actual account
		Account acc2 = am.getAccount("gati3478");

		// testing isBanned
		assertFalse(am.isBanned(acc1));
		assertFalse(am.isBanned(acc2));

		// testing banAccount
		assertFalse(am.banAccount(acc1));
		assertTrue(am.banAccount(acc2));
		assertTrue(am.isBanned(acc2));

		// testing unbanAccount
		am.unbanAccount(acc1);
		assertFalse(am.isBanned(acc1));
		am.unbanAccount(acc2);
		assertFalse(am.isBanned(acc2));
	}

	/**
	 * Checks
	 * <code>changeHashedPassword(Account acc, String newHashedPassword)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeHashedPassword() throws NoSuchAlgorithmException {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeHashedPassword(acc1, Hash.hashText("paroli"));
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getHashedPassword(), Hash.hashText("paroli"));
		assertEquals(acc1.getHashedPassword(), am
				.getAccount(acc1.getUsername()).getHashedPassword());
		assertTrue(am.authenticateUser(acc1.getUsername(),
				acc1.getHashedPassword()));

		// reverting back changes made to the account
		am.changeHashedPassword(acc1, Hash.hashText("gati3478"));
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getHashedPassword(), Hash.hashText("gati3478"));
		assertEquals(acc1.getHashedPassword(), am
				.getAccount(acc1.getUsername()).getHashedPassword());
		assertTrue(am.authenticateUser(acc1.getUsername(),
				acc1.getHashedPassword()));
	}

	/**
	 * Checks <code>changeEmail(Account acc, String newEmail)</code> method
	 * using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeEmail() throws NoSuchAlgorithmException {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeEmail(acc1, "wut@wut.com");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getEmail(), "wut@wut.com");
		assertEquals(acc1.getEmail(), am.getAccount(acc1.getUsername())
				.getEmail());

		// reverting back changes made to the account
		am.changeEmail(acc1, "gpetr12@freeuni.edu.ge");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getEmail(), "gpetr12@freeuni.edu.ge");
		assertEquals(acc1.getEmail(), am.getAccount(acc1.getUsername())
				.getEmail());
	}

	/**
	 * Checks <code>changeNickname(Account acc, String newNickname)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeNickname() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeNickname(acc1, "hehe");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getNickname(), "hehe");
		assertEquals(acc1.getNickname(), am.getAccount(acc1.getUsername())
				.getNickname());

		// reverting back changes made to the account
		am.changeNickname(acc1, "Songbird");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getNickname(), "Songbird");
		assertEquals(acc1.getNickname(), am.getAccount(acc1.getUsername())
				.getNickname());
	}

	/**
	 * Checks <code>changeFirstName(Account acc, String newFirstName)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeFirstName() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeFirstName(acc1, "Babayoli");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getFirstName(), "Babayoli");
		assertEquals(acc1.getFirstName(), am.getAccount(acc1.getUsername())
				.getFirstName());

		// reverting back changes made to the account
		am.changeFirstName(acc1, "Giorgi");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getFirstName(), "Giorgi");
		assertEquals(acc1.getFirstName(), am.getAccount(acc1.getUsername())
				.getFirstName());
	}

	/**
	 * Checks <code>changeLastName(Account acc, String newLastName)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeLastName() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeLastName(acc1, "Babayoli");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getLastName(), "Babayoli");
		assertEquals(acc1.getLastName(), am.getAccount(acc1.getUsername())
				.getLastName());

		// reverting back changes made to the account
		am.changeLastName(acc1, "Petriashvili");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getLastName(), "Petriashvili");
		assertEquals(acc1.getLastName(), am.getAccount(acc1.getUsername())
				.getLastName());
	}

	/**
	 * Checks <code>changeBirthdate(Account acc, Date newBirthdate)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeBirthdate() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeBirthdate(acc1, Date.valueOf("1994-01-01"));
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getBirthdate(), Date.valueOf("1994-01-01"));
		assertEquals(acc1.getBirthdate(), am.getAccount(acc1.getUsername())
				.getBirthdate());

		// reverting back changes made to the account
		am.changeBirthdate(acc1, Date.valueOf("1994-01-24"));
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getBirthdate(), Date.valueOf("1994-01-24"));
		assertEquals(acc1.getBirthdate(), am.getAccount(acc1.getUsername())
				.getBirthdate());
	}

	/**
	 * Checks <code>changeGender(Account acc, String newGender)</code> method
	 * using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeGender() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeGender(acc1, "female");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getGender(), "female");
		assertEquals(acc1.getGender(), am.getAccount(acc1.getUsername())
				.getGender());

		// reverting back changes made to the account
		am.changeGender(acc1, "male");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getGender(), "male");
		assertEquals(acc1.getGender(), am.getAccount(acc1.getUsername())
				.getGender());
	}

	/**
	 * Checks <code>Country(Account acc, String newCountry)</code> method using
	 * actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeCountry() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeCountry(acc1, "Japan");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getCountry(), "Japan");
		assertEquals(acc1.getCountry(), am.getAccount(acc1.getUsername())
				.getCountry());

		// reverting back changes made to the account
		am.changeCountry(acc1, "Georgia");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getCountry(), "Georgia");
		assertEquals(acc1.getCountry(), am.getAccount(acc1.getUsername())
				.getCountry());
	}

	/**
	 * Checks <code>changeAvatarName(Account acc, String newAvatarName)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeAvatarName() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeAvatarName(acc1, "wayne.jpg");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getAvatarFilename(), "wayne.jpg");
		assertEquals(acc1.getAvatarFilename(), am
				.getAccount(acc1.getUsername()).getAvatarFilename());

		// reverting back changes made to the account
		am.changeAvatarName(acc1,
				"11e65581b237604efdaf1bae515b7d3633187353_full.jpg");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getAvatarFilename(),
				"11e65581b237604efdaf1bae515b7d3633187353_full.jpg");
		assertEquals(acc1.getAvatarFilename(), am
				.getAccount(acc1.getUsername()).getAvatarFilename());
	}

	/**
	 * Checks <code>changeSteamID(Account acc, String newSteamID)</code> method
	 * using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeSteamID() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeSteamID(acc1, "wut");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getSteamID(), "wut");
		assertEquals(acc1.getSteamID(), am.getAccount(acc1.getUsername())
				.getSteamID());

		// reverting back changes made to the account
		am.changeSteamID(acc1, "gio777333x");
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getSteamID(), "gio777333x");
		assertEquals(acc1.getSteamID(), am.getAccount(acc1.getUsername())
				.getSteamID());
	}

	/**
	 * Checks
	 * <code>changePlayStationNetworkID(Account acc, String newPSNID)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangePlayStationNetworkID() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changePlayStationNetworkID(acc1, null);
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getPlayStationNetworkID(), null);
		assertEquals(acc1.getPlayStationNetworkID(),
				am.getAccount(acc1.getUsername()).getPlayStationNetworkID());
	}

	/**
	 * Checks
	 * <code>changeXboxLiveGamertag(Account acc, String newXboxLiveGamertag)</code>
	 * method using actual user accounts in database.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testChangeXboxLiveGamertag() {
		// actual account
		Account acc1 = am.getAccount("gati3478");
		am.changeXboxLiveGamertag(acc1, null);
		acc1 = am.getAccount("gati3478");
		assertEquals(acc1.getXboxLiveGamertag(), null);
		assertEquals(acc1.getXboxLiveGamertag(),
				am.getAccount(acc1.getUsername()).getXboxLiveGamertag());
	}

	/**
	 * Checks <code>getEntries(Account acc, int page, int limit)</code> and
	 * <code>getEntriesQuantity(Account acc)</code> methods using actual user
	 * accounts in database and non-existent ones.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testGetEntries() {
		// testing with made up account
		Account acc1 = new Account.AccountBuilder("madeup", null, null).build();
		int quantity1 = am.getEntriesQuantity(acc1);
		assertEquals(0, quantity1);
		Account acc2 = new Account.AccountBuilder(null, null, null).build();
		int quantity2 = am.getEntriesQuantity(acc2);
		assertEquals(0, quantity2);

		// testing with actual account
		Account acc3 = am.getAccount("anano");
		int quantity3 = am.getEntriesQuantity(acc3);
		assertEquals(6, quantity3);
		List<Entry> entries = am.getEntries(acc3, 1, 3);
		assertEquals(3, entries.size());
		System.out.println(entries.get(0));
		System.out.println(entries.get(1));
		System.out.println(entries.get(2));
	}

	/**
	 * Checks <code>getReviews(Account acc, int page, int limit)</code> and
	 * <code>getReviewsQuantity(Account acc)</code> methods using actual user
	 * accounts in database and non-existent ones.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testGetReviews() {
		// testing with made up account
		Account acc1 = new Account.AccountBuilder("madeup", null, null).build();
		int quantity1 = am.getReviewsQuantity(acc1);
		assertEquals(0, quantity1);
		Account acc2 = new Account.AccountBuilder(null, null, null).build();
		int quantity2 = am.getReviewsQuantity(acc2);
		assertEquals(0, quantity2);

		// testing with actual account
		Account acc3 = am.getAccount("anano");
		int quantity3 = am.getReviewsQuantity(acc3);
		assertEquals(6, quantity3);
		List<Review> reviews = am.getReviews(acc3, 1, 3);
		assertEquals(3, reviews.size());
		System.out.println(reviews.get(0));
		System.out.println(reviews.get(1));
		System.out.println(reviews.get(2));
	}

}
