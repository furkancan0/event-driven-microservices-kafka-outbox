create sequence order_event_id_seq start with 1 increment by 50;

create table order_events
(
    id           bigint default nextval('order_event_id_seq') not null,
    event_id     text                                         not null unique,
    event_type   text                                         not null,
    payload      text                                         not null,
    is_delivered boolean                                      not null default '0',
    created_at   timestamp                                    not null,
    primary key (id)
);