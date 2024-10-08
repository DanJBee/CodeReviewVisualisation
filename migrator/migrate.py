# Migration file

import glob
import json
import os
from configparser import ConfigParser
from datetime import datetime, timezone

import mysql.connector

# Initialises & reads the settings.ini configuration file
config = ConfigParser()
config.read("../settings.ini")

# Initialises the database connection
db = mysql.connector.connect(
    host=config["DATABASE"]["HOST"],
    user=config["DATABASE"]["USER"],
    password=config["DATABASE"]["PASSWORD"],
    database=config["DATABASE"]["DATABASE_NAME"],
    time_zone="+00:00",  # Sets timezone to UTC
)

# Initialises the database cursor
cursor = db.cursor()


# Add the microsoft/typescript repository to the projects table
OWNER = config["DATABASE"]["OWNER"]
REPO = config["DATABASE"]["REPO"]
projects_sql = f"INSERT INTO projects (owner, repository) VALUES ('{OWNER}', '{REPO}');"
cursor.execute(projects_sql)

# Change directory to the "microsoft-typescript-split" folder inside the crawler folder
os.chdir(f"../data/{OWNER}-{REPO}-split")

# Defines a function that converts a given creation date into a timestamp that MariaDB accepts
def convert_timestamp(creation_date: str) -> str:
    created_at_datetime = datetime.strptime(creation_date, "%Y-%m-%dT%H:%M:%SZ")
    created_at_datetime = created_at_datetime.replace(tzinfo=timezone.utc)
    return created_at_datetime.strftime("%Y-%m-%d %H:%M:%S")


# Defines a function that inserts an author into the authors table with cursor, login, avatar_url,
# & type_name parameters
def insert_author(db_cursor, author_id_value, avatar_url_value, type_name_value):
    # Defines a boolean variable showing whether an author is already added or not
    already_added = False

    # For each value in the author_ids list of tuples
    for (item,) in author_ids:
        # If the author is not already in the authors table
        if author_id_value == item:
            already_added = True

    # If the author is already in the authors table
    if already_added:
        # Append to the authors_id list
        author_ids.append(author_id_value)
    else:
        # Insert into authors table
        authors_sql = "INSERT INTO authors VALUES (%s, %s, %s);"
        authors_values = (author_id_value, avatar_url_value, type_name_value)
        db_cursor.execute(authors_sql, authors_values)


# Defines a function that inserts a pull request into the pull_requests table with number, project_id,
# created_at, & author_id parameters
def insert_pull_request(
    db_cursor, pr_number, project_id_value, created_at_timestamp_value, author_id_value
):
    # Insert into pull_requests table
    pull_requests_sql = (
        "INSERT INTO pull_requests (number, project_id, created_at, author_id)"
        "VALUES (%s, %s, %s, %s);"
    )
    pull_requests_values = [
        pr_number,
        project_id_value,
        created_at_timestamp_value,
        author_id_value,
    ]
    db_cursor.execute(pull_requests_sql, pull_requests_values)


# Defines a function that inserts a comment into the comments table with cursor, author_id, & created_at parameters
def insert_comment(db_cursor, author_id_value, created_at_date):
    # Get the last inserted pull request number
    pull_request_number_sql_query = "SELECT id FROM pull_requests ORDER BY id DESC;"
    cursor.execute(pull_request_number_sql_query)
    last_inserted_pull_request_number = cursor.fetchone()

    # Clear the result set before executing the next SQL query
    cursor.fetchall()

    # If the author is a deleted user, then a "Ghost" value is used for author_id
    comments_sql_deleted_user_query = "INSERT INTO comments (pull_request, author_id, created_at) VALUES (%s, %s, %s);"
    comments_deleted_user_values = (
        last_inserted_pull_request_number[0],
        author_id_value,
        created_at_date,
    )
    db_cursor.execute(comments_sql_deleted_user_query, comments_deleted_user_values)


# Find all files with the .json extension
for filename in glob.glob("*.json"):
    # Open each file
    with open(filename, "r") as file:
        data = json.load(file)

    # Finds the project ID value for the owner and repository names
    cursor.execute(
        "SELECT id FROM projects WHERE owner = '%s' AND repository = '%s';"
        % (OWNER, REPO)
    )
    result = cursor.fetchone()
    project_id = result[0]

    # Clear the result set
    cursor.fetchall()

    # Finds the pull request number
    number = data["number"]
    # Finds the pull request creation date
    created_at = data["createdAt"]
    # If the author of the pull request is not a deleted user
    if data["author"] is not None:
        # Defines the author array of the collected data
        author = data["author"]
        # Finds the pull request author identifier
        login = author["login"]
        # Finds the pull request author avatar URL
        avatar_url = author["avatarUrl"]
        # Finds the pull request author type name
        type_name = author["__typename"]

        # Converts the pull request creation date into timestamp format that MariaDB accepts
        created_at_timestamp = convert_timestamp(created_at)

        # Check if the author ID is already in the authors table
        authors_ids_sql = "SELECT author_id FROM authors;"
        cursor.execute(authors_ids_sql)
        author_ids = cursor.fetchall()

        # If the pull request author is not a bot, then execute the query
        if not type_name == "Bot":
            # Insert the author into the authors table
            insert_author(cursor, login, avatar_url, type_name)

            # Get the last inserted author ID using the current value of the "login" variable
            author_id = login

            # Insert the pull request into the pull_requests table
            insert_pull_request(
                cursor, number, project_id, created_at_timestamp, author_id
            )
    # If the author of the pull request is a deleted user
    else:
        # If the author of the pull request is a deleted user then set the author_id to "Deleted User"
        login = "Ghost"

        # Converts the pull request creation date into timestamp format that MariaDB accepts
        created_at_timestamp = convert_timestamp(created_at)

        # Insert the pull request into the pull_requests table
        insert_pull_request(cursor, number, project_id, created_at_timestamp, login)

    # Finds the number of comments in the pull request
    comment_count = len(data["comments"]["nodes"])

    # Loop through every comment
    for i in range(comment_count):
        # If the author is a deleted user
        if data["comments"]["nodes"][i]["author"] is None:
            comment_created_at = data["comments"]["nodes"][i]["createdAt"]
            # Converts the comment creation date into timestamp format that MariaDB accepts
            comment_created_at_timestamp = convert_timestamp(comment_created_at)

            # Insert the comment into the comments table
            insert_comment(cursor, "Ghost", comment_created_at_timestamp)
        else:
            # Finds the comment author identifier
            login = data["comments"]["nodes"][i]["author"]["login"]
            # Finds the comment creation date
            comment_created_at = data["comments"]["nodes"][i]["createdAt"]
            # Converts the comment creation date into a timestamp format that MariaDB accepts
            comment_created_at_timestamp = convert_timestamp(comment_created_at)
            # Finds the comment author type name
            type_name = data["comments"]["nodes"][i]["author"]["__typename"]

            # Insert the comment into the comments table
            insert_comment(cursor, login, comment_created_at_timestamp)

    # Output a confirmation message that a record was inserted into the database
    print("Record inserted into database for pull request with ID", number)

# Commit the changes to the database
db.commit()
