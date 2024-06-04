create table projects
(
    id         int,
    owner      varchar(100) not null,
    repository varchar(100) not null
);

create index projects_id_index
    on projects (id);

create index projects_owner_index
    on projects (owner);

create index projects_repository_index
    on projects (repository);

alter table projects
    add constraint projects_pk
        primary key (id);

alter table projects
    modify id int auto_increment;

create table pull_requests
(
    id                int          null,
    number            bigint       not null,
    created_at        timestamp    not null,
    author_id         varchar(100) null,
    author_avatar_url varchar(100) null,
    type_name         varchar(100) null,
    constraint pull_requests_projects_id_fk
        foreign key (id) references projects (id)
);

create index pull_requests_author_avatar_url_index
    on pull_requests (author_avatar_url);

create index pull_requests_author_id_index
    on pull_requests (author_id);

create index pull_requests_id_index
    on pull_requests (id);

create index pull_requests_number_index
    on pull_requests (number);

create index pull_requests_type_name_index
    on pull_requests (type_name);

alter table pull_requests
    add constraint pull_requests_pk
        primary key (number);

create table comments
(
    number            bigint       not null,
    author_id         varchar(100) null,
    author_avatar_url varchar(100) null,
    type_name         varchar(100) null,
    created_at        timestamp    not null,
    constraint comments_pull_requests_number_fk
        foreign key (number) references pull_requests (number)
);

create index comments_author_avatar_url_index
    on comments (author_avatar_url);

create index comments_author_id_index
    on comments (author_id);

create index comments_number_index
    on comments (number);

create index comments_type_name_index
    on comments (type_name);