create table roles
(
    id   serial primary key,
    role varchar(50) not null unique
);

create table persons
(
    id       serial primary key,
    login    varchar(50)  not null,
    password varchar(100) not null,
    role_id  int          not null references roles (id)
);

create table rooms
(
    id   serial primary key,
    name varchar(50) not null
);

create table messages
(
    id        serial primary key,
    text      varchar(50) not null,
    created   timestamp   not null,
    person_id int         not null references persons (id),
    room_id   int         not null references rooms (id)
);

insert into roles (role)
values ('ROLE_USER');

insert into roles (role)
values ('ROLE_ADMIN');

insert into persons (login, password, role_id)
values ('admin', '$2a$10$4IGWhFKjyIqlzYmz4sMjHOeG6An2bKlnOLCvfgftEKEhXzm.4v25q',
        (select id from roles where role = 'ROLE_ADMIN'));
