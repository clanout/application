SELECT
  friends.friend_id,
  (info.first_name || ' ' || info.last_name) AS name,
  info.location_zone,
  is_blocked
FROM
  (
    SELECT
      user_1 AS friend_id,
      FALSE  AS is_blocked
    FROM friends
    WHERE user_2 = ?
    UNION ALL
    SELECT
      user_2 AS friend_id,
      FALSE  AS is_blocked
    FROM friends
    WHERE user_1 = ?
    UNION ALL
    SELECT
      blocked_user AS friend_id,
      TRUE         AS is_blocked
    FROM blocked_friends
    WHERE blocking_user = ?
  ) friends,
  users info
WHERE friends.friend_id = info.user_id