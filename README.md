# Blog Application

A full-stack blog platform with user authentication, posts management, comments, likes, follow system, notifications, and admin panel.

## Technologies Used

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.5** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database ORM
- **PostgreSQL 15** - Relational database
- **JWT (JSON Web Tokens)** - Secure authentication
- **Gradle** - Build tool

### Frontend
- **Angular 20.2.0** - Frontend framework
- **TypeScript 5.9.2** - Programming language
- **RxJS 7.8.0** - Reactive programming
- **Bootstrap 5.3.8** - UI framework
- **Bootstrap Icons 1.13.1** - Icon library
- **Angular SSR** - Server-side rendering support
- **JWT Decode** - Token handling

### DevOps & Tools
- **Docker & Docker Compose** - Containerization
- **PostgreSQL Docker Image (alpine)** - Database container
- **Bash Scripts** - Application lifecycle management

## Features

- User authentication (registration, login, JWT-based)
- Create, read, update, delete blog posts
- Image uploads for posts
- Like and comment on posts
- Follow/unfollow users
- Real-time notifications
- User profiles
- Admin panel with statistics
- Report system
- Pagination and infinite scroll
- Filter posts by followed users

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17** or higher
- **Node.js** (v18 or higher) and **npm**
- **Docker** and **Docker Compose**
- **Git** (optional, for cloning)

## Project Structure

```
01-Blog/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Java source files
│   │       └── resources/  # Configuration files
│   ├── build.gradle        # Gradle configuration
│   └── gradlew            # Gradle wrapper
├── frontend/               # Angular frontend
│   ├── src/               # Source files
│   ├── package.json       # Node dependencies
│   └── angular.json       # Angular configuration
├── docker-compose.yml     # Docker configuration
├── start-app.sh          # Startup script
└── stop-app.sh           # Shutdown script
```

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://learn.zone01oujda.ma/git/babdelil/01blog.git
cd 01blog
```

### 2. Configure Database (Optional)

The default database configuration is:
- Database: `blogdb`
- User: `abdelilah`
- Password: `abdelilah`
- Port: `5432`

To change these, edit:
- [docker-compose.yml](docker-compose.yml) - Docker environment variables
- [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties) - Spring Boot configuration

### 3. Make Scripts Executable

```bash
chmod +x start-app.sh
chmod +x stop-app.sh
```

## Running the Application

### Quick Start (Recommended)

Use the provided startup script that handles everything automatically:

```bash
./start-app.sh
```

This script will:
1. Start PostgreSQL in Docker
2. Wait for database to be ready
3. Start the Spring Boot backend
4. Install frontend dependencies (if needed)
5. Start the Angular development server

### Access the Application

Once started, access:
- **Frontend**: [http://localhost:4200](http://localhost:4200)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Database**: localhost:5432

### Stopping the Application

```bash
./stop-app.sh
```

Or press `Ctrl+C` in the terminal where the app is running.

## Manual Setup (Alternative)

If you prefer to run services individually:

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Start Backend

```bash
cd backend
./gradlew bootRun
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

## Development

### Backend Development

```bash
cd backend
./gradlew build          # Build the project
./gradlew test           # Run tests
./gradlew bootRun        # Run the application
```

### Frontend Development

```bash
cd frontend
npm start                # Start dev server
npm run build            # Build for production
npm test                 # Run tests
npm run watch            # Build with watch mode
```

## API Endpoints

The backend exposes RESTful API endpoints for:

- **/api/auth** - Authentication (login, register)
- **/api/posts** - Post management
- **/api/users** - User profiles and follow system
- **/api/notifications** - User notifications
- **/api/reports** - Report system
- **/api/admin** - Admin panel operations

## Database Schema

Main entities:
- **User** - User accounts and profiles
- **Post** - Blog posts with content and images
- **Comment** - Comments on posts
- **Like** - Post likes
- **Follow** - User follow relationships
- **Notification** - User notifications
- **Report** - Content reports

## Configuration Files

- [backend/build.gradle](backend/build.gradle) - Backend dependencies
- [frontend/package.json](frontend/package.json) - Frontend dependencies
- [docker-compose.yml](docker-compose.yml) - Database container configuration
- Backend application properties - Database and Spring Boot settings

## Troubleshooting

### PostgreSQL Connection Issues

If the backend can't connect to PostgreSQL:

1. Check if Docker container is running:
   ```bash
   docker ps
   ```

2. Check PostgreSQL logs:
   ```bash
   docker logs blog-postgres
   ```

3. Verify database is ready:
   ```bash
   docker-compose exec postgres pg_isready -U abdelilah -d blogdb
   ```

### Port Already in Use

If ports 4200, 8080, or 5432 are already in use:

- **Frontend (4200)**: Kill the process or change port in [frontend/angular.json](frontend/angular.json)
- **Backend (8080)**: Kill the process or change port in [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties)
- **Database (5432)**: Stop other PostgreSQL instances or change port in [docker-compose.yml](docker-compose.yml)

### Node Modules Issues

If you encounter frontend dependency issues:

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### Gradle Issues

If Gradle build fails:

```bash
cd backend
./gradlew clean build --refresh-dependencies
```

## License

This project is licensed under the MIT License.
