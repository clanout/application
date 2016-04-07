DELETE FROM gcm_tokens
WHERE user_id = ?;

INSERT INTO gcm_tokens
VALUES(?, ?, NOW());