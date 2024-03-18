-- create database hero
--param1     with oparam1wner root;

create table biography
(
    id_biography varchar(200) not null
        constraint usuarios_pk
            primary key,
    full_name varchar(200) not null
);

create table biography_aliases
(
    id_biography varchar(200) not null
        constraint usuarios_pk
            primary key,
    full_name varchar(200) not null
);

create table biography_aliases
(
    id_biography varchar(200) not null
        constraint hero_biography_aliases_biography_fk
            references biography,
    aliases_name varchar(200) not null
);

create table hero
(
    id_hero varchar(200) not null
        constraint hero_pk
            primary key,
    id_biography varchar(200) not null
        constraint hero_biography_id_biography_fk
            references biography,
    name varchar(200) not null
);

create table hero_relatives
(
    id_hero varchar(200) not null
        constraint hero_relatives_id_hero_fk
                    references hero,
    relatives_name varchar(200) not null
);

create table hero_group_affiliation
(
    id_hero varchar(200) not null
        constraint hero_group_affiliation_id_hero_fk
            references hero,
    group_affiliation_name varchar(200) not null
);