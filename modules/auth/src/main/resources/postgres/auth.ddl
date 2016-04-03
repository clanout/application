DROP TABLE session_encryption_key;
DROP TABLE refresh_tokens;

CREATE TABLE session_encryption_key
(
  encryption_key TEXT PRIMARY KEY,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE refresh_tokens
(
  token      VARCHAR(128) PRIMARY KEY,
  user_id    VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);