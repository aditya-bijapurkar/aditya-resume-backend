ALTER TABLE meet_schedule
ADD COLUMN meet_password TEXT;

INSERT INTO user_profile (id, firstname, lastname, email_id, created_at)
VALUES (UUID_GENERATE_V4(), 'Aditya', 'Admin', 'adityabijapurkar@gmail.com', NOW());