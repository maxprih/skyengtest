create table mail_item
(
    id               bigserial    not null,
    current_index    integer      not null,
    moving_to_index  integer,
    receiver_address varchar(255) not null,
    receiver_index   integer      not null,
    receiver_name    varchar(255) not null,
    start_index      integer      not null,
    status           smallint     not null check (status between 0 and 2),
    type             smallint     not null check (type between 0 and 3),
    primary key (id)
);
create table mail_item_event
(
    id           bigserial not null,
    event_date   timestamp(6),
    event_type   smallint check (event_type between 0 and 3),
    index        integer,
    mail_item_id bigint,
    primary key (id)
);
create table mail_item_movement
(
    id                bigserial not null,
    destination_index integer,
    movement_date     timestamp(6),
    source_index      integer,
    mail_item_id      bigint,
    primary key (id)
);
create table mail_office
(
    id      bigserial    not null,
    address varchar(255) not null,
    index   integer      not null,
    name    varchar(255) not null,
    primary key (id)
);
alter table if exists mail_item_event
    add constraint FK4m6lk8ypbdo99bswa8o9h2elc foreign key (mail_item_id) references mail_item;
alter table if exists mail_item_movement
    add constraint FKnh5ji5rnn5re6kn4ousqj0423 foreign key (mail_item_id) references mail_item;