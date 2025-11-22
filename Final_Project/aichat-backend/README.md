# AI Chat Backend

Spring Boot REST API backend for the AI Chat Desktop Application.

## Prerequisites

- JDK 1.8 or higher
- Maven 3.6+
- PostgreSQL 12+
- Gemini API Key

## Setup

1. **Create PostgreSQL Database:**
   ```bash
   createdb ai_chat
   psql ai_chat < src/main/resources/db/schema.sql
   ```

2. **Configure Database:**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

3. **Set Gemini API Key:**
   ```bash
   export GEMINI_API_KEY=your_api_key_here
   ```

## Build and Run

```bash
mvn clean package
java -jar target/aichat-backend-1.0.0.jar
```

Or run directly:
```bash
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/signup` - Create new user account
- `POST /api/v1/auth/login` - Login with username/password

### Conversations
- `POST /api/v1/conversations` - Create new conversation
- `GET /api/v1/conversations` - Get user's conversations
- `GET /api/v1/conversations/{id}/messages` - Get conversation messages
- `POST /api/v1/conversations/{id}/messages` - Send message and get AI reply
- `PUT /api/v1/conversations/{id}/title` - Update conversation title
- `DELETE /api/v1/conversations/{id}` - Delete conversation (soft delete)

## Testing

Use Postman or curl to test endpoints:

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

