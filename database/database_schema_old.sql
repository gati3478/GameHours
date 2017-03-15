DROP DATABASE IF EXISTS gamehours_db;
CREATE DATABASE gamehours_db;
USE gamehours_db;

CREATE TABLE IF NOT EXISTS Games (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
	game_name CHAR(64) NOT NULL,
	image_file VARCHAR(128),
	developer_s TEXT,
	publisher_s TEXT,
	release_date DATE,
	short_description TEXT CHARACTER SET utf8
);

CREATE TABLE IF NOT EXISTS Genres (
	game_id INT,
	genre CHAR(32),
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Platforms (
	game_id INT,
	platform CHAR(32),
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GameImages (
	game_id INT,
	screenshot_file VARCHAR(128),
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS VideoLinks (
	game_id INT,
	video_link VARCHAR(128),
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Accounts (
	username CHAR(64) NOT NULL PRIMARY KEY UNIQUE,
	hashed_password CHAR(64) NOT NULL,
	email VARCHAR(254) NOT NULL,
	nickname NCHAR(32),
	first_name NCHAR(32),
	last_name NCHAR(32),
	birthdate DATE,
	gender CHAR(8),
	country CHAR(32),
	avatar_file VARCHAR(128),
	steam_id CHAR(32),
	psn_id CHAR(32),
	xbox_live_gamertag CHAR(32)
);

CREATE TABLE IF NOT EXISTS Admins (
	username CHAR(64),
	FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BannedAccounts (
	username CHAR(64),
	FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Reviews (
	username CHAR(64),
	game_id INT,
	rating INT,
	review_text TEXT CHARACTER SET utf8,
	review_date DATETIME,
	FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE CASCADE,
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Entries (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username CHAR(64),
	game_id INT,
	main_gameplay_time INT,
	extra_gameplay_time INT,
	complete_gameplay_time INT,
	platform CHAR(32),
	submission_date DATETIME,
	FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE SET NULL,
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

-- Statistics are viewed a lot more than being updated, so it was decided to store pre-computed answers in table and update them only when a new Entry is added to the database
CREATE TABLE IF NOT EXISTS GameplayStats (
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
	FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

-- Creates empty statistics row for a newly added game in GameplayStats table
delimiter |

CREATE TRIGGER onAddGame AFTER INSERT ON Games
	FOR EACH ROW
	BEGIN
		INSERT INTO GameplayStats(game_id) VALUES(NEW.id);
	END;

|
delimiter ;

-- The following three triggers update single row in GameplayStats table when the row in Entries or Reviews is affected either by insert, update or delete
delimiter |

CREATE TRIGGER update_GameplayStats_on_insert AFTER INSERT ON Entries
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_GameplayStatsRating_on_insert AFTER INSERT ON Reviews
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_rating = (SELECT AVG(rating) FROM Reviews WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_GameplayStats_on_update AFTER UPDATE ON Entries
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM Entries WHERE game_id = NEW.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM Entries WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_GameplayStatsRating_on_update AFTER UPDATE ON Reviews
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_rating = (SELECT AVG(rating) FROM Reviews WHERE game_id = NEW.game_id)
			WHERE game_id = NEW.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_GameplayStats_on_delete AFTER DELETE ON Entries
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_main = (SELECT AVG(main_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			average_extra = (SELECT AVG(extra_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			average_complete = (SELECT AVG(complete_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			min_main = (SELECT MIN(main_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			min_extra = (SELECT MIN(extra_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			min_complete = (SELECT MIN(complete_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			max_main = (SELECT MAX(main_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			max_extra = (SELECT MAX(extra_gameplay_time) FROM Entries WHERE game_id = OLD.game_id),
			max_complete =  (SELECT MAX(complete_gameplay_time) FROM Entries WHERE game_id = OLD.game_id)
			WHERE game_id = OLD.game_id;
END;

|
delimiter ;

delimiter |

CREATE TRIGGER update_GameplayStatsRating_on_delete AFTER DELETE ON Reviews
FOR EACH ROW
BEGIN
	UPDATE GameplayStats
		SET average_rating = (SELECT AVG(rating) FROM Reviews WHERE game_id = OLD.game_id)
			WHERE game_id = OLD.game_id;
END;

|
delimiter ;
