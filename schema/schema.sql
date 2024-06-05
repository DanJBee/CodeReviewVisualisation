create table projects
(
    id         int          primary key auto_increment,
    owner      varchar(100) not null,
    repository varchar(100) not null,
    index projects_id_index on projects (id),
    index projects_owner_index on projects (owner),
    index projects_repository_index on projects (repository)
);

create table pull_requests
(
    number            bigint       primary key auto_increment,
    project_id        int          foreign key (project_id) references projects (id),
    created_at        timestamp    not null,
    author_id         varchar(100) foreign key (author_id) references author (author_id),
    type_name         varchar(100) foreign key (type_name) references author (type_name),
    index pull_requests_author_id_index on pull_requests (author_id),
    index pull_requests_id_index on pull_requests (project_id),
    index pull_requests_number_index on pull_requests (number),
    index pull_requests_type_name_index on pull_requests (type_name)
);

create table comments
(
    id                bigint       primary key auto_increment,
    number            bigint       foreign key (number) references pull_requests (number),
    author_id         varchar(100) null,
    type_name         varchar(100) null,
    created_at        timestamp    not null,
    index comments_author_id_index on comments (author_id),
    index comments_number_index on comments (number),
    index comments_type_name_index on comments (type_name)
);

create table authors
(
    id                bigint       primary key auto_increment,
    number            bigint       foreign key (number) references pull_requests (number),
    author_id         varchar(100) not null,
    author_avatar_url varchar(100) not null,
    type_name         varchar(100) not null,
    index author_author_avatar_url_index on authors (author_avatar_url),
    index author_author_id_index on authors (author_id),
    index author_id_index on authors (id),
    index author_number_index on authors (number),
    index author_type_name_index on authors (type_name)
);