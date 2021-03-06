create table pcmr_articles
(
    id                  bigint       not null auto_increment,
    created             datetime(6),
    created_by          varchar(100),
    last_modified_by    varchar(100),
    updated             datetime(6),
    description         longtext     not null,
    details             longtext     not null,
    name                varchar(255) not null,
    price               integer      not null check (price>=0 AND price<=99999999),
    stock               integer      not null check (stock>=0 AND stock<=999999),
    primary key (id)
) engine=InnoDB;

create table pcmr_customers
(
    id                  bigint          not null auto_increment,
    created             datetime(6),
    created_by          varchar(100),
    last_modified_by    varchar(100),
    updated             datetime(6),
    email               varchar(100)    not null,
    first_name          varchar(50)     not null,
    last_name           varchar(50)     not null,
    primary key (id)
) engine=InnoDB;

alter table pcmr_customers
    add constraint UK_luo0qxa57p6pjyvvt3vhn5c43 unique (email);
