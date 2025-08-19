# Cafeteria Management System

A Java-based cafeteria management system with MySQL database support, containerized with Docker.

## Features
- Student management with loyalty program
- Menu management
- Order processing
- Payment handling (Cash, Visa, MasterCard)
- Loyalty points system

## Quick Start with Docker

### Prerequisites
- Docker Desktop installed and running
- Git (optional, for cloning)

### Running the Application

1. **Clone or download the project** (if receiving from someone else)
2. **Navigate to the project directory**
   ```bash
   cd iti
   ```

3. **Start the application**
   ```bash
   docker-compose up --build
   ```

4. **Access the application**
   - The application will start in interactive mode
   - Follow the console prompts to use the cafeteria system

### What happens when you run `docker-compose up --build`:
- MySQL database is created with sample data
- Java application is compiled and started
- Database is automatically initialized with tables and sample data
- Application connects to the database and is ready to use

### Stopping the Application
```bash
docker-compose down
```

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
