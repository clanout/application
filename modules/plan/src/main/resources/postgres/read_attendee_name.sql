SELECT (first_name || ' ' || last_name) AS attendee_name
FROM users
WHERE user_id = ?