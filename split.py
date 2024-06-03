# GraphQL splitter

import json
import os.path

OWNER_REPO_DICT = {
    "ant-design": "ant-design",
    "web-infra-dev": "modern-js",
    "emqx": "emqx",
    "pancakeswap": "pancake-frontend",
    "baidu": "amis",
}


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
for owner, repo in OWNER_REPO_DICT.items():
    # If the owner/repository pair does not have a directory then continue
    if not os.path.isdir(f"{owner}-{repo}"):
        continue

    print(owner, repo)
    output_directory = "./%s-%s-split/" % (owner, repo)
    if not os.path.isdir(output_directory):
        os.mkdir(output_directory)

    with open("./%s-%s/first.json" % (owner, repo)) as file:
        content = file.read()
        edges = extract_edges(content)
        save_nodes(output_directory, edges)
        previous_cursor = edges[0]["cursor"]

    while previous_cursor:
        print(previous_cursor)
        try:
            with open("./%s-%s/%s" % (owner, repo, previous_cursor)) as file:
                content = file.read()
                edges = extract_edges(content)
                save_nodes(output_directory, edges)
                previous_cursor = edges[0]["cursor"]
        except:
            previous_cursor = None
            continue
