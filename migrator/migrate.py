# Migration file

import glob
import json
import os
from configparser import ConfigParser
from datetime import datetime, timezone

import mysql.connector

# Initialises & reads the settings.ini configuration file
config = ConfigParser()
config.read("../crawler/settings.ini")

# Initialises the database connection
db = mysql.connector.connect(
    host="localhost",
    user=config["DEFAULT"]["USER"],
    password=config["DEFAULT"]["PASSWORD"],
    database="codereviewvisualisation",
    time_zone="+00:00",  # Sets timezone to UTC
)

# Prints the database connection (for testing purposes, will probably be removed later on)
print(db)

# Initialises the database cursor
cursor = db.cursor()

# Change directory to the "microsoft-typescript-split" folder inside the crawler folder
os.chdir("../crawler/microsoft-typescript-split")

# Add the microsoft/typescript repository to the projects table
OWNER = "microsoft"
REPO = "typescript"
projects_sql = f"INSERT INTO projects VALUES (1, '{OWNER}', '{REPO}');"
cursor.execute(projects_sql)

# Initialise the pull request count to be 1
pull_request_id = 1

# Initialise the comment ID to 1
comment_id = 1

# Find all files with the .json extension
for filename in glob.glob("*.json"):
    # Open each file
    with open(filename, "r") as file:
        data = json.load(file)

    # Finds the project ID value for microsoft/typescript
    cursor.execute(
        "SELECT id FROM projects WHERE owner = 'microsoft' AND repository = 'typescript';"
    )
    result = cursor.fetchall()
    project_id = result[0][0]
    # Finds the pull request number
    number = data["number"]
    # Finds the pull request creation dat
    created_at = data["createdAt"]
    # Finds the pull request author identifier
    login = data["author"]["login"]
    # Finds the pull request author avatar URL
    avatar_url = data["author"]["avatarUrl"]
    # Finds the pull request author type name
    type_name = data["author"]["__typename"]
    # Finds the number of comments in the pull request
    comment_count = len(data["comments"]["nodes"])

    # Converts the pull request creation date into datetime format
    created_at_datetime = datetime.strptime(created_at, "%Y-%m-%dT%H:%M:%SZ")
    # Converts the pull request creation date in datetime format into the UTC timezone
    created_at_datetime = created_at_datetime.replace(tzinfo=timezone.utc)
    # Converts the pull request creation date in datetime format back into a string
    created_at_timestamp = created_at_datetime.strftime("%Y-%m-%d %H:%M:%S")

    # If the pull request author is not a bot, then execute the query
    if not type_name == "Bot":
        # Check if the author ID is already in the authors table
        authors_check_sql = (
            "SELECT author_id FROM authors WHERE author_id = '%s';" % login
        )
        cursor.execute(authors_check_sql)
        authors_check_result = cursor.fetchall()

        # If the author is not already in the authors table
        if len(authors_check_result) == 0:
            # Insert into authors table with no number originally
            authors_sql = "INSERT INTO authors (author_id, author_avatar_url, type_name) VALUES (%s, %s, %s);"
            authors_values = (login, avatar_url, type_name)
            cursor.execute(authors_sql, authors_values)

        # Get the last inserted author ID
        author_id_sql = "SELECT author_id FROM authors ORDER BY author_id DESC LIMIT 1;"
        cursor.execute(author_id_sql)
        author_id = cursor.fetchall()[0][0]

        # Insert into pull_requests table with no author_id & type_name originally
        pull_requests_sql = "INSERT INTO pull_requests (id, number, project_id, created_at) VALUES (%s, %s, %s, %s);"
        pull_requests_values = (
            pull_request_id,
            number,
            project_id,
            created_at_timestamp,
        )
        cursor.execute(pull_requests_sql, pull_requests_values)

        # Get the last inserted pull request number
        pull_request_number_sql = (
            "SELECT number FROM pull_requests ORDER BY number DESC LIMIT 1;"
        )
        cursor.execute(pull_request_number_sql)
        pull_request_number = cursor.fetchall()[0][0]

        # Update the latest authors table entry with the appropriate number
        update_authors_sql = "UPDATE authors SET number = %s WHERE author_id = %s;"
        update_authors_values = (pull_request_number, author_id)
        cursor.execute(update_authors_sql, update_authors_values)

        # Update the latest pull_requests table entry with the appropriate author_id & type_name
        update_pull_requests_sql = (
            "UPDATE pull_requests SET author_id = %s WHERE number = %s;"
        )
        update_pull_requests_values = (author_id, pull_request_number)
        cursor.execute(update_pull_requests_sql, update_pull_requests_values)

        # Increment the pull request count by 1
        pull_request_id += 1

    # Loop through every comment
    for i in range(comment_count):
        # If the author is a deleted user
        if data["comments"]["nodes"][i]["author"] is None:
            comment_created_at = data["comments"]["nodes"][i]["createdAt"]
            comment_created_at_datetime = datetime.strptime(
                comment_created_at, "%Y-%m-%dT%H:%M:%SZ"
            )
            comment_created_at_datetime = comment_created_at_datetime.replace(
                tzinfo=timezone.utc
            )
            comment_created_at_timestamp = comment_created_at_datetime.strftime(
                "%Y-%m-%d %H:%M:%S"
            )

            # If the author is a deleted user, then add "Deleted User" value for author_id & type_name
            comments_sql_deleted_user = "INSERT INTO comments VALUES (%s, %s, %s, %s);"
            comments_values_deleted_user = (
                comment_id,
                number,
                "Deleted User",
                comment_created_at_timestamp,
            )
            cursor.execute(comments_sql_deleted_user, comments_values_deleted_user)

            # Increment comment ID by 1
            comment_id += 1

            continue

        # Finds the author identifier
        login = data["comments"]["nodes"][i]["author"]["login"]
        # Finds the author avatar URL
        avatar_url = data["comments"]["nodes"][i]["author"]["avatarUrl"]
        # Finds the comment creation date
        comment_created_at = data["comments"]["nodes"][i]["createdAt"]
        comment_created_at_datetime = datetime.strptime(
            comment_created_at, "%Y-%m-%dT%H:%M:%SZ"
        )
        comment_created_at_datetime = comment_created_at_datetime.replace(
            tzinfo=timezone.utc
        )
        comment_created_at_timestamp = comment_created_at_datetime.strftime(
            "%Y-%m-%d %H:%M:%S"
        )
        type_name = data["comments"]["nodes"][i]["author"]["__typename"]

        # If the pull request author is not a bot, then execute the query
        if not type_name == "Bot":
            # Insert into comments table
            comments_sql = "INSERT INTO comments VALUES (%s, %s, %s, %s);"
            comments_values = (
                comment_id,
                number,
                login,
                comment_created_at_timestamp,
            )
            cursor.execute(comments_sql, comments_values)

            comment_id += 1

    # Commit the changes to the database
    db.commit()
    # Output a confirmation message that a record was inserted into the database
    print(
        cursor.rowcount,
        "record inserted into database from pull request with ID",
        number,
    )
