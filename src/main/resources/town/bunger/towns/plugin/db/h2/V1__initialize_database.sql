create table "${tablePrefix}resident"
(
    "id"          UUID                             not null,
    "name"        CHARACTER VARYING                not null,
    "created"     TIMESTAMP default LOCALTIMESTAMP not null,
    "last_joined" TIMESTAMP default NULL,
    "town_id"     INTEGER   default NULL,
    "metadata"    JSON      default JSON '{}'      not null,
    constraint "resident_pk"
        primary key ("id")
);

create table "${tablePrefix}town"
(
    "id"          INTEGER auto_increment,
    "name"        CHARACTER VARYING                  not null
        constraint "town_uk_name"
            unique,
    "owner_id"    UUID                               not null,
    "created"     TIMESTAMP default LOCALTIMESTAMP   not null,
    "open"        BOOLEAN   default FALSE            not null,
    "public"      BOOLEAN   default FALSE            not null,
    "metadata"    JSON      default '{}'             not null,
    constraint "town_pk"
        primary key ("id"),
    constraint "town_resident_id_fk"
        foreign key ("owner_id") references "${tablePrefix}resident"
);

alter table "${tablePrefix}resident"
    add constraint "resident_town_id_fk"
        foreign key ("town_id") references "${tablePrefix}town"
            on delete set null;

