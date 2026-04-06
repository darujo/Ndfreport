ALTER TABLE users
    ADD column  code_email      VARCHAR(255),
    add column  email           VARCHAR(255) not null ,
    add column  email_new           VARCHAR(255) ,
    add column  send_code       timestamp,
    add column  recovery        boolean not null
;