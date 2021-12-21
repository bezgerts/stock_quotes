-- auto-generated definition
create table if not exists company_info
(
    symbol           varchar(512)
        references company,
    "companyName"    varchar(1024),
    volume           bigint,
    "previousVolume" bigint,
    "latestPrice"    numeric
);

alter table company_info owner to bezgerts;

