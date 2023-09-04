create table cleverbank._user
(
    user_id          bigserial
        primary key,
    user_first_name  varchar(1000) not null,
    user_last_name   varchar(1000) not null,
    user_family_name varchar(1000) not null
);

alter table cleverbank._user
    owner to cleverbank;

create table cleverbank._currency
(
    currency_id   bigserial
        primary key,
    currency_name varchar(300) not null
        unique
);

alter table cleverbank._currency
    owner to cleverbank;

create table cleverbank.bank
(
    bank_id   bigserial
        primary key,
    bank_name varchar(1000) not null
        unique
);

alter table cleverbank.bank
    owner to cleverbank;

create table cleverbank.bank_account
(
    bank_account_id      bigserial
        primary key
        unique,
    bank_account_user_id bigint           not null
        constraint bank_account__user_user_id_fk
            references cleverbank._user,
    date_created         timestamp        not null,
    currency_id          bigint           not null
        constraint bank_account__currency_currency_id_fk
            references cleverbank._currency,
    balance              double precision not null,
    bank_id              bigint           not null
        constraint bank_account_bank_bank_id_fk
            references cleverbank.bank,
    accrual_date         timestamp        not null
);

alter table cleverbank.bank_account
    owner to cleverbank;

create table cleverbank._transaction_type
(
    transaction_type_id   bigserial
        primary key,
    transaction_type_name varchar(500) not null
        unique
);

alter table cleverbank._transaction_type
    owner to cleverbank;

create table cleverbank._transaction
(
    transaction_id       bigserial
        primary key,
    bank_account_id_to   bigint           not null
        constraint _transaction_bank_account_bank_account_id_fk
            references cleverbank.bank_account,
    transaction_type_id  bigint           not null
        constraint _transaction__transaction_type_transaction_type_id_fk
            references cleverbank._transaction_type
        constraint _transaction__transaction_type_transaction_type_id_fk_2
            references cleverbank._transaction_type,
    transaction_date     timestamp        not null,
    bank_account_id_from bigint           not null
        constraint _transaction_bank_account_bank_account_id_fk_2
            references cleverbank.bank_account,
    money                double precision not null
);

alter table cleverbank._transaction
    owner to cleverbank;

