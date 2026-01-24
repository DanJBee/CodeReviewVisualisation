# Project Changes Log

Improvements over the original CRV (Code Review Visualiser) system:

> **Visualising Developer Interactions in Code Reviews**  
> Daniel Bee & DongGyun Han, FSE Companion '25  
> https://doi.org/10.1145/3696630.3728583

---

## 1. Review Workload Indicator

**Problem:** The paper mentions teams "may struggle to distribute workloads effectively" and could "overlook a concentrated workload on a specific developer," but the original system only visualised past interactions — not current review burden.

**Solution:** Added visual indicators showing each developer's pending review queue:

- Tracks open PRs where a user has commented (as a reviewer, not author)
- Color-coded ring around each node: **green** (0-2) → **yellow** (3-4) → **red** (5+)
- Tooltip: "username • 2/5 reviews pending • Available"

**Files changed:**
- `backend/.../Node.java` — Added `openPrCount`, `MAX_WORKLOAD=5`, `getWorkloadValue()`, `isOverloaded()`
- `backend/.../GraphService.java` — Count open PRs per reviewer
- `frontend/src/types.ts` — Added `openPrCount`, `workloadValue`, `overloaded` to Node
- `frontend/src/graph.ts` — Added workload ring and tooltip

---

## 2. PR State Tracking

**Problem:** The crawler only fetched `createdAt`, `author`, and `comments` — no PR state.

**Solution:** Extended GraphQL query to include `state` (OPEN/CLOSED/MERGED), enabling workload calculation based on currently open PRs.

**Files changed:**
- `crawler/crawl.py` — Added `state` to GraphQL query
- `migrator/migrate.py` — Added state field to `insert_pull_request()`
- `backend/.../PullRequest.java` — Added `state` field with getter/setter
- Database — Added `state` column to `pull_requests` table

---

## 3. Crawler Bug Fixes

**Problems:**
- Incorrect import path (`from crawler.logger` → should be `from logger`)
- `os.mkdir` fails for nested directories
- No way to distinguish "done crawling" vs "rate limited"
- Hardcoded `OWNER` and `REPO` values

**Solutions:**
- Fixed import statement
- Changed to `os.makedirs`
- Updated return type to `tuple[str | None, bool]`
- Read `OWNER`/`REPO` from `settings.ini`
- Added configurable `MAX_PRS = 100`

---

## 4. Migrator Bug Fixes

**Problems:**
- Crashed on deleted GitHub users ("Ghost" authors)
- Duplicate detection logic was inverted
- Re-running created duplicate projects
- No commits between files (data loss on error)

**Solutions:**
- Insert "Ghost" author before creating PRs for deleted users
- Fixed with `INSERT IGNORE`
- Check for existing projects before insertion
- Commit after each JSON file

---

## 5. Cleaner UI

**Problem:** Saturated colors everywhere (red/yellow/green on nodes AND edges) was overwhelming.

**Solution:**
- Neutral gray links (`#cbd5e1` at 60% opacity)
- Single color system — only workload rings use color
- Softer Tailwind palette colors
- Sharp avatars (removed blur filter)
- Clean single-line tooltips

---

## 6. Automation Script

**Problem:** Setting up a new repository required manually editing code and running multiple scripts.

**Solution:** Created `set_repo.py`:

```bash
python3 set_repo.py <owner> <repo>
```

Automatically: updates `settings.ini` → runs crawler → runs split → runs migrator.

---

## Configuration

| Setting | Location | Default | Description |
|---------|----------|---------|-------------|
| `MAX_PRS` | `crawler/crawl.py` | 100 | Max PRs to crawl (None = unlimited) |
| `MAX_WORKLOAD` | `Node.java` | 5 | Reviews before user is "overloaded" |