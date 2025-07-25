CREATE TABLE status (
	id UUID PRIMARY KEY,
	title TEXT
);

INSERT INTO status(id, title) VALUES
(uuid_generate_v4(), 'pending_approval'),
(uuid_generate_v4(), 'scheduled'),
(uuid_generate_v4(), 'declined');

CREATE TABLE meet_schedule (
	id UUID PRIMARY KEY,
	description TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	scheduled_at TIMESTAMP NOT NULL,
	meet_link TEXT NULL,
	meet_platform TEXT,
	meet_status_id UUID REFERENCES status(id)
);

CREATE TABLE user_profile (
	id UUID PRIMARY KEY,
	firstname TEXT,
    lastname TEXT,
	email_id TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE schedule_user_map (
	id UUID PRIMARY KEY,
	meet_id UUID REFERENCES meet_schedule(id),
	user_id UUID REFERENCES user_profile(id)
);