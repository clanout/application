SELECT (first_name || ' ' || last_name) AS name
FROM users
WHERE user_id = ?