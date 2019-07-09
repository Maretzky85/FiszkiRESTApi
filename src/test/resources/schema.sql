create table users
(
	id serial not null
		constraint users_pkey
			primary key,
	name varchar(50) not null
		constraint users_name_key
			unique,
	email varchar(255) not null
		constraint users_email_key
			unique,
	password varchar(255) not null
);


create table tag
(
	id serial not null
		constraint tag_pkey
			primary key,
	tag varchar(50)
);


create table questions
(
	id serial not null
		constraint questions_pkey
			primary key,
	title varchar(255),
	question text,
	user_id varchar(255),
	accepted boolean default false
);


create table answers
(
	id serial not null
		constraint answers_pkey
			primary key,
	answer text,
	question_id integer
		constraint questions_id_fkey
			references questions
				on delete cascade,
	user_id integer
		constraint answers_users_id_fk
			references users
);


create table questions_tag
(
	question_id integer not null
		constraint questions_tag_question_id_fkey
			references questions
				on update cascade on delete cascade,
	tag_id integer not null
		constraint questions_tag_tag_id_fkey
			references tag
				on update cascade on delete cascade,
	constraint question_tag_pkey
		primary key (question_id, tag_id)
);


create table roles
(
	id serial not null
		constraint roles_pkey
			primary key,
	role varchar(255) not null
);

create table users_roles
(
	user_id integer not null
		constraint users_roles_user_id_fkey
			references users
				on update cascade on delete cascade,
	role_id integer not null
		constraint users_roles_role_id_fkey
			references roles
				on update cascade,
	constraint users_roles_pkey
		primary key (user_id, role_id)
);


create table user_known_question
(
	user_id integer not null
		constraint user_known_question_user_id_fkey
			references users
				on update cascade on delete cascade,
	question_id integer not null
		constraint user_known_question_question_id_fkey
			references questions
				on update cascade,
	constraint user_known_question_id_pkey
		primary key (user_id, question_id)
);
