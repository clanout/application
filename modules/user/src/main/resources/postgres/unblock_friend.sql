INSERT INTO friends
VALUES (?, ?);

DELETE FROM blocked_friends
WHERE blocking_user = ?
      AND blocked_user = ?;