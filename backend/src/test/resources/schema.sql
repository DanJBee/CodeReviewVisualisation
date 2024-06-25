DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS pull_requests;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS projects;

create table projects
(
    id         int primary key auto_increment,
    owner      varchar(100) not null,
    repository varchar(100) not null
);

create table authors
(
    author_id         varchar(100) primary key,
    author_avatar_url varchar(100) not null,
    type_name         varchar(100) not null
);

create table pull_requests
(
    id         bigint primary key auto_increment,
    number     bigint    not null,
    project_id int,
    created_at timestamp not null,
    author_id  varchar(100),
    foreign key (project_id) references projects (id),
    foreign key (author_id) references authors (author_id)
);

create table comments
(
    id           bigint primary key auto_increment,
    pull_request bigint,
    author_id    varchar(100) not null,
    created_at   timestamp    not null,
    foreign key (id) references pull_requests (id)
);

CREATE INDEX projects_owner_index ON projects (owner);
CREATE INDEX projects_repository_index ON projects (repository);
CREATE INDEX author_id_index ON authors (author_id);