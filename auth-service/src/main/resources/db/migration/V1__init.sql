CREATE SEQUENCE IF NOT EXISTS user_ndfl.users_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE user_ndfl.users_id_seq
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS user_ndfl.users
(
    id bigint NOT NULL DEFAULT nextval('user_ndfl.users_id_seq'::regclass),
    nik_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    patronymic character varying(255) COLLATE pg_catalog."default",
    password_change boolean,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS user_ndfl.users
    OWNER to postgres;

ALTER SEQUENCE user_ndfl.users_id_seq
    OWNED BY user_ndfl.users.id;
