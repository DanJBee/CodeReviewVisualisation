# CodeReviewVisualisation
### Visualisation tool to optimise the code review process for the ICSE 2025 Demonstration Track.
#### Developed by DongGyun Han and Dan Bee. 

## Usage instructions
### Crawling
To crawl data, you will first need to ensure that your GitHub Personal Access Token is added into the `settings.ini` file located at the root of this repository. You should add this under the `PAT` attribute.
In addition to this, you will need to install the `requests` library: `pip install requests`.
After this, simply navigate to the `crawler` directory (`cd crawler/`) and run: `python crawl.py` and wait until the crawling halts.

### Splitting (optional)
To split data, ensure that you edit the `OWNER` and `REPO` constants at the beginning of the file to adjust to the project that you are crawling.
After this is done, run: `python split.py` and wait until it is completed.

### Migrating
To migrate data to your MariaDB relational database, first navigate to the `migrator` directory (`cd migrator/`).
Next, you will need to install the `mysql-connector-python` library: `pip install mysql-connector-python`.
After this, you can run the migrator: `python migrate.py`.

### Running the backend
You will need a MariaDB database with a `codereviewvisualisation` schema created. Spring Boot will create all the necessary tables for you.
Furthermore, you will need to set credentials for this database and add them to the `database.properties` file in the `src/main/resources` directory. Please do not edit any attribute names.
Java version 22 will be required to run the backend.
To run the backend of the system, you can navigate to the `backend` directory (`cd backend/`) and run (assuming Maven is installed and added to PATH): `mvn spring-boot:run`. Port `:8080` will need to be available.

### Running the frontend
To run the frontend of the system, navigate to the `frontend` directory (cd `cd frontend/`) and start off by installing the necessary NPM dependencies: `npm install`.
The local server can be started using: `npm run dev`. Port `:5173` will need to be available.
