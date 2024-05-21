import os
import sys
import json
import requests
import logging
from logger import init_logger
from configparser import ConfigParser

config = ConfigParser()
config.read("settings.ini")


# Init logger for both file and stdout
def init_logger():
    log_format = "%(asctime)s [%(levelname)-5.5s]  %(message)s"
    logging.basicConfig(
        filename="execution.log", level=logging.DEBUG, format=log_format
    )
    formatter = logging.Formatter(log_format)
    stream_handler = logging.StreamHandler(sys.stdout)
    stream_handler.setFormatter(formatter)
    logging.getLogger().addHandler(stream_handler)


init_logger()

# The authz token is a temporary personal access token for testing
HEADERS = {"Authorization": "Bearer %s" % config["DEFAULT"]["PAT"]}

# OWNER_REPO_DICT = {"ant-design": "ant-design", "web-infra-dev": "modern.js", "emqx": "emqx", "pancakeswap":"pancake-frontend", "baidu": "amis"}
OWNER_REPO_DICT = {"baidu": "amis"}

query_template = """{
  repository(owner: "%s", name: "%s") {
    pullRequests(last: %s %s) {
      edges {
        cursor
        node {
          author {
            login
            __typename
          }
          bodyText
          createdAt
          mergedAt
          closed
          closedAt
          reviewDecision
          url
          title
          updatedAt
          publishedAt
          lastEditedAt
          number
          state
          userContentEdits(first: 100){
            nodes{
                editor{
                    login
                    __typename
                }
                diff
                editedAt
                id
            }
          }
          files(first:100) {
            totalCount
            nodes{
              path
              changeType
              additions
              deletions
            }
          }
          reviews(first:100){
            totalCount
            nodes{
              author{
                login
                __typename
              }
              comments(first:100){
                totalCount
                nodes{
                  bodyText
                  author{
                    login
                    __typename
                  }
                  diffHunk
                  url
                  startLine
                  line
                  createdAt
                  lastEditedAt
                }
              }
            }
          }
          comments(first: 100) {
            totalCount
            nodes {
              author {
                login
                __typename
              }
              id
              body
              createdAt
              lastEditedAt
              publishedAt
              updatedAt
              url
            }
          }
          commits(first: 100) {
            nodes {
              commit {
                id
                additions
                deletions
                message
                committedDate
                author {
                  user{
                    login
                    __typename
                  }
                }
              }
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


def generate_query(
    repo_owner: str, name: str, prs_before_cursor: str | None = None, num_prs: int = 10
) -> str:
    before_cursor = ', before: "%s"' % prs_before_cursor if prs_before_cursor else ""
    try:
        q = query_template % (repo_owner, name, str(num_prs), before_cursor)
    except Exception as e:
        logging.error("Query failed", e, exc_info=True)
        # return with smaller number of prs
        return generate_query(repo_owner, name, prs_before_cursor, num_prs - 10)
    return q


def crawl(
    repo_owner: str,
    name: str,
    output_directory: str,
    prs_before_cursor: str | None = None,
    num_prs: int = 10,
) -> str | None:
    query = generate_query(repo_owner, name, prs_before_cursor, num_prs)
    try:
        result = run_query(query)
        if result is None:
            return None
    except Exception as e:
        logging.error("Query failed", e, exc_info=True)
        return crawl(repo_owner, name, output_directory, prs_before_cursor, num_prs - 5)
    new_cursor = None

    output_name = (
        output_directory + prs_before_cursor
        if prs_before_cursor
        else output_dir + "first.json"
    )
    with open(output_name, "w") as f:
        f.write(json.dumps(result))

    try:
        edges = result["data"]["repository"]["pullRequests"]["edges"]
        if len(edges) == num_prs:
            new_cursor = edges[0]["cursor"]
            logging.info("Next cursor: %s" % new_cursor)

        remaining_rate_limit = result["data"]["rateLimit"][
            "remaining"
        ]  # Drill down the dictionary
        logging.info("Remaining rate limit - {}".format(remaining_rate_limit))
        if remaining_rate_limit < 1:
            logging.info("Spent all remaining rate")
            return
    except Exception as e:
        logging.error("Query failed", e, exc_info=True)

    return new_cursor


for owner, repo in OWNER_REPO_DICT.items():
    output_dir = "./%s-%s/" % (owner, repo)
    if not os.path.isdir(output_dir):
        os.mkdir(output_dir)

    cursor = crawl(owner, repo, output_dir, None)
    while cursor:
        cursor = crawl(owner, repo, output_dir, cursor)
    logging.info("Crawling prs from '%s/%s' is done" % (owner, repo))
