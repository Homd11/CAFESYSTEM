# Cafeteria Management System

A Java-based cafeteria management system with MySQL database support, containerized with Docker.

## Features
- Student management with loyalty program
- Menu management
- Order processing
- Payment handling (Cash, Visa, MasterCard)
- Loyalty points system

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed on your system

### Running the Application

1. **Navigate to the project directory:**
   ```bash
   cd cafeteria-deploy
   ```

2. **Start the database:**
   ```bash
   docker-compose up mysql -d
   ```

3. **Run the application interactively:**
   ```bash
   docker-compose run --rm app-interactive
   ```

### Alternative Running Methods

1. **Using Docker Desktop:**
   - Start only the database: `docker-compose up mysql -d`
   - Open Docker Desktop
   - Go to "Images" tab
   - Find the "cafeteria-deploy_app" image
   - Click "Run" button
   - In the run dialog, check "Interactive" option
   - Click "Run"

2. **Manual interactive session:**
   ```bash
   # Start database first
   docker-compose up mysql -d
   
   # Run app container interactively
   docker run -it --network cafeteria-deploy_cafeteria_network \
     -e DB_HOST=mysql \
     -e DB_PORT=3306 \
     -e DB_NAME=CafeteriaDB \
     -e DB_USER=root \
     -e DB_PASSWORD=ab1ab2ab \
     cafeteria-deploy_app java -cp /app/build:/app/lib/* Main
   ```

### Viewing in Docker Desktop

After running `docker-compose up --build`, you should see:

1. **In Docker Desktop Containers tab:**
   - `cafeteria_db` - MySQL Database for Cafeteria System
   - `cafeteria_app` - Cafeteria Management System

2. **To interact with the application:**
   - Click on `cafeteria_app` container
   - Go to "Logs" tab to see output
   - Go to "Exec" tab to run commands interactively

3. **Alternative ways to run interactively:**
   ```bash
   # Option 1: Attach to running container
   docker exec -it cafeteria_app java -cp /app/build:/app/lib/* Main
   
   # Option 2: Run in new terminal session
   docker exec -it cafeteria_app /bin/bash
   java -cp /app/build:/app/lib/* Main
   ```

### Troubleshooting

- **Can't see containers in Docker Desktop?** Make sure Docker Desktop is running and refresh the Containers tab
- **Can't type input?** Use the Docker Desktop Exec tab or the `docker exec` commands above
- **Application not starting?** Check the logs in Docker Desktop or run `docker-compose logs app`

### Stopping the Application
```bash
docker-compose down
```

To also remove volumes (database data):
```bash
docker-compose down -v
```

## Running in IDE (IntelliJ IDEA / Eclipse)

### Prerequisites
- Java 17+ installed
- MySQL server running locally
- IDE with Java support

### Setup Steps:

1. **Start MySQL Database Only**:
   ```bash
   docker-compose up mysql -d
   ```
   This starts only the MySQL container in detached mode.

2. **Open the project in your IDE**:
   - Open the `cafeteria-deploy` folder in IntelliJ IDEA or Eclipse
   - The project should automatically detect the source files

3. **Add MySQL Driver to Project**:
   - In IntelliJ: Go to File → Project Structure → Libraries → + → Java → Select `lib/mysql-connector-j-9.4.0.jar`
   - In Eclipse: Right-click project → Properties → Java Build Path → Libraries → Add JARs → Select the jar file

4. **Update Database Connection**:
   Your `DBconnection.java` should connect to:
   - Host: `localhost` (not `mysql`)
   - Port: `3306`
   - Database: `CafeteriaDB`
   - Username: `root`
   - Password: `ab1ab2ab`

5. **Run the Main class**:
   - Right-click on `Main.java` → Run
   - The application will start in your IDE console with full interactive input

### Database Access
- **Host**: localhost
- **Port**: 3306
- **Database**: CafeteriaDB
- **Username**: root
- **Password**: ab1ab2ab

## Manual Setup (Without Docker)

If you prefer to run without Docker:

1. Install MySQL Server
2. Create database using the `init-db.sql` file
3. Update database connection settings in `DBconnection.java`
4. Compile and run:
   ```bash
   javac -cp "lib/*" -d build src/**/*.java
   java -cp "build:lib/*" Main
   ```

## Project Structure
```
├── src/                    # Java source code
├── lib/                    # MySQL connector JAR
├── docker-compose.yml      # Docker services configuration
├── Dockerfile             # Application container configuration
├── init-db.sql           # Database initialization script
└── README.md             # This file
```

## Database Schema
The system uses the following main tables:
- `students` - Student information with loyalty accounts
- `menu_items` - Available food items
- `orders` - Customer orders
- `payments` - Payment records
- `loyalty_accounts` - Loyalty points tracking

## Troubleshooting

### Common Issues:
1. **Port 3306 already in use**: Stop any existing MySQL services
2. **Permission denied**: Make sure Docker Desktop is running
3. **Database connection failed**: Wait for MySQL container to fully start (check logs with `docker-compose logs mysql`)

### Viewing Logs:
```bash
docker-compose logs app    # Application logs
docker-compose logs mysql  # Database logs
```
