package gh.datamodel;

import java.sql.Date;

public class Account {
	public static final String ATTRIBUTE_NAME = "account";
	private String username; // required
	private String hashedPassword; // required
	private String email; // required
	private String nickname; // optional
	private String firstName; // optional
	private String lastName; // optional
	private Date birthdate; // optional
	private String gender; // optional
	private String country; // optional
	private String avatarName; // optional
	private String steam_id; // optional
	private String psn_id; // optional
	private String xbox_live_gamertag; // optional

	/*
	 * Private constructor. Returns new Account object.
	 */
	private Account(String username, String hashedPassword, String email,
			String nickname, String firstName, String lastName, Date birthdate,
			String gender, String country, String avatarName, String steam_id,
			String psn_id, String xbox_live_gamertag) {
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.email = email;
		this.nickname = nickname;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.gender = gender;
		this.country = country;
		this.avatarName = avatarName;
		this.steam_id = steam_id;
		this.psn_id = psn_id;
		this.xbox_live_gamertag = xbox_live_gamertag;
	}

	/*
	 * Private constructor required for AccountBuilder. Takes AccountBuilder
	 * object as a parameter. Returns new Account object.
	 */
	private Account(AccountBuilder builder) {
		this(builder.username, builder.hashedPassword, builder.email,
				builder.nickname, builder.firstName, builder.lastName,
				builder.birthdate, builder.gender, builder.country,
				builder.avatarName, builder.steam_id, builder.psn_id,
				builder.xbox_live_gamertag);
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
	 * Returns account's hashed password.
	 * 
	 * @return Hashed password.
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}

	/**
	 * Returns account's E-mail address.
	 * 
	 * @return E-mail address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns user's nickname.
	 * 
	 * @return String representing user's nickname.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Returns user's first name.
	 * 
	 * @return String representing user's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Returns user's last name.
	 * 
	 * @return String representing user's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Returns user's birth date.
	 * 
	 * @return Date object representing user's birth date.
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * Returns user's gender.
	 * 
	 * @return String representing user's gender.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Returns user's country of residence.
	 * 
	 * @return String representing user's country of residence.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Returns the filename of the user's avatar.
	 * 
	 * @return String representing the filename of the user's avatar.
	 */
	public String getAvatarFilename() {
		return avatarName;
	}

	/**
	 * Returns the Steam ID associated with the account.
	 * 
	 * @return String representing the Steam ID associated with the account.
	 */
	public String getSteamID() {
		return steam_id;
	}

	/**
	 * Returns the PlayStationNetwork ID associated with the account.
	 * 
	 * @return String representing the PlayStationNetwork ID associated with the
	 *         account.
	 */
	public String getPlayStationNetworkID() {
		return psn_id;
	}

	/**
	 * Returns the Xbox Live Gamertag (ID) associated with the account.
	 * 
	 * @return String representing the Xbox Live Gamertag (ID) associated with
	 *         the account.
	 */
	public String getXboxLiveGamertag() {
		return xbox_live_gamertag;
	}

	/**
	 * Returns Account object's String representation in format "Username:
	 * <code>username</code>; Hashed Password: <code>hashed password</code>;
	 * E-mail: <code>e-mail</code>; Nickname: <code>nickname</code>; First Name:
	 * <code>First Name</code>"; Last Name: <code>lastName</code>; Birthdate:
	 * <code>birthdate</code>; Gender: <code>gender</code>; Country:
	 * <code>Country</code>; Avatar: <code>avatarName</code>; Steam ID:
	 * <code>steam_id</code>; PSN ID: <code>psn_id</code>; Xbox Live Gamertag:
	 * <code>xbox_live_gamertag</code>;
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Username: " + username + "; ");
		result.append("Hashed Password: " + hashedPassword + "; ");
		result.append("E-mail: " + email + "; ");
		result.append("Nickname: " + nickname + "; ");
		result.append("First Name: " + firstName + "; ");
		result.append("Last Name: " + lastName + "; ");
		result.append("Birthdate: " + birthdate + "; ");
		result.append("Gender: " + gender + "; ");
		result.append("Country: " + country + "; ");
		result.append("Avatar: " + avatarName + "; ");
		result.append("Steam ID: " + steam_id + "; ");
		result.append("PSN ID: " + psn_id + "; ");
		result.append("Xbox Live Gamertag: " + xbox_live_gamertag + ";");
		return result.toString();
	}

	/**
	 * Using Builder pattern as the Account class hold many optional parameters.
	 */
	public static class AccountBuilder {
		private String username;
		private String hashedPassword;
		private String email;
		private String nickname;
		private String firstName;
		private String lastName;
		private Date birthdate;
		private String gender;
		private String country;
		private String avatarName;
		private String steam_id;
		private String psn_id;
		private String xbox_live_gamertag;

		/**
		 * Constructs new AccountBuilder object representing a registered or
		 * non-registered person's credentials. None of the parameters should be
		 * null pointers.
		 * 
		 * @param username
		 *            A unique user (account) identifier (ID).
		 * @param hashedPassword
		 *            Hashed password.
		 * @param email
		 *            E-mail address.
		 */
		public AccountBuilder(String username, String hashedPassword,
				String email) {
			this.username = username;
			this.hashedPassword = hashedPassword;
			this.email = email;
		}

		/**
		 * Adds user's nickname.
		 * 
		 * @param nickname
		 *            String representing user's nickname.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}

		/**
		 * Adds user's first name.
		 * 
		 * @param firstName
		 *            String representing user's first name.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		/**
		 * Adds user's last name.
		 * 
		 * @param lastName
		 *            String representing user's last name.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		/**
		 * Adds user's birth date.
		 * 
		 * @param birthdate
		 *            Date object representing user's birth date.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder birthdate(Date birthdate) {
			this.birthdate = birthdate;
			return this;
		}

		/**
		 * Adds user's gender.
		 * 
		 * @param gender
		 *            String representing user's gender.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder gender(String gender) {
			this.gender = gender;
			return this;
		}

		/**
		 * Adds user's country of residence.
		 * 
		 * @param country
		 *            String representing user's country of residence.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder country(String country) {
			this.country = country;
			return this;
		}

		/**
		 * Adds the filename of the user's avatar.
		 * 
		 * @param avatarName
		 *            String representing the filename of the user's avatar.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder avatarName(String avatarName) {
			this.avatarName = avatarName;
			return this;
		}

		/**
		 * Adds the Steam ID.
		 * 
		 * @param steam_id
		 *            String representing the Steam ID.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder steamID(String steam_id) {
			this.steam_id = steam_id;
			return this;
		}

		/**
		 * Adds the PlayStationNetwork ID.
		 * 
		 * @param psn_id
		 *            String representing the PlayStationNetwork ID.
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder playStationNetworkID(String psn_id) {
			this.psn_id = psn_id;
			return this;
		}

		/**
		 * Adds the Xbox Live Gamertag (ID).
		 * 
		 * @param xbox_live_gamertag
		 *            String representing the Xbox Live Gamertag (ID).
		 * @return <code>this</code> (AccountBuilder) object.
		 */
		public AccountBuilder xboxLiveGamertag(String xbox_live_gamertag) {
			this.xbox_live_gamertag = xbox_live_gamertag;
			return this;
		}

		/**
		 * Returns new Account object.
		 * 
		 * @return Account object based on accumulated information.
		 */
		public Account build() {
			return new Account(this);
		}

	}

}
