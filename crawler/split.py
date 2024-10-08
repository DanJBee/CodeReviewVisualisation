# GraphQL splitter

import json
import os.path
import sys

# Defines owner, repository name, & output constants
OWNER = "owner_name"
REPO = "repository_name"
OUTPUT = "../data/%s-%s" % (OWNER, REPO)


# Extracts the edges from the generated JSON files from the crawler
def extract_edges(json_content: str):
    j = json.loads(json_content)
    return j["data"]["repository"]["pullRequests"]["edges"]


# Saves each node into its own JSON file
def save_nodes(output_dir: str, edges_list: list):
    for edge in edges_list:
        node = edge["node"]
        with open("%s/%s.json" % (output_dir, node["number"]), "w") as output:
            output.write(json.dumps(node))


# Main loop
if __name__ == "__main__":
    # If the owner/repository pair does not have a directory then continue
    if not os.path.isdir(OUTPUT):
        sys.exit(1)

    output_directory = f"../data/{OWNER}-{REPO}-split/"
    if not os.path.isdir(output_directory):
        os.mkdir(output_directory)

    with open(f"../data/{OWNER}-{REPO}/first.json") as file:
        content = file.read()
        edges = extract_edges(content)
        save_nodes(output_directory, edges)
        previous_cursor = edges[0]["cursor"]

    while previous_cursor:
        print(previous_cursor)
        try:
            with open(f"../data/{OWNER}-{REPO}/%s" % previous_cursor) as file:
                content = file.read()
                edges = extract_edges(content)
                save_nodes(output_directory, edges)
                previous_cursor = edges[0]["cursor"]
        except:
            previous_cursor = None
            continue
