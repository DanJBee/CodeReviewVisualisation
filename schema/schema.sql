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
    number            bigint       auto_increment,
    project_id        int          foreign key (project_id) references projects (id),
    created_at        timestamp    not null,
    author_id         varchar(100) null,
    author_avatar_url varchar(100) null,
    type_name         varchar(100) null,
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
    id                bigint       auto_increment,
    number            bigint       foreign key (number) references pull_requests (number),
    author_id         varchar(100) null,
    author_avatar_url varchar(100) null,
    type_name         varchar(100) null,
    created_at        timestamp    not null,
);

create index comments_author_avatar_url_index
    on comments (author_avatar_url);

create index comments_author_id_index
    on comments (author_id);

create index comments_number_index
    on comments (number);

create index comments_type_name_index
    on comments (type_name);

alter table comments
    add constraint comments_pk
        primary key (id);

create table author
(
    id                bigint,
    number            bigint       foreign key (number) references pull_requests (number),
    author_id         varchar(100) foreign key (author_id) references author (author_id),
    author_avatar_url varchar(100) foreign key (author_avatar_url) references author (author_avatar_url),
    type_name         varchar(100) foreign key (type_name) references author (type_name),
);

create index author_author_avatar_url_index
    on author (author_avatar_url);

create index author_author_id_index
    on author (author_id);

create index author_id_index
    on author (id);

create index author_number_index
    on author (number);

create index author_type_name_index
    on author (type_name);

alter table author
    add constraint author_pk
        primary key (id);

alter table author
    modify id bigint auto_increment;