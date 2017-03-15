package gh.db.managers;

import gh.datamodel.Account;
import gh.datamodel.Entry;
import gh.datamodel.GameplayTimes;
import gh.datamodel.Review;
import gh.datamodel.Account.AccountBuilder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class AccountManager extends Manager {
	public static final String ATTRIBUTE_NAME = "account_manager";

	/**
	 * Constructs AccountManager object with provided DataSource object.
	 * 
	 * @param dataSource
	 *            DataSource object representing connection pool.
	 */
	public AccountManager(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Checks if the given username exists in the database (i.e. there exists a
	 * registered account associated with it), assuming it is a unique account
	 * identifier.
	 * 
	 * @param username
	 *            A unique user (account) identifier (ID).
	 * @return true if username exists in the database, false otherwise.
	 */
	public boolean usernameExists(String username) {
		try {
			Connection con = dataSource.getConnection();
			String query = generateSimpleSelectQuery("Accounts",
					new ArrayList<String>(), "username", username);
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
	 * Creates account from given account model.
	 * 
	 * @param acc
	 *            Account model.
	 * @return true if the account creation is successful, false otherwise,
	 *         namely, if any of the mandatory parameters is null or if account
	 *         with the same username already exists.
	 */
	public boolean createAccount(Account acc) {
		// username, password and email are mandatory fields
		if (acc.getUsername() == null || acc.getHashedPassword() == null
				|| acc.getEmail() == null)
			return false;

		if (usernameExists(acc.getUsername()))
			return false;

		// preparing insert values
		List<String> insertValues = new ArrayList<String>();
		fillWithAccountData(insertValues, acc);
		// registering account
		executeInsert("Accounts", insertValues);

		return true;
	}

	/*
	 * Fills List object with account data prior to insert into the database.
	 */
	private void fillWithAccountData(List<String> values, Account acc) {
		values.add(acc.getUsername());
		values.add(acc.getHashedPassword());
		values.add(acc.getEmail());
		values.add(acc.getNickname());
		values.add(acc.getFirstName());
		values.add(acc.getLastName());
		if (acc.getBirthdate() != null)
			values.add(acc.getBirthdate().toString());
		else
			values.add(null);
		values.add(acc.getGender());
		values.add(acc.getCountry());
		values.add(acc.getAvatarFilename());
		values.add(acc.getSteamID());
		values.add(acc.getPlayStationNetworkID());
		values.add(acc.getXboxLiveGamertag());
	}

	/**
	 * Returns account model by given username, assuming it is a unique account
	 * identifier.
	 * 
	 * @param username
	 *            A unique user (account) identifier (ID).
	 * @return Account model if username was found, null otherwise.
	 */
	public Account getAccount(String username) {
		Account acc = null;
		try {
			Connection con = dataSource.getConnection();
			String query = generateSimpleSelectQuery("Accounts",
					new ArrayList<String>(), "username", username);
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				Account.AccountBuilder b = new AccountBuilder(rs.getString(1),
						rs.getString(2), rs.getString(3));
				b.nickname(rs.getString(4));
				b.firstName(rs.getString(5));
				b.lastName(rs.getString(6));
				b.birthdate(rs.getDate(7));
				b.gender(rs.getString(8));
				b.country(rs.getString(9));
				b.avatarName(rs.getString(10));
				b.steamID(rs.getString(11));
				b.playStationNetworkID(rs.getString(12));
				b.xboxLiveGamertag(rs.getString(13));
				acc = b.build();
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return acc;
	}

	/**
	 * Returns the list of accounts within the given range.
	 * 
	 * @param page
	 *            The page the user is browsing.
	 * @param limit
	 *            The limit of results per page.
	 * @return The list of users within the given range.
	 * @throws SQLException
	 */
	public List<Account> getAccounts(int page, int limit) throws SQLException {
		List<Account> accounts = new ArrayList<Account>();
		Connection con = dataSource.getConnection();
		String query = "SELECT username FROM Accounts LIMIT ?, ?;";
		PreparedStatement statement = con.prepareStatement(query);
		statement.setInt(1, (page - 1) * limit);
		statement.setInt(2, limit);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			Account acc = getAccount(rs.getString(1));
			accounts.add(acc);
		}
		con.close();
		return accounts;
	}

	/**
	 * Returns the number of accounts in database.
	 * 
	 * @return The number of accounts in database.
	 */
	public int getAccountsQuantity() {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			String query = "SELECT COUNT(username) FROM Accounts";
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
	 * Makes an user authentication attempt. Namely, checks if the given user
	 * exists, if so, checks whether the password is correct.
	 * 
	 * @param username
	 *            A unique user (account) identifier (ID).
	 * @param hashedPassword
	 *            Hashed password.
	 * @return true if authentication was successful (user exists and password
	 *         matches), false if account didn't exist or password was
	 *         incorrect.
	 */
	public boolean authenticateUser(String username, String hashedPassword) {
		Account target = getAccount(username);
		if (target != null)
			return hashedPassword.equals(target.getHashedPassword());
		return false;
	}

	/**
	 * Checks whether the given account has administrator privileges.
	 * 
	 * @param acc
	 *            Account model.
	 * @return true if given account has administrator privileges, false
	 *         otherwise.
	 */
	public boolean isAdmin(Account acc) {
		try {
			Connection con = dataSource.getConnection();
			String query = generateSimpleSelectQuery("Admins",
					new ArrayList<String>(), "username", acc.getUsername());
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			boolean isAdmin = result.next();
			con.close();

			return isAdmin;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Adds new administrator based on given account.
	 * 
	 * @param acc
	 *            Account model.
	 * @return true if admin account was successfully added, false otherwise.
	 */
	public boolean addAdmin(Account acc) {
		String username = acc.getUsername();
		if (usernameExists(username) && !isAdmin(acc)) {
			List<String> values = new ArrayList<String>();
			values.add(username);
			executeInsert("Admins", values);
			return true;
		}
		return false;
	}

	/**
	 * Removes administrator based on given account.
	 * 
	 * @param acc
	 *            Account model.
	 */
	public void removeAdmin(Account acc) {
		executeSimpleDelete("Admins", "username", acc.getUsername());
	}

	/**
	 * Checks whether the given account is banned.
	 * 
	 * @param acc
	 *            Account model.
	 * @return true if given account is banned, false otherwise.
	 */
	public boolean isBanned(Account acc) {
		try {
			Connection con = dataSource.getConnection();
			String query = generateSimpleSelectQuery("BannedAccounts",
					new ArrayList<String>(), "username", acc.getUsername());
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			boolean isBanned = result.next();
			con.close();

			return isBanned;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Bans given account.
	 * 
	 * @param acc
	 *            Account model.
	 * @return true if account was successfully baned, false otherwise, namely,
	 *         if account was not valid.
	 */
	public boolean banAccount(Account acc) {
		String username = acc.getUsername();
		if (usernameExists(username)) {
			List<String> values = new ArrayList<String>();
			values.add(username);
			executeInsert("BannedAccounts", values);
			return true;
		}
		return false;
	}

	/**
	 * Removes ban from given account.
	 * 
	 * @param acc
	 *            Account model.
	 */
	public void unbanAccount(Account acc) {
		executeSimpleDelete("BannedAccounts", "username", acc.getUsername());
	}

	/**
	 * Changes user account's password to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newHashedPassword
	 *            New (updated) hashed password.
	 */
	public void changeHashedPassword(Account acc, String newHashedPassword) {
		executeSimpleUpdate("Accounts", "hashed_password", newHashedPassword,
				"username", acc.getUsername());
	}

	/**
	 * Changes user account's email address to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newEmail
	 *            New (updated) email address.
	 */
	public void changeEmail(Account acc, String newEmail) {
		executeSimpleUpdate("Accounts", "email", newEmail, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's nickname to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newNickname
	 *            New (updated) nickname.
	 */
	public void changeNickname(Account acc, String newNickname) {
		executeSimpleUpdate("Accounts", "nickname", newNickname, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's first name to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newFirstName
	 *            New (updated) first name.
	 */
	public void changeFirstName(Account acc, String newFirstName) {
		executeSimpleUpdate("Accounts", "first_name", newFirstName, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's last name to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newLastName
	 *            New (updated) last name.
	 */
	public void changeLastName(Account acc, String newLastName) {
		executeSimpleUpdate("Accounts", "last_name", newLastName, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's birthdate to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newBirthdate
	 *            New (updated) birthdate.
	 */
	public void changeBirthdate(Account acc, Date newBirthdate) {
		String birthday = null;
		if (newBirthdate != null)
			birthday = newBirthdate.toString();
		executeSimpleUpdate("Accounts", "birthdate", birthday, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's gender to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newGender
	 *            New (updated) gender.
	 */
	public void changeGender(Account acc, String newGender) {
		executeSimpleUpdate("Accounts", "gender", newGender, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's country to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newCountry
	 */
	public void changeCountry(Account acc, String newCountry) {
		executeSimpleUpdate("Accounts", "country", newCountry, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's avatar filename to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newAvatarName
	 */
	public void changeAvatarName(Account acc, String newAvatarName) {
		executeSimpleUpdate("Accounts", "avatar_file", newAvatarName,
				"username", acc.getUsername());
	}

	/**
	 * Changes user account's steam id to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newSteamID
	 */
	public void changeSteamID(Account acc, String newSteamID) {
		executeSimpleUpdate("Accounts", "steam_id", newSteamID, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's PlayStation Netowkr ID to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newPSNID
	 */
	public void changePlayStationNetworkID(Account acc, String newPSNID) {
		executeSimpleUpdate("Accounts", "psn_id", newPSNID, "username",
				acc.getUsername());
	}

	/**
	 * Changes user account's Xbox Live Gamertag to the new one.
	 * 
	 * @param acc
	 *            Account model.
	 * @param newXboxLiveGamertag
	 */
	public void changeXboxLiveGamertag(Account acc, String newXboxLiveGamertag) {
		executeSimpleUpdate("Accounts", "xbox_live_gamertag",
				newXboxLiveGamertag, "username", acc.getUsername());
	}

	/**
	 * Returns the List of Entry objects - all gameplay entries associated with
	 * the account.
	 * 
	 * @param acc
	 *            Account model.
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of number of elements on single page.
	 * @return The list of Entry objects within the given range.
	 */
	public List<Entry> getEntries(Account acc, int page, int limit) {
		List<Entry> entries = new ArrayList<Entry>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT * FROM Entries ");
			builder.append("WHERE username = ? ");
			builder.append("ORDER BY submission_date DESC ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setString(1, acc.getUsername());
			statement.setInt(2, (page - 1) * limit);
			statement.setInt(3, limit);
			ResultSet rs = statement.executeQuery();
			// building Entry objects
			while (rs.next()) {
				Integer id = rs.getInt(1);
				String username = acc.getUsername();
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
	 * Returns number of entries associated with the given account.
	 * 
	 * @param acc
	 *            Account model.
	 * @return The number of entries associated with the given account.s
	 */
	public int getEntriesQuantity(Account acc) {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT COUNT(id) ");
			builder.append("FROM Entries WHERE username = ?");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setString(1, acc.getUsername());
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
	 * Returns the List of Review objects - all reviews associated with the
	 * account.
	 * 
	 * @param acc
	 *            Account model.
	 * @param page
	 *            The page user is on.
	 * @param limit
	 *            The limit of number of elements on single page.
	 * @return The list of Review objects within the given range.
	 */
	public List<Review> getReviews(Account acc, int page, int limit) {
		List<Review> reviews = new ArrayList<Review>();
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT * FROM Reviews ");
			builder.append("WHERE username = ? ");
			builder.append("ORDER BY review_date DESC ");
			builder.append("LIMIT ?, ? ;");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setString(1, acc.getUsername());
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
	 * Returns the number of reviews left by user.
	 * 
	 * @param acc
	 *            Account model.
	 * @return The number of reviews left by user.
	 */
	public int getReviewsQuantity(Account acc) {
		int result = 0;
		try {
			Connection con = dataSource.getConnection();
			StringBuilder builder = new StringBuilder("SELECT COUNT(game_id) ");
			builder.append("FROM Reviews WHERE username = ?");
			PreparedStatement statement = con.prepareStatement(builder
					.toString());
			statement.setString(1, acc.getUsername());
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
	 * Permanently deletes account.
	 * 
	 * @param acc
	 *            Account model.
	 */
	public void removeAccount(Account acc) {
		executeSimpleDelete("Accounts", "username", acc.getUsername());
	}

}
