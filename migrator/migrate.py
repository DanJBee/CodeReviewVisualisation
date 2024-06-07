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

# Change directory to the "microsoft-typescript-split" folder inside the crawler folder
os.chdir("../data/microsoft-typescript-split")

# Add the microsoft/typescript repository to the projects table
OWNER = config["DATABASE"]["OWNER"]
REPO = config["DATABASE"]["REPO"]
projects_sql = f"INSERT INTO projects (owner, repository) VALUES ('{OWNER}', '{REPO}');"
cursor.execute(projects_sql)


# Defines a function that converts a given creation date into a timestamp that MariaDB accepts
def convert_timestamp(creation_date: str) -> str:
    created_at_datetime = datetime.strptime(creation_date, "%Y-%m-%dT%H:%M:%SZ")
    created_at_datetime = created_at_datetime.replace(tzinfo=timezone.utc)
    return created_at_datetime.strftime("%Y-%m-%d %H:%M:%S")


# Find all files with the .json extension
for filename in glob.glob("*.json"):
    # Open each file
    with open(filename, "r") as file:
        data = json.load(file)

    # Finds the project ID value for microsoft/typescript
    cursor.execute(
        "SELECT id FROM projects WHERE owner = 'microsoft' AND repository = 'typescript';"
    )
    result = cursor.fetchone()
    project_id = result[0]

    # Clear the result set
    cursor.fetchall()

    # Finds the pull request number
    number = data["number"]
    # Finds the pull request creation date
    created_at = data["createdAt"]
    # Finds the pull request author identifier
    login = data["author"]["login"]
    # Finds the pull request author avatar URL
    avatar_url = data["author"]["avatarUrl"]
    # Finds the pull request author type name
    type_name = data["author"]["__typename"]
    # Finds the number of comments in the pull request
    comment_count = len(data["comments"]["nodes"])

    # Converts the pull request creation date into timestamp format that MariaDB accepts
    created_at_timestamp = convert_timestamp(created_at)

    # Check if the author ID is already in the authors table
    authors_ids_sql = "SELECT author_id FROM authors;"
    cursor.execute(authors_ids_sql)
    author_ids = cursor.fetchall()

    # If the pull request author is not a bot, then execute the query
    if not type_name == "Bot":

        # Initialise the value of already_added to False
        already_added = False

        # For each value in the author_ids list of tuples
        for (item,) in author_ids:
            # If the author is not already in the authors table
            if login == item:
                # Set the value of already_added to true
                already_added = True
                break

        # If the author is not already added into the authors table
        if not already_added:
            # Insert into authors table with no number originally
            authors_sql = "INSERT INTO authors (author_id, author_avatar_url, type_name) VALUES (%s, %s, %s);"
            authors_values = (login, avatar_url, type_name)
            cursor.execute(authors_sql, authors_values)

        # Get the last inserted author ID using the current value of the "login" variable
        author_id = login

        # Insert into pull_requests table with author_id as "Ghost" originally
        pull_requests_sql = (
            "INSERT INTO pull_requests (number, project_id, created_at, author_id) VALUES (%s, %s, "
            "%s, %s);"
        )
        pull_requests_values = (
            number,
            project_id,
            created_at_timestamp,
            "Ghost",
        )
        cursor.execute(pull_requests_sql, pull_requests_values)

        # Get the last inserted pull request number
        pull_request_number_sql = (
            "SELECT number FROM pull_requests ORDER BY number DESC LIMIT 1;"
        )
        cursor.execute(pull_request_number_sql)
        pull_request_number = cursor.fetchall()[0][0]

        # Update the latest pull_requests table entry with the appropriate author_id & type_name
        update_pull_requests_sql = (
            "UPDATE pull_requests SET author_id = %s WHERE number = %s;"
        )
        update_pull_requests_values = (author_id, pull_request_number)
        cursor.execute(update_pull_requests_sql, update_pull_requests_values)

    # Loop through every comment
    for i in range(comment_count):
        # If the author is a deleted user
        if data["comments"]["nodes"][i]["author"] is None:
            comment_created_at = data["comments"]["nodes"][i]["createdAt"]
            # Converts the comment creation date into timestamp format that MariaDB accepts
            comment_created_at_timestamp = convert_timestamp(comment_created_at)

            # Get the last inserted pull request number
            pull_request_number_sql = (
                "SELECT number FROM pull_requests ORDER BY number DESC LIMIT 1;"
            )
            cursor.execute(pull_request_number_sql)
            pull_request_number = cursor.fetchall()[0][0]

            # If the author is a deleted user, then add "Deleted User" value for author_id
            comments_sql_deleted_user = "INSERT INTO comments (number, author_id, created_at) VALUES (%s, %s, %s);"
            comments_values_deleted_user = (
                pull_request_number,
                "Deleted User",
                comment_created_at_timestamp,
            )
            cursor.execute(comments_sql_deleted_user, comments_values_deleted_user)
            continue

        # Finds the author identifier
        login = data["comments"]["nodes"][i]["author"]["login"]
        # Finds the author avatar URL
        avatar_url = data["comments"]["nodes"][i]["author"]["avatarUrl"]
        # Finds the comment creation date
        comment_created_at = data["comments"]["nodes"][i]["createdAt"]
        # Converts the comment creation date into a timestamp format that MariaDB accepts
        comment_created_at_timestamp = convert_timestamp(comment_created_at)
        type_name = data["comments"]["nodes"][i]["author"]["__typename"]

        # Get the last inserted pull request number
        pull_request_number_sql = (
            "SELECT number FROM pull_requests ORDER BY number DESC LIMIT 1;"
        )
        cursor.execute(pull_request_number_sql)
        pull_request_number = cursor.fetchall()[0][0]

        # If the pull request author is not a bot, then execute the query
        if not type_name == "Bot":
            # Insert into comments table
            comments_sql = "INSERT INTO comments (number, author_id, created_at) VALUES (%s, %s, %s);"
            comments_values = (
                pull_request_number,
                login,
                comment_created_at_timestamp,
            )
            cursor.execute(comments_sql, comments_values)

    # Commit the changes to the database
    db.commit()
    # Output a confirmation message that a record was inserted into the database
    print(
        cursor.rowcount,
        "record inserted into database from pull request with ID",
        number,
    )
