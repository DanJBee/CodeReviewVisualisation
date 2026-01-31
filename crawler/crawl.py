# GraphQL Crawler

import json
import logging
import os.path
import time
from configparser import ConfigParser

import requests

from logger import init_logger

# Initialises & reads the settings.ini configuration file
config = ConfigParser()
config.read("../settings.ini")

# Initialises the logger
init_logger("execution.log")

# Initialises the authorisation headers required by GitHub's GraphQL API
HEADERS = {"Authorization": "Bearer %s" % config["DEFAULT"]["PAT"]}

# Defines a standard cursor file name
CURSOR_FILE = "cursor.txt"

# Defines owner, repository name from settings
OWNER = config["DATABASE"]["OWNER"]
REPO = config["DATABASE"]["REPO"]

# Maximum number of PRs to crawl (set to None for unlimited)
MAX_PRS = 100
PRS_PER_REQUEST = 10

# Defines a template string for the GraphQL query
query_template = """{
  repository(owner: "%s", name: "%s") {
    pullRequests(last: %s %s) {
      edges {
        cursor
        node {
          number
          createdAt
          state
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
              # Request commnent body as well to further analyse its quality
              body
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
    repo_owner: str, name: str, prs_before_cursor: str | None = None, nums_pr: int = PRS_PER_REQUEST
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
# Returns a tuple: (next_cursor, rate_limited)
# - next_cursor: the cursor for the next page, or None if done
# - rate_limited: True if rate limit was exhausted
def crawl(
    repo_owner: str,
    name: str,
    output_directory: str,
    prs_before_cursor: str | None = None,
    nums_pr: int = 10,
    current_cursor: str = None,
) -> tuple[str | None, bool]:
    query = generate_query(repo_owner, name, prs_before_cursor, nums_pr)
    try:
        result = run_query(query)
        if result is None:
            return None, False

        # make a request to github api in order to fetch the diff and patch urls
        # and append them to the pull request object
        # these will be further given to the ai to analyse the content quality
        repository = result.get("data", {}).get("repository", {})
        pr_edges = repository.get("pullRequests", {}).get("edges", [])

        for edge in pr_edges:
            pr = edge.get("node")

            if pr:
                pr_number = pr.get("number")
                if pr_number:
                    api_url = f"https://api.github.com/repos/{repo_owner}/{name}/pulls/{pr_number}"
                    headers = {
                        'Authorization': f'token {config["DEFAULT"]["PAT"]}',
                        'Accept': 'application.vnd.github.v3.diff'
                    }
                    response = requests.get(api_url, headers=headers)
                    if response.status_code == 200:
                        data = response.json()

                        diff_url = data.get("diff_url")
                        patch_url = data.get("patch_url")

                        pr["diff_link"] = diff_url
                        pr["patch_link"] = patch_url
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
            return new_cursor, True  # Rate limited
    except Exception as exception:
        logging.error("Query failed", exception, exc_info=True)
    return new_cursor, False


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
        os.makedirs(output_dir)

    # Loads the cursor value from the current output directory
    cursor = load_cursor(output_dir)
    rate_limited = False
    prs_crawled = 0
    
    # If there is no currently loaded cursor i.e. this is the first query
    if not cursor:
        # Initially set prs_before_cursor = None since this is the first crawl
        cursor, rate_limited = crawl(OWNER, REPO, output_dir, None)
        prs_crawled += PRS_PER_REQUEST
    
    # Track the last valid cursor
    last_valid_cursor = cursor
    
    # While there is still pull request information being returned
    while cursor or rate_limited:
        # Check if we've reached the maximum number of PRs
        if MAX_PRS and prs_crawled >= MAX_PRS:
            logging.info(f"Reached maximum of {MAX_PRS} PRs. Stopping crawl.")
            break
            
        if rate_limited:
            # Sleep for an hour to recover the API rate limit
            logging.info("Rate limit exhausted. Sleeping for 1 hour...")
            time.sleep(3600)
            cursor, rate_limited = crawl(OWNER, REPO, output_dir, last_valid_cursor)
        else:
            last_valid_cursor = cursor
            cursor, rate_limited = crawl(OWNER, REPO, output_dir, cursor)
            prs_crawled += PRS_PER_REQUEST
    
    logging.info(f"Crawling PRs from '{OWNER}/{REPO}' is done ({prs_crawled} PRs)")
    # Save the last cursor, so it can be re-used in the next run of the program
    if last_valid_cursor:
        with open(CURSOR_FILE, "w") as last_cursor_file:
            last_cursor_file.write(last_valid_cursor)
