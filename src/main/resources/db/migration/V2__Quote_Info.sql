-- auto-generated definition
create table if not exists quote_info
(
    symbol              varchar(512)
        references company,
    company_name        varchar(1024),
    volume              bigint,
    previous_volume     bigint,
    latest_price        numeric
);

