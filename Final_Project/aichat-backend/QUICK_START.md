# Quick Start Guide - Backend Implementation

## Prerequisites Check

```bash
# Check Java version (must be 1.8)
java -version

# Check Maven (must be 3.6+)
mvn -version

# Check PostgreSQL (must be 12+)
psql --version
```

## Step 1: Database Setup

```bash
# Create database
createdb ai_chat

# Apply schema
psql ai_chat < src/main/resources/db/schema.sql

# Verify tables created
psql ai_chat -c "\dt"
# Should show: app_user, conversation, message
```

## Step 2: Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
```

## Step 3: Set Gemini API Key

```bash
# Windows (PowerShell)
$env:GEMINI_API_KEY="your_api_key_here"

# Linux/Mac
export GEMINI_API_KEY=your_api_key_here
```

## Step 4: Build and Test

```bash
# Navigate to backend directory
cd Final_Project/aichat-backend

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Start application
mvn spring-boot:run
```

## Step 5: Verify Database Connectivity

Run with test profile to verify database:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test-db
```

You should see:
```
=== Database Connectivity Test ===
✓ Created test user with ID: 1
✓ Successfully read user: test_user_...
✓ Successfully found user by username
✓ Cleaned up test user
=== Database test completed successfully ===
```

## Step 6: Test with Postman/curl

Once the application is running (Phase 4 - Controllers), test endpoints:

```bash
# Signup
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

## Troubleshooting

### Maven not found
- Install Maven or add to PATH
- Or use IDE's built-in Maven

### Database connection failed
- Verify PostgreSQL is running: `pg_isready`
- Check credentials in `application.properties`
- Verify database exists: `psql -l | grep ai_chat`

### Gemini API errors
- Verify `GEMINI_API_KEY` environment variable is set
- Check API key is valid
- Review error logs for details

### Port 8080 already in use
- Change port in `application.properties`: `server.port=8081`
- Or stop the process using port 8080

## Implementation Status

✅ **Phases 1-3 Complete:**
- Database schema and entities
- Repository interfaces
- Service layer (Auth, Chat, Gemini)
- Validation and error handling
- Test suite

⏳ **Phase 4 Pending:**
- Controllers (REST endpoints)
- Exception handler
- API testing

See `IMPLEMENTATION_STATUS.md` for detailed status.

