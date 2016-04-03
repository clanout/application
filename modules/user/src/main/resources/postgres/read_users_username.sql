SELECT user_id
FROM users
WHERE username_type = ?
      AND username = ANY (?)