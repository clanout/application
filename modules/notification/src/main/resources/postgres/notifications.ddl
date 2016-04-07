DROP TABLE gcm_tokens;

CREATE TABLE gcm_tokens
(
  gcm_token    TEXT PRIMARY KEY,
  user_id      VARCHAR(128) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
  UNIQUE (user_id)
);