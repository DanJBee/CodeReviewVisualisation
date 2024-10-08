# GraphQL Crawler

import json
import logging
import os.path
import time
from configparser import ConfigParser

import requests

from crawler.logger import init_logger

# Initialises & reads the settings.ini configuration file
config = ConfigParser()
config.read("../settings.ini")

# Initialises the logger
init_logger("execution.log")

# Initialises the authorisation headers required by GitHub's GraphQL API
HEADERS = {"Authorization": "Bearer %s" % config["DEFAULT"]["PAT"]}

# Defines a standard cursor file name
CURSOR_FILE = "cursor.txt"

# Defines owner, repository name, & output constants
OWNER = "owner_name"
REPO = "repository_name"

# Defines a template string for the GraphQL query
query_template = """{
  repository(owner: "%s", name: "%s") {
    pullRequests(last: %s %s) {
      edges {
        cursor
        node {
          number
          createdAt
          author {
            login
            avatarUrl
            __typename
          }
          comments(first: 100) {
            nodes {
              author {
                login
                avatarUrl
                __typename
              }
              createdAt
            }
          }
        }
      }
    }
  }
  rateLimit {
    limit
    cost
    remaining
    resetAt
  }
}
"""


# Runs the GraphQL query by submitting a post request to the GraphQL API endpoint & returning response in JSON format
def run_query(q: str) -> dict:
    request = requests.post(
        "https://api.github.com/graphql", json={"query": q}, headers=HEADERS
    )
    if request.status_code == 200:
        if "errors" in request.json():
            logging.info(
                f"GitHub returns error with message: {request.json()['errors'][0]['message']}"
            )
            return
        return request.json()
    else:
        raise Exception(
            "Query failed by returning status code of {}. {}".format(
                request.status_code, q
            )
        )


# Generates the GraphQL query based on the repository owner, name, number of PRs required, & previous cursor value
def generate_query(
    repo_owner: str, name: str, prs_before_cursor: str | None = None, nums_pr: int = 10
) -> str:
    before_cursor = ', before: "%s"' % prs_before_cursor if prs_before_cursor else ""
    try:
        query = query_template % (repo_owner, name, str(nums_pr), before_cursor)
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
        # Return with a smaller number of PRs
        return generate_query(repo_owner, name, prs_before_cursor, nums_pr - 10)
    return query


# Crawls the data using the previously generated query & stores it & the current cursor in a given output directory
def crawl(
    repo_owner: str,
    name: str,
    output_directory: str,
    prs_before_cursor: str | None = None,
    nums_pr: int = 10,
    current_cursor: str = None,
) -> str | None:
    query = generate_query(repo_owner, name, prs_before_cursor, nums_pr)
    try:
        result = run_query(query)
        if result is None:
            return None
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
        return crawl(
            repo_owner,
            name,
            output_directory,
            prs_before_cursor,
            nums_pr - 5,
            current_cursor,
        )
    # Instantiate new cursor value to None initially
    new_cursor = None

    output_name = (
        output_directory + prs_before_cursor
        if prs_before_cursor
        else output_directory + "first.json"
    )
    with open(output_name, "w") as file:
        file.write(json.dumps(result))

    try:
        edges = result["data"]["repository"]["pullRequests"]["edges"]
        if len(edges) == nums_pr:
            new_cursor = edges[0]["cursor"]
            # Save the current cursor value by calling save_cursor on the output directory & new cursor value
            save_cursor(output_directory, new_cursor)
            logging.info("Next cursor: %s" % new_cursor)

        remaining_rate_limit = result["data"]["rateLimit"][
            "remaining"
        ]  # Output the remaining rate limit
        logging.info("Remaining rate limit - {}".format(remaining_rate_limit))
        if remaining_rate_limit < 1:
            logging.info("Spent all remaining rate")
            return
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
    return new_cursor


# Saves the current cursor value to a file called "cursor.txt" for future reference
def save_cursor(output_directory: str, saved_cursor: str):
    # Save the current cursor value to "cursor.txt" for storage
    cursor_file = os.path.join(output_directory, CURSOR_FILE)
    with open(cursor_file, "w") as file:
        # Save & write the cursor to the assigned cursor file
        file.write(saved_cursor)
    logging.info("Saved cursor to file %s" % cursor_file)


# Loads the cursor from a given output directory
def load_cursor(output_directory: str) -> str | None:
    # Retrieve previously saved cursor file from save_cursor method
    saved_cursor_file = os.path.join(output_directory, CURSOR_FILE)
    # If the file does not exist then return
    if not os.path.exists(saved_cursor_file):
        return
    # Otherwise read & load the saved cursor file
    with open(saved_cursor_file, "r") as file:
        retrieved_cursor = file.read()
        logging.info(
            "Loaded cursor from saved cursor file %s with cursor value %s"
            % (saved_cursor_file, retrieved_cursor)
        )
        # Return the retrieved cursor value from the saved cursor file
        return retrieved_cursor


# Main loop
if __name__ == "__main__":
    # If the output directory does not exist then create it
    output_dir = f"../data/{OWNER}-{REPO}/"
    if not os.path.isdir(output_dir):
        os.mkdir(output_dir)

    # Loads the cursor value from the current output directory
    cursor = (
        load_cursor(output_dir)
        if load_cursor(output_dir)
        else crawl(OWNER, REPO, output_dir, None)
    )
    # If there is no currently loaded cursor i.e. this is the first query
    if not cursor:
        # Initially set prs_before_cursor = None since this is the first crawl
        cursor = crawl(OWNER, REPO, output_dir, None)
    # While there is still pull request information being returned
    while cursor:
        cursor = crawl(OWNER, REPO, output_dir, cursor)
        if cursor is None:
            # Sleep for an hour to recover the API rate limit
            time.sleep(3600)
            cursor = crawl(OWNER, REPO, output_dir, cursor)
    logging.info(f"Crawling all PRs from '{OWNER}/{REPO}' is done")
    # Save the last cursor, so it can be re-used in the next run of the program
    with open(CURSOR_FILE, "w") as last_cursor_file:
        last_cursor_file.write(cursor)
