DROP DATABASE IF EXISTS gamehours_db;
-- Or we could use CREATE DATABASE gamehours_db IF NOT EXISTS;
CREATE DATABASE gamehours_db;
USE gamehours_db;

-- Table's each row contains game's mandatory data
CREATE TABLE IF NOT EXISTS game (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	game_name NCHAR(128) NOT NULL,
	image_filename VARCHAR(128),
	short_description TEXT CHARACTER SET utf8
);

-- Table's each row contains developer's information
CREATE TABLE IF NOT EXISTS developer (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	developer_name NCHAR(64)
);

-- Table's each row contains publisher's information
CREATE TABLE IF NOT EXISTS publisher (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	publisher_name NCHAR(64)
);

/* Table's each row represents genre. Once populated, this table is not meant
 * to be updated, as new genres hardly ever appear */
CREATE TABLE IF NOT EXISTS genre (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	genre_name CHAR(32) UNIQUE
);

/* Table's each row represents gaming platform. This table might be dinamycally
 * updated with new platforms as they will be released (available) */
CREATE TABLE IF NOT EXISTS platform (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	platform_name CHAR(32) UNIQUE
);

-- Junction table linking developers to games
CREATE TABLE IF NOT EXISTS game_developers (
	game_id INT,
	developer_id INT,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
	FOREIGN KEY (developer_id) REFERENCES developer(id) ON DELETE CASCADE
);

-- Junction table linking publishers to games
CREATE TABLE IF NOT EXISTS game_publishers (
	game_id INT,
	publisher_id INT,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
	FOREIGN KEY (publisher_id) REFERENCES publisher(id) ON DELETE CASCADE
);

-- Junction table linking genres to games
CREATE TABLE IF NOT EXISTS game_genres (
	game_id INT,
	genre_id INT,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
	FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

-- Junction table linking platforms to games along with release date
CREATE TABLE IF NOT EXISTS game_platforms_release_date (
	game_id INT,
	platform_id INT,
	release_date DATE,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
	FOREIGN KEY (platform_id) REFERENCES platform(id) ON DELETE CASCADE
);

-- Table containing all images (artworks and/or screenshots) associated with game
CREATE TABLE IF NOT EXISTS game_images (
	game_id INT,
	image_filename VARCHAR(128),
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

-- Table containing all video links associated with game
CREATE TABLE IF NOT EXISTS game_videolinks (
	game_id INT,
	video_link VARCHAR(128),
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

/* Table listing countries. This table is not meant to be updated as new
 * countries hardly ever appear. */
CREATE TABLE IF NOT EXISTS country (
	id VARCHAR(2) NOT NULL PRIMARY KEY UNIQUE,
	country_name VARCHAR(64)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- Table's each row contains user account information
CREATE TABLE IF NOT EXISTS account (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	username CHAR(64) NOT NULL UNIQUE,
	hashed_password CHAR(64) NOT NULL,
	email_address VARCHAR(254) NOT NULL,
	nickname NCHAR(32),
	first_name NCHAR(32),
	last_name NCHAR(32),
	birthdate DATE,
	gender CHAR(8),
	country_id VARCHAR(2),
	avatar_filename VARCHAR(128),
	steam_id CHAR(32),
	psn_id CHAR(32),
	xbox_live_gamertag CHAR(32),
	is_private BOOL,
	FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS follows (
);

-- Table listing administrators
CREATE TABLE IF NOT EXISTS admin (
	user_id INT,
	FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE CASCADE
);

-- Table listing banned user accounts
CREATE TABLE IF NOT EXISTS banned_account (
	user_id INT,
	FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE CASCADE
);

-- Table listing reviews per game
CREATE TABLE IF NOT EXISTS review (
	user_id INT,
	game_id INT,
	rating INT,
	review_text TEXT CHARACTER SET utf8,
	review_date DATETIME,
	FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE CASCADE,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

-- Table listing user submitted entries per game
CREATE TABLE IF NOT EXISTS entry (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT,
	game_id INT,
	main_gameplay_time INT,
	extra_gameplay_time INT,
	complete_gameplay_time INT,
	platform_id INT,
	submission_date DATETIME,
	FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE SET NULL,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
	FOREIGN KEY (platform_id) REFERENCES platform(id) ON DELETE SET NULL
);

-- Statistics are viewed a lot more than being updated, so it was decided to store pre-computed answers in table and update them only when a new entry is added to the database
CREATE TABLE IF NOT EXISTS gameplay_stats (
	game_id INT,
	average_rating INT,
	average_main INT,
	average_extra INT,
	average_complete INT,
	min_main INT,
	min_extra INT,
	min_complete INT,
	max_main INT,
	max_extra INT,
	max_complete INT,
	FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

-- Creates empty statistics row for a newly added game in gameplay_stats table
delimiter |

CREATE TRIGGER onAddGame AFTER INSERT ON game
	FOR EACH ROW
	BEGIN
		INSERT INTO gameplay_stats(game_id) VALUES(NEW.id);
	END;

|
delimiter ;

-- The following three triggers update single row in gameplay_stats table when the row in entry or review is affected either by insert, update or delete
delimiter |

CREATE TRIGGER update_gameplay_stats_on_insert AFTER INSERT ON entry
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_gameplay_statsRating_on_insert AFTER INSERT ON review
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_rating = (SELECT AVG(rating) FROM review WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_gameplay_stats_on_update AFTER UPDATE ON entry
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM entry WHERE game_id = NEW.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM entry WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_gameplay_statsRating_on_update AFTER UPDATE ON review
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_rating = (SELECT AVG(rating) FROM review WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_gameplay_stats_on_delete AFTER DELETE ON entry
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM entry WHERE game_id = OLD.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM entry WHERE game_id = OLD.game_id)
			WHERE game_id = OLD.game_id;
END;

|
delimiter ;

-- Updates game's average rating if certain review was deleted

delimiter |

CREATE TRIGGER update_gameplay_statsRating_on_delete AFTER DELETE ON review
FOR EACH ROW
BEGIN
	UPDATE gameplay_stats
		SET average_rating = (SELECT AVG(rating) FROM review WHERE game_id = OLD.game_id)
			WHERE game_id = OLD.game_id;
END;

|
delimiter ;

-- Populating genre table with all possible general genre names
INSERT INTO genre (genre_name) VALUES ("Action");
INSERT INTO genre (genre_name) VALUES ("Adventure");
INSERT INTO genre (genre_name) VALUES ("Strategy");
INSERT INTO genre (genre_name) VALUES ("RPG");
INSERT INTO genre (genre_name) VALUES ("Indie");
INSERT INTO genre (genre_name) VALUES ("Massively Multiplayer");
INSERT INTO genre (genre_name) VALUES ("Casual");
INSERT INTO genre (genre_name) VALUES ("Simulation");
INSERT INTO genre (genre_name) VALUES ("Racing");
INSERT INTO genre (genre_name) VALUES ("Sports");

-- Populating platform table with the most common platforms available to date
INSERT INTO platform (platform_name) VALUES ("PC/Windows");
INSERT INTO platform (platform_name) VALUES ("PC/Linux");
INSERT INTO platform (platform_name) VALUES ("PC/Mac");
INSERT INTO platform (platform_name) VALUES ("PlayStation 4");
INSERT INTO platform (platform_name) VALUES ("PlayStation 3");
INSERT INTO platform (platform_name) VALUES ("PlayStation 2");
INSERT INTO platform (platform_name) VALUES ("PlayStation");
INSERT INTO platform (platform_name) VALUES ("PlayStation Vita");
INSERT INTO platform (platform_name) VALUES ("PSP");
INSERT INTO platform (platform_name) VALUES ("Xbox One");
INSERT INTO platform (platform_name) VALUES ("Xbox 360");
INSERT INTO platform (platform_name) VALUES ("Xbox");
INSERT INTO platform (platform_name) VALUES ("Wii");
INSERT INTO platform (platform_name) VALUES ("Wii U");
INSERT INTO platform (platform_name) VALUES ("Super Nintendo");
INSERT INTO platform (platform_name) VALUES ("iOS");
INSERT INTO platform (platform_name) VALUES ("Android");
INSERT INTO platform (platform_name) VALUES ("Windows Phone");
INSERT INTO platform (platform_name) VALUES ("3DO");
INSERT INTO platform (platform_name) VALUES ("Amiga");
INSERT INTO platform (platform_name) VALUES ("Amstrad CPC");
INSERT INTO platform (platform_name) VALUES ("Apple II");
INSERT INTO platform (platform_name) VALUES ("Arcade");
INSERT INTO platform (platform_name) VALUES ("Atari 2600");
INSERT INTO platform (platform_name) VALUES ("Atari 5200");
INSERT INTO platform (platform_name) VALUES ("Atari 7800");
INSERT INTO platform (platform_name) VALUES ("Atari Jaguar");
INSERT INTO platform (platform_name) VALUES ("Atari Jaguar CD");
INSERT INTO platform (platform_name) VALUES ("Atari Lynx");
INSERT INTO platform (platform_name) VALUES ("Atari ST");
INSERT INTO platform (platform_name) VALUES ("Browser");
INSERT INTO platform (platform_name) VALUES ("Commodore 64");
INSERT INTO platform (platform_name) VALUES ("Dreamcast");
INSERT INTO platform (platform_name) VALUES ("Game & Watch");
INSERT INTO platform (platform_name) VALUES ("Game Boy");
INSERT INTO platform (platform_name) VALUES ("Game Boy Advance");
INSERT INTO platform (platform_name) VALUES ("Game Boy Color");
INSERT INTO platform (platform_name) VALUES ("Mobile");
INSERT INTO platform (platform_name) VALUES ("MSX");
INSERT INTO platform (platform_name) VALUES ("N-Gage");
INSERT INTO platform (platform_name) VALUES ("Neo Geo");
INSERT INTO platform (platform_name) VALUES ("Neo Geo Pocket");
INSERT INTO platform (platform_name) VALUES ("Neo Geo Pocket Color");
INSERT INTO platform (platform_name) VALUES ("NES");
INSERT INTO platform (platform_name) VALUES ("Nintendo 3DS");
INSERT INTO platform (platform_name) VALUES ("Nintendo 64");
INSERT INTO platform (platform_name) VALUES ("Nintendo DS");
INSERT INTO platform (platform_name) VALUES ("Nintendo GameCube");
INSERT INTO platform (platform_name) VALUES ("OnLive");
INSERT INTO platform (platform_name) VALUES ("Ouya");
INSERT INTO platform (platform_name) VALUES ("Philips CD-i");
INSERT INTO platform (platform_name) VALUES ("Sega 32X");
INSERT INTO platform (platform_name) VALUES ("Sega CD");
INSERT INTO platform (platform_name) VALUES ("Sega Game Gear");
INSERT INTO platform (platform_name) VALUES ("Sega Master System");
INSERT INTO platform (platform_name) VALUES ("Sega Mega Drive/Genesis");
INSERT INTO platform (platform_name) VALUES ("Sega Saturn");
INSERT INTO platform (platform_name) VALUES ("TurboGrafx-16");
INSERT INTO platform (platform_name) VALUES ("TurboGrafx-CD");
INSERT INTO platform (platform_name) VALUES ("Virtual Boy");
INSERT INTO platform (platform_name) VALUES ("ZX Spectrum");

-- Populating country table with countries' data
INSERT INTO country VALUES ("GE", "Georgia");
INSERT INTO country VALUES ("AF", "Afghanistan");
INSERT INTO country VALUES ("AL", "Albania");
INSERT INTO country VALUES ("DZ", "Algeria");
INSERT INTO country VALUES ("AS", "American Samoa");
INSERT INTO country VALUES ("AD", "Andorra");
INSERT INTO country VALUES ("AO", "Angola");
INSERT INTO country VALUES ("AI", "Anguilla");
INSERT INTO country VALUES ("AQ", "Antarctica");
INSERT INTO country VALUES ("AG", "Antigua and Barbuda");
INSERT INTO country VALUES ("AR", "Argentina");
INSERT INTO country VALUES ("AM", "Armenia");
INSERT INTO country VALUES ("AW", "Aruba");
INSERT INTO country VALUES ("AU", "Australia");
INSERT INTO country VALUES ("AT", "Austria");
INSERT INTO country VALUES ("AZ", "Azerbaijan");
INSERT INTO country VALUES ("BS", "Bahamas");
INSERT INTO country VALUES ("BH", "Bahrain");
INSERT INTO country VALUES ("BD", "Bangladesh");
INSERT INTO country VALUES ("BB", "Barbados");
INSERT INTO country VALUES ("BY", "Belarus");
INSERT INTO country VALUES ("BE", "Belgium");
INSERT INTO country VALUES ("BZ", "Belize");
INSERT INTO country VALUES ("BJ", "Benin");
INSERT INTO country VALUES ("BM", "Bermuda");
INSERT INTO country VALUES ("BT", "Bhutan");
INSERT INTO country VALUES ("BO", "Bolivia");
INSERT INTO country VALUES ("BA", "Bosnia and Herzegovina");
INSERT INTO country VALUES ("BW", "Botswana");
INSERT INTO country VALUES ("BV", "Bouvet Island");
INSERT INTO country VALUES ("BR", "Brazil");
INSERT INTO country VALUES ("BQ", "British Antarctic Territory");
INSERT INTO country VALUES ("IO", "British Indian Ocean Territory");
INSERT INTO country VALUES ("VG", "British Virgin Islands");
INSERT INTO country VALUES ("BN", "Brunei");
INSERT INTO country VALUES ("BG", "Bulgaria");
INSERT INTO country VALUES ("BF", "Burkina Faso");
INSERT INTO country VALUES ("BI", "Burundi");
INSERT INTO country VALUES ("KH", "Cambodia");
INSERT INTO country VALUES ("CM", "Cameroon");
INSERT INTO country VALUES ("CA", "Canada");
INSERT INTO country VALUES ("CT", "Canton and Enderbury Islands");
INSERT INTO country VALUES ("CV", "Cape Verde");
INSERT INTO country VALUES ("KY", "Cayman Islands");
INSERT INTO country VALUES ("CF", "Central African Republic");
INSERT INTO country VALUES ("TD", "Chad");
INSERT INTO country VALUES ("CL", "Chile");
INSERT INTO country VALUES ("CN", "China");
INSERT INTO country VALUES ("CX", "Christmas Island");
INSERT INTO country VALUES ("CC", "Cocos [Keeling] Islands");
INSERT INTO country VALUES ("CO", "Colombia");
INSERT INTO country VALUES ("KM", "Comoros");
INSERT INTO country VALUES ("CG", "Congo - Brazzaville");
INSERT INTO country VALUES ("CD", "Congo - Kinshasa");
INSERT INTO country VALUES ("CK", "Cook Islands");
INSERT INTO country VALUES ("CR", "Costa Rica");
INSERT INTO country VALUES ("HR", "Croatia");
INSERT INTO country VALUES ("CU", "Cuba");
INSERT INTO country VALUES ("CY", "Cyprus");
INSERT INTO country VALUES ("CZ", "Czech Republic");
INSERT INTO country VALUES ("CI", "Côte d’Ivoire");
INSERT INTO country VALUES ("DK", "Denmark");
INSERT INTO country VALUES ("DJ", "Djibouti");
INSERT INTO country VALUES ("DM", "Dominica");
INSERT INTO country VALUES ("DO", "Dominican Republic");
INSERT INTO country VALUES ("NQ", "Dronning Maud Land");
INSERT INTO country VALUES ("DD", "East Germany");
INSERT INTO country VALUES ("EC", "Ecuador");
INSERT INTO country VALUES ("EG", "Egypt");
INSERT INTO country VALUES ("SV", "El Salvador");
INSERT INTO country VALUES ("GQ", "Equatorial Guinea");
INSERT INTO country VALUES ("ER", "Eritrea");
INSERT INTO country VALUES ("EE", "Estonia");
INSERT INTO country VALUES ("ET", "Ethiopia");
INSERT INTO country VALUES ("FK", "Falkland Islands");
INSERT INTO country VALUES ("FO", "Faroe Islands");
INSERT INTO country VALUES ("FJ", "Fiji");
INSERT INTO country VALUES ("FI", "Finland");
INSERT INTO country VALUES ("FR", "France");
INSERT INTO country VALUES ("GF", "French Guiana");
INSERT INTO country VALUES ("PF", "French Polynesia");
INSERT INTO country VALUES ("TF", "French Southern Territories");
INSERT INTO country VALUES ("FQ", "French Southern and Antarctic Territories");
INSERT INTO country VALUES ("GA", "Gabon");
INSERT INTO country VALUES ("GM", "Gambia");
INSERT INTO country VALUES ("DE", "Germany");
INSERT INTO country VALUES ("GH", "Ghana");
INSERT INTO country VALUES ("GI", "Gibraltar");
INSERT INTO country VALUES ("GR", "Greece");
INSERT INTO country VALUES ("GL", "Greenland");
INSERT INTO country VALUES ("GD", "Grenada");
INSERT INTO country VALUES ("GP", "Guadeloupe");
INSERT INTO country VALUES ("GU", "Guam");
INSERT INTO country VALUES ("GT", "Guatemala");
INSERT INTO country VALUES ("GG", "Guernsey");
INSERT INTO country VALUES ("GN", "Guinea");
INSERT INTO country VALUES ("GW", "Guinea-Bissau");
INSERT INTO country VALUES ("GY", "Guyana");
INSERT INTO country VALUES ("HT", "Haiti");
INSERT INTO country VALUES ("HM", "Heard Island and McDonald Islands");
INSERT INTO country VALUES ("HN", "Honduras");
INSERT INTO country VALUES ("HK", "Hong Kong SAR China");
INSERT INTO country VALUES ("HU", "Hungary");
INSERT INTO country VALUES ("IS", "Iceland");
INSERT INTO country VALUES ("IN", "India");
INSERT INTO country VALUES ("ID", "Indonesia");
INSERT INTO country VALUES ("IR", "Iran");
INSERT INTO country VALUES ("IQ", "Iraq");
INSERT INTO country VALUES ("IE", "Ireland");
INSERT INTO country VALUES ("IM", "Isle of Man");
INSERT INTO country VALUES ("IL", "Israel");
INSERT INTO country VALUES ("IT", "Italy");
INSERT INTO country VALUES ("JM", "Jamaica");
INSERT INTO country VALUES ("JP", "Japan");
INSERT INTO country VALUES ("JE", "Jersey");
INSERT INTO country VALUES ("JT", "Johnston Island");
INSERT INTO country VALUES ("JO", "Jordan");
INSERT INTO country VALUES ("KZ", "Kazakhstan");
INSERT INTO country VALUES ("KE", "Kenya");
INSERT INTO country VALUES ("KI", "Kiribati");
INSERT INTO country VALUES ("KW", "Kuwait");
INSERT INTO country VALUES ("KG", "Kyrgyzstan");
INSERT INTO country VALUES ("LA", "Laos");
INSERT INTO country VALUES ("LV", "Latvia");
INSERT INTO country VALUES ("LB", "Lebanon");
INSERT INTO country VALUES ("LS", "Lesotho");
INSERT INTO country VALUES ("LR", "Liberia");
INSERT INTO country VALUES ("LY", "Libya");
INSERT INTO country VALUES ("LI", "Liechtenstein");
INSERT INTO country VALUES ("LT", "Lithuania");
INSERT INTO country VALUES ("LU", "Luxembourg");
INSERT INTO country VALUES ("MO", "Macau SAR China");
INSERT INTO country VALUES ("MK", "Macedonia");
INSERT INTO country VALUES ("MG", "Madagascar");
INSERT INTO country VALUES ("MW", "Malawi");
INSERT INTO country VALUES ("MY", "Malaysia");
INSERT INTO country VALUES ("MV", "Maldives");
INSERT INTO country VALUES ("ML", "Mali");
INSERT INTO country VALUES ("MT", "Malta");
INSERT INTO country VALUES ("MH", "Marshall Islands");
INSERT INTO country VALUES ("MQ", "Martinique");
INSERT INTO country VALUES ("MR", "Mauritania");
INSERT INTO country VALUES ("MU", "Mauritius");
INSERT INTO country VALUES ("YT", "Mayotte");
INSERT INTO country VALUES ("FX", "Metropolitan France");
INSERT INTO country VALUES ("MX", "Mexico");
INSERT INTO country VALUES ("FM", "Micronesia");
INSERT INTO country VALUES ("MI", "Midway Islands");
INSERT INTO country VALUES ("MD", "Moldova");
INSERT INTO country VALUES ("MC", "Monaco");
INSERT INTO country VALUES ("MN", "Mongolia");
INSERT INTO country VALUES ("ME", "Montenegro");
INSERT INTO country VALUES ("MS", "Montserrat");
INSERT INTO country VALUES ("MA", "Morocco");
INSERT INTO country VALUES ("MZ", "Mozambique");
INSERT INTO country VALUES ("MM", "Myanmar [Burma]");
INSERT INTO country VALUES ("NA", "Namibia");
INSERT INTO country VALUES ("NR", "Nauru");
INSERT INTO country VALUES ("NP", "Nepal");
INSERT INTO country VALUES ("NL", "Netherlands");
INSERT INTO country VALUES ("AN", "Netherlands Antilles");
INSERT INTO country VALUES ("NT", "Neutral Zone");
INSERT INTO country VALUES ("NC", "New Caledonia");
INSERT INTO country VALUES ("NZ", "New Zealand");
INSERT INTO country VALUES ("NI", "Nicaragua");
INSERT INTO country VALUES ("NE", "Niger");
INSERT INTO country VALUES ("NG", "Nigeria");
INSERT INTO country VALUES ("NU", "Niue");
INSERT INTO country VALUES ("NF", "Norfolk Island");
INSERT INTO country VALUES ("KP", "North Korea");
INSERT INTO country VALUES ("VD", "North Vietnam");
INSERT INTO country VALUES ("MP", "Northern Mariana Islands");
INSERT INTO country VALUES ("NO", "Norway");
INSERT INTO country VALUES ("OM", "Oman");
INSERT INTO country VALUES ("PC", "Pacific Islands Trust Territory");
INSERT INTO country VALUES ("PK", "Pakistan");
INSERT INTO country VALUES ("PW", "Palau");
INSERT INTO country VALUES ("PS", "Palestinian Territories");
INSERT INTO country VALUES ("PA", "Panama");
INSERT INTO country VALUES ("PZ", "Panama Canal Zone");
INSERT INTO country VALUES ("PG", "Papua New Guinea");
INSERT INTO country VALUES ("PY", "Paraguay");
INSERT INTO country VALUES ("YD", "People\'s Democratic Republic of Yemen");
INSERT INTO country VALUES ("PE", "Peru");
INSERT INTO country VALUES ("PH", "Philippines");
INSERT INTO country VALUES ("PN", "Pitcairn Islands");
INSERT INTO country VALUES ("PL", "Poland");
INSERT INTO country VALUES ("PT", "Portugal");
INSERT INTO country VALUES ("PR", "Puerto Rico");
INSERT INTO country VALUES ("QA", "Qatar");
INSERT INTO country VALUES ("RO", "Romania");
INSERT INTO country VALUES ("RU", "Russia");
INSERT INTO country VALUES ("RW", "Rwanda");
INSERT INTO country VALUES ("RE", "Réunion");
INSERT INTO country VALUES ("BL", "Saint Barthélemy");
INSERT INTO country VALUES ("SH", "Saint Helena");
INSERT INTO country VALUES ("KN", "Saint Kitts and Nevis");
INSERT INTO country VALUES ("LC", "Saint Lucia");
INSERT INTO country VALUES ("MF", "Saint Martin");
INSERT INTO country VALUES ("PM", "Saint Pierre and Miquelon");
INSERT INTO country VALUES ("VC", "Saint Vincent and the Grenadines");
INSERT INTO country VALUES ("WS", "Samoa");
INSERT INTO country VALUES ("SM", "San Marino");
INSERT INTO country VALUES ("SA", "Saudi Arabia");
INSERT INTO country VALUES ("SN", "Senegal");
INSERT INTO country VALUES ("RS", "Serbia");
INSERT INTO country VALUES ("CS", "Serbia and Montenegro");
INSERT INTO country VALUES ("SC", "Seychelles");
INSERT INTO country VALUES ("SL", "Sierra Leone");
INSERT INTO country VALUES ("SG", "Singapore");
INSERT INTO country VALUES ("SK", "Slovakia");
INSERT INTO country VALUES ("SI", "Slovenia");
INSERT INTO country VALUES ("SB", "Solomon Islands");
INSERT INTO country VALUES ("SO", "Somalia");
INSERT INTO country VALUES ("ZA", "South Africa");
INSERT INTO country VALUES ("GS", "South Georgia and the South Sandwich Islands");
INSERT INTO country VALUES ("KR", "South Korea");
INSERT INTO country VALUES ("ES", "Spain");
INSERT INTO country VALUES ("LK", "Sri Lanka");
INSERT INTO country VALUES ("SD", "Sudan");
INSERT INTO country VALUES ("SR", "Suriname");
INSERT INTO country VALUES ("SJ", "Svalbard and Jan Mayen");
INSERT INTO country VALUES ("SZ", "Swaziland");
INSERT INTO country VALUES ("SE", "Sweden");
INSERT INTO country VALUES ("CH", "Switzerland");
INSERT INTO country VALUES ("SY", "Syria");
INSERT INTO country VALUES ("ST", "São Tomé and Príncipe");
INSERT INTO country VALUES ("TW", "Taiwan");
INSERT INTO country VALUES ("TJ", "Tajikistan");
INSERT INTO country VALUES ("TZ", "Tanzania");
INSERT INTO country VALUES ("TH", "Thailand");
INSERT INTO country VALUES ("TL", "Timor-Leste");
INSERT INTO country VALUES ("TG", "Togo");
INSERT INTO country VALUES ("TK", "Tokelau");
INSERT INTO country VALUES ("TO", "Tonga");
INSERT INTO country VALUES ("TT", "Trinidad and Tobago");
INSERT INTO country VALUES ("TN", "Tunisia");
INSERT INTO country VALUES ("TR", "Turkey");
INSERT INTO country VALUES ("TM", "Turkmenistan");
INSERT INTO country VALUES ("TC", "Turks and Caicos Islands");
INSERT INTO country VALUES ("TV", "Tuvalu");
INSERT INTO country VALUES ("UM", "U.S. Minor Outlying Islands");
INSERT INTO country VALUES ("PU", "U.S. Miscellaneous Pacific Islands");
INSERT INTO country VALUES ("VI", "U.S. Virgin Islands");
INSERT INTO country VALUES ("UG", "Uganda");
INSERT INTO country VALUES ("UA", "Ukraine");
INSERT INTO country VALUES ("SU", "Union of Soviet Socialist Republics");
INSERT INTO country VALUES ("AE", "United Arab Emirates");
INSERT INTO country VALUES ("GB", "United Kingdom");
INSERT INTO country VALUES ("US", "United States");
INSERT INTO country VALUES ("ZZ", "Unknown or Invalid Region");
INSERT INTO country VALUES ("UY", "Uruguay");
INSERT INTO country VALUES ("UZ", "Uzbekistan");
INSERT INTO country VALUES ("VU", "Vanuatu");
INSERT INTO country VALUES ("VA", "Vatican City");
INSERT INTO country VALUES ("VE", "Venezuela");
INSERT INTO country VALUES ("VN", "Vietnam");
INSERT INTO country VALUES ("WK", "Wake Island");
INSERT INTO country VALUES ("WF", "Wallis and Futuna");
INSERT INTO country VALUES ("EH", "Western Sahara");
INSERT INTO country VALUES ("YE", "Yemen");
INSERT INTO country VALUES ("ZM", "Zambia");
INSERT INTO country VALUES ("ZW", "Zimbabwe");
INSERT INTO country VALUES ("AX", "Åland Islands");