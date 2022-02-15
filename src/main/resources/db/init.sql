create table exchange_rate_entity (id bigint generated by default as identity, base_currency varchar(255), date date, rate decimal(19,2), target_currency varchar(255), updated_at timestamp, primary key (id));
create table spread_entity (id bigint generated by default as identity, currency varchar(255), is_default boolean not null, spread decimal(19,2), primary key (id));
create table request_counter_entity (id bigint generated by default as identity, counter bigint, currency_from varchar(255), currency_to varchar(255), date date, primary key (id));

insert into spread_entity (currency, is_default, spread) values ( 'USD', false, 0 );
insert into spread_entity (currency, is_default, spread) values ( 'JPY', false, 3.25 );
insert into spread_entity (currency, is_default, spread) values ( 'HKD', false, 3.25 );
insert into spread_entity (currency, is_default, spread) values ( 'KRW', false, 3.25 );
insert into spread_entity (currency, is_default, spread) values ( 'MYR', false, 4.5 );
insert into spread_entity (currency, is_default, spread) values ( 'INR', false, 4.5 );
insert into spread_entity (currency, is_default, spread) values ( 'MXN', false, 4.5 );
insert into spread_entity (currency, is_default, spread) values ( 'RUB', false, 6.0 );
insert into spread_entity (currency, is_default, spread) values ( 'CNY', false, 6.0 );
insert into spread_entity (currency, is_default, spread) values ( 'MXN', false, 6.0 );
insert into spread_entity (currency, is_default, spread) values ( 'DEFAULT', true, 2.75 );