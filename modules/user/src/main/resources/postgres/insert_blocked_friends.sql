INSERT INTO blocked_friends
  SELECT
    ?         AS blocking_user,
    a.user_id AS blocked_user
  FROM users a
  WHERE a.user_id = ANY (?);