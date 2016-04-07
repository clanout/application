CREATE TABLE phone_invitations
(
  mobile_hash TEXT                     NOT NULL,
  plan_id     VARCHAR(128)             NOT NULL,
  user_id     VARCHAR(128) REFERENCES users (user_id),
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  PRIMARY KEY (mobile_hash, plan_id, user_id)
);

CREATE TABLE plan_suggestions
(
  category VARCHAR(20) NOT NULL,
  title    TEXT        NOT NULL,
  PRIMARY KEY (category, title)
);