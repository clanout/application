INSERT INTO blocked_friends
VALUES (?, ?);

DELETE FROM friends
WHERE (user_1 = ? AND user_2 = ?)
      OR (user_2 = ? AND user_1 = ?);