DELETE FROM friends
WHERE (user_1 = ? AND user_2 = ANY (?))
      OR (user_2 = ? AND user_1 = ANY (?))