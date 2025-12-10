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

2. **Configure Database and Gemini API Key:**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   gemini.api.key=your_api_key_here
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
- `POST /api/v1/auth/signup` - Create user (returns 201 Created)
- `POST /api/v1/auth/login` - Authenticate user (returns 200 OK)

### Conversations
- `POST /api/v1/conversations` - Create conversation (returns 201 Created)
- `GET /api/v1/conversations` - List conversations (returns 200 OK)
- `GET /api/v1/conversations/{id}/messages` - Get messages (returns 200 OK)
- `PUT /api/v1/conversations/{id}/title` - Update title (returns 200 OK)
- `DELETE /api/v1/conversations/{id}` - Delete conversation (returns 200 OK)

### Messages
- `POST /api/v1/conversations/{id}/messages` - Send message (returns 200 OK)

### Headers
All conversation/message endpoints require: `X-User-Id: {userId}`

### Error Format
All errors follow this standardized format:
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable error message"
}
```

Common error codes:
- `VALIDATION_ERROR` - Input validation failed (400)
- `USER_NOT_FOUND` - User doesn't exist (404)
- `CONVERSATION_NOT_FOUND` - Conversation doesn't exist (404)
- `UNAUTHORIZED` - Access denied (403)
- `LIMIT_EXCEEDED` - Resource limit reached (400)
- `AI_SERVICE_ERROR` - Gemini API failure (500)
- `INTERNAL_ERROR` - Unexpected server error (500)

## API Documentation
For complete API specifications, request/response formats, and examples, see:
- `docs/LLD.md` sections 8.1-8.2 (API Specifications)

