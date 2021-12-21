-- auto-generated definition
create table if not exists company
(
    symbol      varchar(512) not null
        constraint table_name_pk
            primary key,
    name        varchar(1024),
    date        date,
    type        varchar(64),
    "iexid"     varchar(256),
    region      varchar(32),
    currency    varchar(8),
    "isenabled" boolean      not null,
    figi        varchar(32),
    cik         varchar(32)
);

alter table company
    owner to bezgerts;