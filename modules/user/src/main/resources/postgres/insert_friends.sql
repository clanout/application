INSERT INTO friends
  SELECT
    ?         AS user_1,
    a.user_id AS user_2
  FROM users a
  WHERE a.user_id = ANY (?)