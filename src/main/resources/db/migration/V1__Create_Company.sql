-- auto-generated definition
create table if not exists company
(
    symbol          varchar(512)        not null
        constraint table_name_pk
            primary key,
    name            varchar(1024),
    date            date,
    type            varchar(64),
    iex_id          varchar(256),
    region          varchar(32),
    currency        varchar(8),
    is_enabled      boolean      not null,
    figi            varchar(32),
    cik             varchar(32)
);