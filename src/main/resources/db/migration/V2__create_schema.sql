CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS status (
	id UUID PRIMARY KEY,
	title TEXT
);

INSERT INTO status(id, title) VALUES
(uuid_generate_v4(), 'pending_approval'),
(uuid_generate_v4(), 'scheduled'),
(uuid_generate_v4(), 'declined');

CREATE TABLE IF NOT EXISTS meet_schedule (
	id UUID PRIMARY KEY,
	description TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	scheduled_at TIMESTAMP NOT NULL,
	meet_platform TEXT,
	meet_link TEXT NULL,
	meet_password TEXT,
	meet_status_id UUID REFERENCES status(id),
	attendee_emails TEXT[]
);

CREATE INDEX IF NOT EXISTS idx_user_emails
ON meet_schedule
USING GIN (attendee_emails);