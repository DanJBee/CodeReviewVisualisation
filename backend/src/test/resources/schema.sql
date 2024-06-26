DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS pull_requests;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS projects;

create table projects
(
    id         int primary key auto_increment,
    owner      varchar(100) not null,
    repository varchar(100) not null,
    index projects_owner_index (owner),
    index projects_repository_index (repository)
);

create table authors
(
    author_id         varchar(100) primary key,
    author_avatar_url varchar(100) not null,
    type_name         varchar(100) not null,
    index author_id_index (author_id),
    index author_type_name_index (type_name)
);

create table pull_requests
(
    id         bigint primary key auto_increment,
    number     bigint    not null,
    project_id int,
    created_at timestamp not null,
    author_id  varchar(100),
    index pull_requests_author_id_index (author_id),
    index pull_requests_id_index (project_id),
    index pull_requests_number_index (number),
    foreign key (project_id) references projects (id),
    foreign key (author_id) references authors (author_id)
);

create table comments
(
    id           bigint primary key auto_increment,
    pull_request bigint,
    author_id    varchar(100) not null,
    created_at   timestamp    not null,
    index comments_author_id_index (author_id),
    index comments_number_index (pull_request),
    foreign key (pull_request) references pull_requests (id)
);

INSERT INTO authors
VALUES ('Ghost', 'https://avatars.githubusercontent.com/u/9919?s=200&v=4', 'User');