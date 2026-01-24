# Project Changes Log

This document tracks all changes made to the codebase and database.

---

## Python Environment Setup

### Crawler (`/crawler`)
- Created Python 3.10 virtual environment: `python3.10 -m venv venv`
- Installed packages:
  - `requests`

### Migrator (`/migrator`)
- Created Python 3.10 virtual environment: `python3.10 -m venv venv`
- Installed packages:
  - `mysql-connector-python`

---

## Code Changes

### Crawler - `crawl.py`

#### 1. Fixed import statement
Changed from package import to local import:
```python
# Before
from crawler.logger import init_logger

# After
from logger import init_logger
```

#### 2. Fixed directory creation
Changed `os.mkdir` to `os.makedirs` to create nested directories:
```python
# Before
if not os.path.isdir(output_dir):
    os.mkdir(output_dir)

# After
if not os.path.isdir(output_dir):
    os.makedirs(output_dir)
```

#### 3. Fixed rate limit handling
Updated `crawl()` function to return a tuple `(cursor, rate_limited)` instead of just `cursor`:
```python
# Before
def crawl(...) -> str | None:
    ...
    return new_cursor

# After
def crawl(...) -> tuple[str | None, bool]:
    ...
    return new_cursor, False  # or True if rate limited
```

#### 4. Updated main loop for proper rate limit handling
```python
# Before - would get stuck sleeping when crawl was complete
while cursor:
    cursor = crawl(OWNER, REPO, output_dir, cursor)
    if cursor is None:
        time.sleep(3600)
        cursor = crawl(OWNER, REPO, output_dir, cursor)

# After - properly distinguishes between "done" and "rate limited"
while cursor or rate_limited:
    if rate_limited:
        logging.info("Rate limit exhausted. Sleeping for 1 hour...")
        time.sleep(3600)
        cursor, rate_limited = crawl(OWNER, REPO, output_dir, last_valid_cursor)
    else:
        last_valid_cursor = cursor
        cursor, rate_limited = crawl(OWNER, REPO, output_dir, cursor)
```

#### 5. Fixed cursor file writing
Added `last_valid_cursor` tracking to avoid writing `None` to file:
```python
# Before
with open(CURSOR_FILE, "w") as last_cursor_file:
    last_cursor_file.write(cursor)  # cursor could be None

# After
if last_valid_cursor:
    with open(CURSOR_FILE, "w") as last_cursor_file:
        last_cursor_file.write(last_valid_cursor)
```

---

## Database Changes

### 1. Added `type_name` column to `authors` table
The `authors` table was missing the `type_name` column required by the migrator script.

```sql
ALTER TABLE authors ADD COLUMN type_name varchar(100) NOT NULL DEFAULT 'User';
```

### 2. Removed incorrect UNIQUE constraint on `comments.author_id`
Hibernate/Spring auto-generated a UNIQUE constraint on `author_id` in the `comments` table, which prevented multiple comments from the same author.

```sql
ALTER TABLE comments DROP FOREIGN KEY FKgui1racsvordvt9frbxunakg2;
ALTER TABLE comments DROP INDEX UK9qvcavmtkru6h0dfydulg39x6;
ALTER TABLE comments ADD CONSTRAINT FK_comments_author FOREIGN KEY (author_id) REFERENCES authors(author_id);
ALTER TABLE comments ADD INDEX idx_comments_author_id (author_id);
```

### 3. Removed incorrect UNIQUE constraint on `pull_requests.author_id`
Same issue - Hibernate/Spring auto-generated a UNIQUE constraint preventing multiple PRs from the same author.

```sql
ALTER TABLE pull_requests DROP FOREIGN KEY FKbmji8juacb1k3sruqrpdden3l;
ALTER TABLE pull_requests DROP INDEX UK6nwm0hdxasmobdku70nbvvrt3;
ALTER TABLE pull_requests ADD CONSTRAINT FK_pr_author FOREIGN KEY (author_id) REFERENCES authors(author_id);
ALTER TABLE pull_requests ADD INDEX idx_pr_author_id (author_id);
ALTER TABLE pull_requests ADD CONSTRAINT FK_pr_project FOREIGN KEY (project_id) REFERENCES projects(id);
```

---

## Migrator Changes - `migrate.py`

### 1. Insert comment authors into authors table
Fixed foreign key constraint error by inserting comment authors into the `authors` table before inserting comments.

```python
# Before - comment author was not inserted into authors table
login = data["comments"]["nodes"][i]["author"]["login"]
comment_created_at = data["comments"]["nodes"][i]["createdAt"]
comment_created_at_timestamp = convert_timestamp(comment_created_at)
type_name = data["comments"]["nodes"][i]["author"]["__typename"]
insert_comment(cursor, login, comment_created_at_timestamp)

# After - now inserts comment author first
login = data["comments"]["nodes"][i]["author"]["login"]
avatar_url = data["comments"]["nodes"][i]["author"]["avatarUrl"]
comment_created_at = data["comments"]["nodes"][i]["createdAt"]
comment_created_at_timestamp = convert_timestamp(comment_created_at)
type_name = data["comments"]["nodes"][i]["author"]["__typename"]
insert_author(cursor, login, avatar_url, type_name)  # Insert author first
insert_comment(cursor, login, comment_created_at_timestamp)
```

### 2. Fixed `insert_author` duplicate detection logic
The function had inverted logic - it was appending to the list when author existed, and not tracking new authors after insertion.

```python
# Before - logic was backwards
if already_added:
    author_ids.append(author_id_value)  # Wrong: appending when already exists
else:
    db_cursor.execute(authors_sql, authors_values)  # Missing: didn't add to tracking list

# After - correct logic
if not already_added:
    db_cursor.execute(authors_sql, authors_values)
    author_ids.append((author_id_value,))  # Track the new author as tuple
```

### 3. Moved `author_ids` fetch to beginning of file processing loop
The `author_ids` was only fetched inside the `if data["author"] is not None` block, so it wasn't available when processing comments for PRs with deleted authors.

```python
# Before - author_ids only fetched when PR author exists
if data["author"] is not None:
    ...
    authors_ids_sql = "SELECT author_id FROM authors;"
    cursor.execute(authors_ids_sql)
    author_ids = cursor.fetchall()

# After - author_ids fetched at start of each file processing
# Check if the author ID is already in the authors table (fetch once per file)
authors_ids_sql = "SELECT author_id FROM authors;"
cursor.execute(authors_ids_sql)
author_ids = cursor.fetchall()

# Finds the pull request number
number = data["number"]
...
```

### 4. Changed INSERT to INSERT IGNORE for authors
To handle edge cases where duplicate detection might miss an author.

```python
# Before
authors_sql = "INSERT INTO authors VALUES (%s, %s, %s);"

# After
authors_sql = "INSERT IGNORE INTO authors VALUES (%s, %s, %s);"
```

### 5. Added commit after each file
Ensures database is in sync before fetching `author_ids` for the next file.

```python
# After each file is processed
print("Record inserted into database for pull request with ID", number)
db.commit()  # Commit after each file
```

---
## Backend Changes (Java)

*No backend changes made yet.*

---

## Frontend Changes (TypeScript/React)

*No frontend changes made yet.*

---

## Automation Script

### Created `set_repo.py` (Project Root)

A new automation script that updates the repository settings and runs the entire pipeline.

**Location:** `/set_repo.py`

**Usage:**
```bash
python set_repo.py <owner> <repo>
```

**Examples:**
```bash
python set_repo.py microsoft typescript
python set_repo.py facebook react
python set_repo.py torvalds linux
```

**Features:**
1. Updates `OWNER` and `REPO` in `settings.ini`
2. Runs `crawl.py` using crawler's virtual environment (`crawler/venv/bin/python`)
3. Runs `split.py` using crawler's virtual environment
4. Runs `migrate.py` using migrator's virtual environment (`migrator/venv/bin/python`)
5. Shows progress for each step and stops on errors
6. Displays success message when all steps complete

**Key implementation details:**
- Uses `subprocess.run()` to execute each script
- Each script runs with its own venv's Python interpreter (no need to manually activate)
- Automatically finds paths relative to the script location

---

## Additional Crawler Changes

### 1. Read OWNER/REPO from settings.ini (`crawl.py`)

Changed hardcoded values to read from configuration:
```python
# Before
OWNER = "hardcoded_owner"
REPO = "hardcoded_repo"

# After
OWNER = config["DATABASE"]["OWNER"]
REPO = config["DATABASE"]["REPO"]
```

### 2. Read OWNER/REPO from settings.ini (`split.py`)

Added ConfigParser import and changed hardcoded values:
```python
# Before
OWNER = "hardcoded_owner"
REPO = "hardcoded_repo"

# After
from configparser import ConfigParser
config = ConfigParser()
config.read("../settings.ini")
OWNER = config["DATABASE"]["OWNER"]
REPO = config["DATABASE"]["REPO"]
```

### 3. Added PR limit to crawler (`crawl.py`)

Added configurable maximum PR limit to avoid long crawl times:
```python
# Maximum number of PRs to crawl (set to None for unlimited)
MAX_PRS = 100
PRS_PER_REQUEST = 10
```

The crawler now tracks `prs_crawled` and stops when reaching `MAX_PRS`:
```python
if MAX_PRS and prs_crawled >= MAX_PRS:
    logging.info(f"Reached maximum of {MAX_PRS} PRs. Stopping crawl.")
    break
```

---

## Additional Migrator Changes

### 1. Fixed Ghost author insertion (`migrate.py`)

When a PR author is a deleted user (Ghost), now properly inserts the Ghost author before creating the PR:
```python
# Before - Ghost author was not inserted, causing foreign key errors
login = "Ghost"
insert_pull_request(cursor, number, project_id, created_at_timestamp, login)

# After - Insert Ghost author first
login = "Ghost"
insert_author(cursor, login, "", "User")  # Insert Ghost author
insert_pull_request(cursor, number, project_id, created_at_timestamp, login)
```

### 2. Check for existing project before inserting (`migrate.py`)

Added logic to reuse existing project instead of creating duplicates:
```python
# Before - Always inserted a new project
projects_sql = f"INSERT INTO projects (owner, repository) VALUES ('{OWNER}', '{REPO}');"
cursor.execute(projects_sql)

# After - Check if project exists first
cursor.execute(f"SELECT id FROM projects WHERE owner = '{OWNER}' AND repository = '{REPO}';")
existing_project = cursor.fetchone()

if existing_project:
    project_id = existing_project[0]
    print(f"Project '{OWNER}/{REPO}' already exists with ID {project_id}. Adding new PRs...")
else:
    cursor.execute(f"INSERT INTO projects (owner, repository) VALUES ('{OWNER}', '{REPO}');")
    project_id = cursor.lastrowid
    print(f"Created new project '{OWNER}/{REPO}' with ID {project_id}.")
    db.commit()
```

---

## Notes

- To activate crawler environment: `source ./bin/activate when you are in that venv folder from crawler`
- To activate migrator environment: `source ./bin/activate when you are in that venv folder from migrator`
- To deactivate any environment: `deactivate`
- Use `python3 set_repo.py <owner> <repo>` in CodeReviewVisualisation folder to automate the entire pipeline without manual steps