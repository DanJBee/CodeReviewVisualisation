# Project Setup Guide

Complete step-by-step guide to set up and run the Code Review Visualisation project.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Configuration](#configuration)
4. [Backend Setup](#backend-setup)
5. [Frontend Setup](#frontend-setup)
6. [Crawler Setup](#crawler-setup)
7. [Migrator Setup](#migrator-setup)
8. [Running the Complete Pipeline](#running-the-complete-pipeline)
9. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

| Software | Version | Check Command |
|----------|---------|---------------|
| Python | 3.10+ | `python3 --version` |
| Node.js | 18.18+ | `node --version` |
| npm | 9+ | `npm --version` |
| Java | 17+ | `java --version` |
| Maven | 3.6+ | `mvn --version` |
| MariaDB/MySQL | 10.5+ | `mysql --version` |

### Installation (macOS)

```bash
# Install Homebrew if not installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Python 3.10
brew install python@3.10

# Install Node.js
brew install node

# Install Java 17
brew install openjdk@17

# Install Maven
brew install maven

# Install MariaDB
brew install mariadb
brew services start mariadb
```

### Installation (Ubuntu/Debian)

```bash
# Update package list
sudo add-apt-repository ppa:deadsnakes/ppa
sudo apt update

# Install Python 3.10
sudo apt install python3.10 python3.10-venv python3-pip

# Install Node.js 18+
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs

# Install Java 17
sudo apt install openjdk-17-jdk

# Install Maven
sudo apt install maven

# Install MariaDB
sudo apt install mariadb-server
sudo systemctl start mariadb
```

### GitHub Personal Access Token (PAT)

You need a GitHub PAT to access the GitHub API:

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Select scopes: `repo`, `read:user`
4. Copy the generated token (starts with `ghp_`)

---

## Database Setup

### 1. Start MariaDB/MySQL Service

**macOS:**
```bash
brew services start mariadb
```

**Linux:**
```bash
sudo systemctl start mariadb
```

### 2. Secure MariaDB (First Time Only)

```bash
sudo mysql_secure_installation
```

Follow the prompts to set a root password.

### 3. Create the Database

```bash
# Login to MariaDB
mysql -u root -p

# Create database
CREATE DATABASE codereviewvisualisation;

# Verify
SHOW DATABASES;

# Exit
EXIT;
```

### 4. Verify Connection

```bash
mysql -u root -p codereviewvisualisation -e "SELECT 1;"
```

---

## Configuration

### 1. Update `settings.ini`

Edit the existing `settings.ini` file in the project root:

```ini
[DEFAULT]
PAT = ghp_YOUR_GITHUB_TOKEN_HERE

[DATABASE]
HOST = localhost
USER = root
PASSWORD = your_mysql_password
DATABASE_NAME = codereviewvisualisation
OWNER = 
REPO = 
```

**Important:** Replace:
- `ghp_YOUR_GITHUB_TOKEN_HERE` with your GitHub PAT
- `your_mysql_password` with your MariaDB root password

**Note:** `OWNER` and `REPO` will be set automatically when you run `set_repo.py` - no need to edit them manually.

### 2. Backend Database Configuration

Edit `backend/src/main/resources/database.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/codereviewvisualisation?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

---

## Backend Setup

### 1. Navigate to Backend Directory

```bash
cd backend
```

### 2. Build the Project

```bash
mvn clean install -DskipTests
```

### 3. Run the Backend

```bash
mvn spring-boot:run
```

The backend will start on http://localhost:8080

### 4. Verify Backend is Running

Open a new terminal and run:

```bash
curl http://localhost:8080/projects
```

You should see `[]` (empty array) if no data has been migrated yet.

### 5. Keep Backend Running

Keep this terminal open with the backend running. Open new terminals for other tasks.

---

## Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Run the Development Server

```bash
npm run dev
```

The frontend will start on http://localhost:5173

### 4. Access the Application

Open your browser and go to:
```
http://localhost:5173
```

---

## Crawler Setup

### 1. Navigate to Crawler Directory

```bash
cd crawler
```

### 2. Create Python Virtual Environment

```bash
python3.10 -m venv venv
```

### 3. Activate Virtual Environment

**macOS/Linux:**
```bash
source venv/bin/activate
```

**Windows:**
```bash
venv\Scripts\activate
```

### 4. Install Dependencies

```bash
pip install requests
```

### 5. Verify Installation

```bash
pip list
```

You should see `requests` in the list.

### 6. Deactivate Environment (When Done)

```bash
deactivate
```

---

## Migrator Setup

### 1. Navigate to Migrator Directory

```bash
cd migrator
```

### 2. Create Python Virtual Environment

```bash
python3.10 -m venv venv
```

### 3. Activate Virtual Environment

**macOS/Linux:**
```bash
source venv/bin/activate
```

**Windows:**
```bash
venv\Scripts\activate
```

### 4. Install Dependencies

```bash
pip install mysql-connector-python
```

### 5. Verify Installation

```bash
pip list
```

You should see `mysql-connector-python` in the list.

### 6. Deactivate Environment (When Done)

```bash
deactivate
```

---

## Running the Complete Pipeline

### Option 1: Using the Automation Script (Recommended)

From the project root directory:

```bash
python3 set_repo.py <owner> <repo>
```

**Examples:**
```bash
python3 set_repo.py microsoft typescript
python3 set_repo.py facebook react
```

This script automatically:
1. Updates `settings.ini` with the owner/repo
2. Runs the crawler (fetches PR data from GitHub)
3. Runs the splitter (splits data into individual files)
4. Runs the migrator (imports data into database)

### Option 2: Running Each Step Manually

#### Step 1: Update settings.ini

Edit `settings.ini` and set:
```ini
OWNER = your_target_owner
REPO = your_target_repo
```

#### Step 2: Run Crawler

```bash
cd crawler
source venv/bin/activate
python crawl.py
deactivate
```

#### Step 3: Run Splitter

```bash
cd crawler
source venv/bin/activate
python split.py
deactivate
```

#### Step 4: Run Migrator

```bash
cd migrator
source venv/bin/activate
python migrate.py
deactivate
```

---

## Viewing the Visualization

### 1. Ensure Backend is Running

```bash
cd backend
mvn spring-boot:run
```

### 2. Ensure Frontend is Running

```bash
cd frontend
npm run dev
```

### 3. Open the Application

1. Go to http://localhost:5173
2. You should see a list of projects
3. Click on a project to view the visualization

### 4. Understanding the Visualization

- **Nodes** = Users (PR authors and commenters)
- **Node size** = Activity level (more comments = larger)
- **Links** = Interactions between users
- **Link thickness** = Frequency of interaction

**Workload Indicators (colored rings around users):**
- **Green** = Available (0-2 open PRs)
- **Yellow** = Busy (3-4 open PRs)
- **Red dashed** = Overloaded (5+ open PRs)

Hover over a user to see their workload status.

---

## Troubleshooting

### Database Connection Error

**Error:** `Access denied for user 'root'@'localhost'`

**Solution:**
1. Verify your password in `settings.ini` and `database.properties`
2. Try resetting MariaDB root password:
   ```bash
   sudo mysql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
   FLUSH PRIVILEGES;
   ```

### Port Already in Use

**Error:** `Port 8080 was already in use`

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Python Module Not Found

**Error:** `ModuleNotFoundError: No module named 'requests'`

**Solution:**
```bash
cd crawler
source venv/bin/activate
pip install requests
```

### GitHub API Rate Limit

**Error:** `Rate limit exhausted`

**Solution:**
- The crawler will automatically sleep for 1 hour
- Or wait and run again later
- Ensure your PAT is valid and has correct scopes

### No Data in Frontend

**Issue:** Projects list is empty

**Solution:**
1. Check if backend is running: `curl http://localhost:8080/projects`
2. Check if data was migrated: 
   ```bash
   mysql -u root -p codereviewvisualisation -e "SELECT * FROM projects;"
   ```
3. Re-run the migration if needed

### Maven Build Failure

**Error:** `Could not resolve dependencies`

**Solution:**
```bash
cd backend
mvn dependency:purge-local-repository
mvn clean install -DskipTests
```

### Node.js Version Error

**Error:** `The engine "node" is incompatible with this module`

**Solution:**
```bash
# Check Node version
node --version

# Install Node 18+ using nvm
nvm install 18
nvm use 18
```

---

## Quick Reference

### Start Everything

Terminal 1 - Backend:
```bash
cd backend && mvn spring-boot:run
```

Terminal 2 - Frontend:
```bash
cd frontend && npm run dev
```

Terminal 3 - Crawl new repo:
```bash
python3 set_repo.py <owner> <repo>
```

### URLs

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Projects endpoint: http://localhost:8080/projects

### Common Commands

```bash
# Check database
mysql -u root -p codereviewvisualisation -e "SELECT * FROM projects;"

# View PR counts by state
mysql -u root -p codereviewvisualisation -e "SELECT state, COUNT(*) FROM pull_requests GROUP BY state;"

# Clear all data
mysql -u root -p codereviewvisualisation -e "DELETE FROM comments; DELETE FROM pull_requests; DELETE FROM authors; DELETE FROM projects;"
```
