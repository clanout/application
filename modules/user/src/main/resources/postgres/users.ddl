DROP TABLE friends;
DROP TABLE blocked_friends;
DROP TABLE users;

CREATE TABLE users
(
  user_id       VARCHAR(128) PRIMARY KEY,
  first_name    VARCHAR(32)                                       NOT NULL,
  last_name     VARCHAR(32)                                       NOT NULL,
  email         VARCHAR(32) UNIQUE,
  mobile_number VARCHAR(15) UNIQUE,
  gender        VARCHAR(10)                                       NOT NULL,
  username      VARCHAR(128)                                      NOT NULL,
  username_type VARCHAR(20)                                       NOT NULL,
  location_zone VARCHAR(20) REFERENCES location_zones (zone_code) NOT NULL,
  created_at    TIMESTAMP WITH TIME ZONE                          NOT NULL,
  UNIQUE (username, username_type)
);

CREATE TABLE friends
(
  user_1 VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  user_2 VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  PRIMARY KEY (user_1, user_2)
);

CREATE TABLE blocked_friends
(
  blocking_user VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  blocked_user  VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  PRIMARY KEY (blocking_user, blocked_user)
);