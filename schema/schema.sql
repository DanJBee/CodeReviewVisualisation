SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE comments;

DROP TABLE pull_requests;

DROP TABLE authors;

DROP TABLE projects;

SET FOREIGN_KEY_CHECKS = 1;

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

create table authors
(
    author_id         varchar(100) not null,
    author_avatar_url varchar(100) not null,
    type_name         varchar(100) not null
);

create index authors_author_id_index
    on authors (author_id);

create index authors_type_name_index
    on authors (type_name);

alter table authors
    add constraint authors_pk
        primary key (author_id);

create table pull_requests
(
    id         bigint,
    number     bigint       not null,
    project_id int          not null,
    created_at timestamp    not null,
    author_id  varchar(100) not null,
    constraint pull_requests_authors_author_id_fk
        foreign key (author_id) references authors (author_id),
    constraint pull_requests_projects_id_fk
        foreign key (project_id) references projects (id)
);

create index pull_requests_author_id_index
    on pull_requests (author_id);

create index pull_requests_number_index
    on pull_requests (number);

create index pull_requests_project_id_index
    on pull_requests (project_id);

alter table pull_requests
    add constraint pull_requests_pk
        primary key (id);

alter table pull_requests
    modify id bigint auto_increment;

create table comments
(
    id         bigint,
    number     bigint       not null,
    author_id  varchar(100) not null,
    created_at timestamp    not null,
    constraint comments_pull_requests_number_fk
        foreign key (number) references pull_requests (number)
);

create index comments_author_id_index
    on comments (author_id);

create index comments_number_index
    on comments (number);

alter table comments
    add constraint comments_pk
        primary key (id);

alter table comments
    modify id bigint auto_increment;

alter table pull_requests
    modify author_id varchar(100) null;

INSERT INTO authors
VALUES ('Ghost', 'https://example.com', 'User');