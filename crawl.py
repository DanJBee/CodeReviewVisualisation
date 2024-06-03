# GraphQL Crawler

import json
import logging
import os.path
from configparser import ConfigParser

import requests

from logger import init_logger

# Initialises & reads the settings.ini configuration file
config = ConfigParser()
config.read("settings.ini")

# Initialises the logger from the logger.py file
init_logger()

# Initialises the authorisation headers required by GitHub's GraphQL API
HEADERS = {"Authorization": "Bearer %s" % config["DEFAULT"]["PAT"]}

# Defines an owner-repository dictionary that will be iterated through during execution of the program
OWNER_REPO_DICT = {"baidu": "amis"}

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
def run_query(q: str) -> dict | None:
    request = requests.post(
        "https://api.github.com/graphql", json={"query": q}, headers=HEADERS
    )
    if request.status_code == 200:
        if "errors" in request.json():
            logging.info(f"GitHub returns error{request.json()['errors']}")
            return
        return request.json()
    else:
        raise Exception(
            "Query failed to run by returning code of {}. {}".format(
                request.status_code, q
            )
        )


# Generates the GraphQL query based on the repository owner, name, number of PRs required, & previous cursor value
def generate_query(
    repo_owner: str, name: str, prs_before_cursor: str | None = None, nums_prs: int = 10
) -> str:
    before_cursor = ', before: "%s"' % prs_before_cursor if prs_before_cursor else ""
    try:
        q = query_template % (repo_owner, name, str(nums_prs), before_cursor)
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
        # Return with a smaller number of PRs
        return generate_query(repo_owner, name, prs_before_cursor, nums_prs - 10)
    return q


# Crawls the data using the previously generated query & stores it in a given output directory
def crawl(
    repo_owner: str,
    name: str,
    output_dir: str,
    prs_before_cursor: str | None = None,
    nums_pr: int = 10,
) -> str | None:
    query = generate_query(repo_owner, name, prs_before_cursor, nums_pr)
    try:
        result = run_query(query)
        if result is None:
            return None
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
        return crawl(repo_owner, name, output_dir, prs_before_cursor, nums_pr - 5)
    # New cursor value originally set as None
    new_cursor = None

    output_name = (
        output_dir + prs_before_cursor
        if prs_before_cursor
        else output_dir + "first.json"
    )
    with open(output_name, "w") as file:
        file.write(json.dumps(result))

    try:
        edges = result["data"]["repository"]["pullRequests"]["edges"]
        if len(edges) == nums_pr:
            new_cursor = edges[0]["cursor"]
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


# Main loop
for owner, repo in OWNER_REPO_DICT.items():
    output_directory = "./%s-%s/" % (owner, repo)
    # If the output directory does not exist then create it
    if not os.path.isdir(output_directory):
        os.mkdir(output_directory)

    # Initially set prs_before_cursor = None since this is the first crawl
    cursor = crawl(owner, repo, output_directory, None)
    # While there is still pull request information being returned
    while cursor:
        cursor = crawl(owner, repo, output_directory, cursor)
    logging.info("Crawling PRs from '%s/%s is done" % (owner, repo))
