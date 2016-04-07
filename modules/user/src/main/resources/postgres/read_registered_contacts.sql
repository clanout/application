SELECT
  a.user_id,
  a.name,
  a.location_zone,
  a.mobile_hash
FROM
  (SELECT
     user_id,
     (first_name || ' ' || last_name) AS name,
     location_zone,
     md5(mobile_number)               AS mobile_hash
   FROM users) a
WHERE a.mobile_hash = ANY (?)