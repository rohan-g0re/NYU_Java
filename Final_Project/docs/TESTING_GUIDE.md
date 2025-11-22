# Phase 4 Backend Controllers - Comprehensive Testing Guide

## Document Purpose
This guide provides step-by-step instructions for testing Phase 4 backend controllers implementation. Use this document when Java environment is set up to verify all functionality.

**Last Updated:** 2025-02-21  
**Phase:** Phase 4 - Backend Controllers  
**Status:** Ready for Testing

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Pre-Testing Verification](#pre-testing-verification)
4. [Manual Testing Procedures](#manual-testing-procedures)
5. [Postman Collection Testing](#postman-collection-testing)
6. [Edge Case Testing](#edge-case-testing)
7. [Integration Testing](#integration-testing)
8. [Verification Checklist](#verification-checklist)
9. [Troubleshooting](#troubleshooting)
10. [Test Results Log](#test-results-log)

---

## Prerequisites

### Required Software
- [ ] JDK 1.8 or higher installed
- [ ] Maven 3.6+ installed
- [ ] PostgreSQL 12+ installed and running
- [ ] Postman installed (for API testing)
- [ ] Git (if cloning repository)

### Required Configuration
- [ ] PostgreSQL database `ai_chat` created
- [ ] Database schema applied (`schema.sql`)
- [ ] `application.properties` configured with database credentials
- [ ] `GEMINI_API_KEY` environment variable set (for AI message tests)

### Verify Installation
```bash
# Check Java version
java -version
# Expected: java version "1.8.0_xxx" or higher

# Check Maven version
mvn -version
# Expected: Apache Maven 3.6.x or higher

# Check PostgreSQL
psql --version
# Expected: psql (PostgreSQL) 12.x or higher

# Check PostgreSQL is running
pg_isready
# Expected: accepting connections
```

---

## Environment Setup

### Step 1: Database Setup

```bash
# Create database
createdb ai_chat

# Apply schema
psql ai_chat < Final_Project/aichat-backend/src/main/resources/db/schema.sql

# Verify tables created
psql ai_chat -c "\dt"
# Expected output: app_user, conversation, message tables
```

### Step 2: Configure Application Properties

Edit `Final_Project/aichat-backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE

# Server Configuration
server.port=8080
```

### Step 3: Set Gemini API Key

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your_api_key_here"
```

**Windows (CMD):**
```cmd
set GEMINI_API_KEY=your_api_key_here
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY=your_api_key_here
```

### Step 4: Build and Start Backend

```bash
# Navigate to backend directory
cd Final_Project/aichat-backend

# Build project
mvn clean package

# Start server
mvn spring-boot:run

# OR run JAR directly
java -jar target/aichat-backend-1.0.0.jar
```

**Expected Output:**
```
Started AichatApplication in X.XXX seconds
```

**Verify Server:**
```bash
# Test server is running
curl http://localhost:8080/api/v1/auth/login
# Expected: 400 Bad Request (missing body, but server responds)
```

---

## Pre-Testing Verification

### Code Verification Checklist

- [ ] **Controllers exist:**
  - [ ] `AuthController.java` exists
  - [ ] `ChatController.java` exists
  - [ ] `GlobalExceptionHandler.java` exists

- [ ] **Validation utilities exist:**
  - [ ] `HeaderValidator.java` exists
  - [ ] `PathValidator.java` exists

- [ ] **DTOs exist:**
  - [ ] All request DTOs in `dto/request/`
  - [ ] All response DTOs in `dto/response/`

- [ ] **Exception classes exist:**
  - [ ] `ApiException.java`
  - [ ] `UserNotFoundException.java`
  - [ ] `ConversationNotFoundException.java`
  - [ ] `UnauthorizedException.java`
  - [ ] `ValidationException.java`
  - [ ] `AiServiceException.java`

### Compile Verification

```bash
cd Final_Project/aichat-backend
mvn compile
# Expected: BUILD SUCCESS
```

### Import Postman Collection

1. Open Postman
2. Click "Import"
3. Select `Final_Project/docs/postman/AI_Chat_Backend_API.json`
4. Import `Final_Project/docs/postman/AI_Chat_Backend_API_Environment.json`
5. Select "AI Chat Backend API Environment" from dropdown
6. Verify `baseUrl` is set to `http://localhost:8080/api/v1`

---

## Manual Testing Procedures

### Test 1: Authentication Endpoints

#### 1.1 Signup - Valid Request

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"password123"}'
```

**Expected Response:**
- Status: `201 Created`
- Body:
```json
{
  "userId": 1,
  "username": "testuser1"
}
```

**Verification:**
- [ ] Status code is 201
- [ ] Response contains `userId` (number)
- [ ] Response contains `username` (string)
- [ ] `userId` is positive integer

**Save userId for later tests:** `export USER_ID=1` (or note it down)

---

#### 1.2 Signup - Duplicate Username

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"differentpassword"}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body:
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Username already exists"
}
```

**Verification:**
- [ ] Status code is 400
- [ ] Error code is `VALIDATION_ERROR`
- [ ] Message indicates username exists

---

#### 1.3 Signup - Invalid Username (Too Short)

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","password":"password123"}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error message

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions username length

---

#### 1.4 Signup - Invalid Username (Special Characters)

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"test@user","password":"password123"}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions invalid characters

---

#### 1.5 Signup - Password Too Short

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"12345"}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions password length

---

#### 1.6 Login - Valid Credentials

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"password123"}'
```

**Expected Response:**
- Status: `200 OK`
- Body:
```json
{
  "userId": 1,
  "username": "testuser1"
}
```

**Verification:**
- [ ] Status code is 200
- [ ] Response matches signup response format
- [ ] `userId` matches previously created user

---

#### 1.7 Login - Invalid Password

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"wrongpassword"}'
```

**Expected Response:**
- Status: `401 Unauthorized`
- Body:
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Username or password is incorrect."
}
```

**Verification:**
- [ ] Status code is 401
- [ ] Error code is `INVALID_CREDENTIALS`

---

#### 1.8 Login - Non-existent User

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"nonexistent","password":"password123"}'
```

**Expected Response:**
- Status: `404 Not Found`
- Body:
```json
{
  "error": "USER_NOT_FOUND",
  "message": "No user exists with this username."
}
```

**Verification:**
- [ ] Status code is 404
- [ ] Error code is `USER_NOT_FOUND`

---

### Test 2: Conversation Endpoints

**Note:** Replace `{userId}` with actual user ID from signup/login tests.

#### 2.1 Create Conversation - With Title

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"title":"My First Chat"}'
```

**Expected Response:**
- Status: `201 Created`
- Body:
```json
{
  "id": 1,
  "title": "My First Chat",
  "createdAt": "2025-02-21T10:30:00Z"
}
```

**Verification:**
- [ ] Status code is 201
- [ ] Response contains `id`, `title`, `createdAt`
- [ ] `createdAt` is valid ISO timestamp

**Save conversationId:** `export CONVERSATION_ID=1`

---

#### 2.2 Create Conversation - Without Title

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{}'
```

**Expected Response:**
- Status: `201 Created`
- Body contains default title ("New Chat" or "Untitled Chat")

**Verification:**
- [ ] Status code is 201
- [ ] Title defaults appropriately

---

#### 2.3 Create Conversation - Missing Header

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -d '{"title":"Test"}'
```

**Expected Response:**
- Status: `400 Bad Request` or `401 Unauthorized`
- Body contains error about missing header

**Verification:**
- [ ] Status code is 400 or 401
- [ ] Error message mentions header requirement

---

#### 2.4 Create Conversation - Invalid User ID

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: -1" \
  -d '{"title":"Test"}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body:
```json
{
  "error": "VALIDATION_ERROR",
  "message": "X-User-Id must be a positive integer"
}
```

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions positive integer requirement

---

#### 2.5 List Conversations - Valid

**Command:**
```bash
curl -X GET http://localhost:8080/api/v1/conversations \
  -H "X-User-Id: {userId}"
```

**Expected Response:**
- Status: `200 OK`
- Body: Array of conversations ordered by `createdAt DESC`

**Verification:**
- [ ] Status code is 200
- [ ] Response is JSON array
- [ ] Conversations ordered by creation date (newest first)
- [ ] Deleted conversations not included

---

#### 2.6 Update Title - Valid

**Command:**
```bash
curl -X PUT http://localhost:8080/api/v1/conversations/{conversationId}/title \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"title":"Updated Chat Title"}'
```

**Expected Response:**
- Status: `200 OK`
- Body: Empty

**Verification:**
- [ ] Status code is 200
- [ ] Response body is empty
- [ ] Title updated (verify with GET request)

---

#### 2.7 Update Title - Empty

**Command:**
```bash
curl -X PUT http://localhost:8080/api/v1/conversations/{conversationId}/title \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"title":""}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions title requirement

---

#### 2.8 Delete Conversation - Valid

**Command:**
```bash
curl -X DELETE http://localhost:8080/api/v1/conversations/{conversationId} \
  -H "X-User-Id: {userId}"
```

**Expected Response:**
- Status: `200 OK`
- Body: Empty

**Verification:**
- [ ] Status code is 200
- [ ] Conversation soft-deleted (not in GET list)
- [ ] Messages still exist (soft delete only)

---

### Test 3: Message Endpoints

#### 3.1 Get Messages - Empty Conversation

**Command:**
```bash
curl -X GET http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "X-User-Id: {userId}"
```

**Expected Response:**
- Status: `200 OK`
- Body: `[]` (empty array)

**Verification:**
- [ ] Status code is 200
- [ ] Response is empty array

---

#### 3.2 Send Message - Valid

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"text":"Hello, what is Java?"}'
```

**Expected Response:**
- Status: `200 OK`
- Body:
```json
{
  "assistantMessage": {
    "id": 2,
    "role": "assistant",
    "content": "Java is a high-level programming language...",
    "ts": "2025-02-21T10:31:05Z"
  }
}
```

**Verification:**
- [ ] Status code is 200
- [ ] Response contains `assistantMessage`
- [ ] `assistantMessage.role` is "assistant"
- [ ] `assistantMessage.content` is non-empty
- [ ] User message saved (verify with GET messages)

**Note:** Requires valid `GEMINI_API_KEY`. If invalid, expect 500 error with `AI_SERVICE_ERROR`.

---

#### 3.3 Send Message - Empty Text

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"text":""}'
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions text requirement

---

#### 3.4 Send Message - Text Too Long

**Command:**
```bash
# Create 4001 character string
LONG_TEXT=$(python -c "print('x' * 4001)")

curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d "{\"text\":\"$LONG_TEXT\"}"
```

**Expected Response:**
- Status: `400 Bad Request`
- Body contains validation error about length

**Verification:**
- [ ] Status code is 400
- [ ] Error message mentions 4000 character limit

---

#### 3.5 Get Messages - Populated Conversation

**Command:**
```bash
curl -X GET http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "X-User-Id: {userId}"
```

**Expected Response:**
- Status: `200 OK`
- Body: Array of messages ordered by `ts ASC, id ASC`

**Verification:**
- [ ] Status code is 200
- [ ] Response is array with messages
- [ ] Messages ordered chronologically
- [ ] User and assistant messages present

---

#### 3.6 Unauthorized Access

**Command:**
```bash
curl -X GET http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "X-User-Id: 999"
```

**Expected Response:**
- Status: `403 Forbidden`
- Body:
```json
{
  "error": "UNAUTHORIZED",
  "message": "You do not have access to this conversation."
}
```

**Verification:**
- [ ] Status code is 403
- [ ] Error code is `UNAUTHORIZED`

---

## Postman Collection Testing

### Setup Postman Collection

1. **Import Collection:**
   - Open Postman
   - Click "Import"
   - Select `Final_Project/docs/postman/AI_Chat_Backend_API.json`

2. **Import Environment:**
   - Click "Import"
   - Select `Final_Project/docs/postman/AI_Chat_Backend_API_Environment.json`
   - Select "AI Chat Backend API Environment" from dropdown

3. **Verify Environment Variables:**
   - `baseUrl`: `http://localhost:8080/api/v1`
   - `userId`: (will be set automatically)
   - `conversationId`: (will be set automatically)

### Execute Test Suite

#### Option 1: Run Individual Tests

Execute tests in this order:

1. **Authentication Tests:**
   - [ ] 1.1 Signup - Valid
   - [ ] 1.2 Signup - Duplicate Username
   - [ ] 1.3 Signup - Invalid Username (short)
   - [ ] 1.4 Signup - Invalid Username (special chars)
   - [ ] 1.5 Signup - Password Too Short
   - [ ] 1.6 Login - Valid (sets `userId`)
   - [ ] 1.7 Login - Invalid Password
   - [ ] 1.8 Login - Non-existent User

2. **Conversation Tests:**
   - [ ] 2.1 Create - With Title (sets `conversationId`)
   - [ ] 2.2 Create - Without Title
   - [ ] 2.3 Create - Missing Header
   - [ ] 2.4 Create - Invalid User ID
   - [ ] 2.5 List - Valid
   - [ ] 2.6 List - Empty Result
   - [ ] 2.7 Update Title - Valid
   - [ ] 2.8 Update Title - Empty
   - [ ] 2.9 Update Title - Too Long
   - [ ] 2.10 Delete - Valid
   - [ ] 2.11 Delete - Already Deleted

3. **Message Tests:**
   - [ ] 3.1 Get - Empty Conversation
   - [ ] 3.2 Get - Populated Conversation
   - [ ] 3.3 Get - Invalid ID
   - [ ] 3.4 Get - Unauthorized Access
   - [ ] 3.5 Send - Valid
   - [ ] 3.6 Send - Empty Text
   - [ ] 3.7 Send - Too Long
   - [ ] 3.8 Send - Multiple Messages
   - [ ] 3.9 Send - AI Error

4. **Error Scenarios:**
   - [ ] 4.1 Conversation Limit (50)
   - [ ] 4.2 Message Limit (10,000)
   - [ ] 4.3 DB Connection Failure
   - [ ] 4.4 Invalid JSON
   - [ ] 4.5 Missing Request Body

#### Option 2: Run Collection Runner

1. Click on collection "AI Chat Backend API - Phase 4"
2. Click "Run" button
3. Select all tests
4. Click "Run AI Chat Backend API - Phase 4"
5. Review results:
   - [ ] All tests pass (green checkmarks)
   - [ ] No failed assertions
   - [ ] Response times acceptable (< 5 seconds)

### Postman Test Results Checklist

- [ ] **Authentication (8 tests):**
  - [ ] All 8 tests pass
  - [ ] `userId` environment variable set after signup/login

- [ ] **Conversations (11 tests):**
  - [ ] All 11 tests pass
  - [ ] `conversationId` environment variable set after creation

- [ ] **Messages (9 tests):**
  - [ ] All 9 tests pass
  - [ ] AI responses received (if API key valid)

- [ ] **Error Scenarios (5 tests):**
  - [ ] All 5 tests pass
  - [ ] Error responses match expected format

---

## Edge Case Testing

### Test 1: Conversation Limit (50 conversations)

**Setup:**
```bash
# Create 50 conversations for a user
# Use Postman Collection Runner with iterations or script
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"title":"51st Conversation"}'
```

**Expected:**
- Status: `400 Bad Request`
- Error: `LIMIT_EXCEEDED`
- Message: "Maximum 50 conversations allowed per user."

**Verification:**
- [ ] 50th conversation succeeds
- [ ] 51st conversation fails with limit error

---

### Test 2: Message Limit (10,000 messages)

**Setup:**
```bash
# Send 10,000 messages to a conversation
# Use automated script or Postman Collection Runner
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"text":"10,001st message"}'
```

**Expected:**
- Status: `400 Bad Request`
- Error indicates message limit reached

**Verification:**
- [ ] 10,000th message succeeds
- [ ] 10,001st message fails with limit error

---

### Test 3: Database Connection Failure

**Setup:**
```bash
# Stop PostgreSQL service
# Windows:
net stop postgresql-x64-12

# Linux:
sudo systemctl stop postgresql
```

**Test:**
```bash
curl -X GET http://localhost:8080/api/v1/conversations \
  -H "X-User-Id: {userId}"
```

**Expected:**
- Status: `500 Internal Server Error`
- Error: `INTERNAL_ERROR`
- Message: "An unexpected error occurred"
- **No database connection details exposed**

**Verification:**
- [ ] Error response doesn't leak database info
- [ ] Generic error message returned

**Cleanup:**
```bash
# Restart PostgreSQL
# Windows:
net start postgresql-x64-12

# Linux:
sudo systemctl start postgresql
```

---

### Test 4: Gemini API Failure

**Setup:**
```bash
# Set invalid API key
export GEMINI_API_KEY="invalid_key"
# Restart backend server
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"text":"Test message"}'
```

**Expected:**
- Status: `500 Internal Server Error`
- Error: `AI_SERVICE_ERROR`
- Message: "I'm sorry, I couldn't generate a response."

**Verification:**
- [ ] Error code is `AI_SERVICE_ERROR`
- [ ] User-friendly error message
- [ ] No stack traces exposed

**Cleanup:**
```bash
# Restore valid API key
export GEMINI_API_KEY="your_valid_key"
```

---

### Test 5: Invalid JSON Format

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":}'
```

**Expected:**
- Status: `400 Bad Request`
- Error indicates JSON parsing failure

**Verification:**
- [ ] Status code is 400
- [ ] Error message indicates JSON issue

---

### Test 6: Missing Request Body

**Command:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json"
```

**Expected:**
- Status: `400 Bad Request`
- Error indicates missing body

**Verification:**
- [ ] Status code is 400
- [ ] Error message indicates missing body

---

### Test 7: Boundary Values

**Username Tests:**
- [ ] 3 characters (minimum) - should succeed
- [ ] 20 characters (maximum) - should succeed
- [ ] 2 characters (too short) - should fail
- [ ] 21 characters (too long) - should fail

**Password Tests:**
- [ ] 6 characters (minimum) - should succeed
- [ ] 5 characters (too short) - should fail

**Message Tests:**
- [ ] 1 character (minimum) - should succeed
- [ ] 4000 characters (maximum) - should succeed
- [ ] 4001 characters (too long) - should fail

**Title Tests:**
- [ ] 200 characters (maximum) - should succeed
- [ ] 201 characters (too long) - should fail

---

## Integration Testing

### Test Flow: Complete User Journey

1. **Signup:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/signup \
     -H "Content-Type: application/json" \
     -d '{"username":"journey_user","password":"password123"}'
   ```
   - [ ] User created successfully
   - [ ] Save `userId`

2. **Login:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"journey_user","password":"password123"}'
   ```
   - [ ] Login successful
   - [ ] `userId` matches signup

3. **Create Conversation:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/conversations \
     -H "Content-Type: application/json" \
     -H "X-User-Id: {userId}" \
     -d '{"title":"Journey Chat"}'
   ```
   - [ ] Conversation created
   - [ ] Save `conversationId`

4. **Send Multiple Messages:**
   ```bash
   # Send 3-4 messages
   curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
     -H "Content-Type: application/json" \
     -H "X-User-Id: {userId}" \
     -d '{"text":"Message 1"}'
   ```
   - [ ] Each message saved
   - [ ] AI responses received
   - [ ] Conversation history grows

5. **Get Conversation History:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/conversations/{conversationId}/messages \
     -H "X-User-Id: {userId}"
   ```
   - [ ] All messages returned
   - [ ] Messages ordered chronologically
   - [ ] User and assistant messages present

6. **Update Title:**
   ```bash
   curl -X PUT http://localhost:8080/api/v1/conversations/{conversationId}/title \
     -H "Content-Type: application/json" \
     -H "X-User-Id: {userId}" \
     -d '{"title":"Updated Journey Chat"}'
   ```
   - [ ] Title updated successfully

7. **List Conversations:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/conversations \
     -H "X-User-Id: {userId}"
   ```
   - [ ] Conversation appears in list
   - [ ] Updated title shown

8. **Delete Conversation:**
   ```bash
   curl -X DELETE http://localhost:8080/api/v1/conversations/{conversationId} \
     -H "X-User-Id: {userId}"
   ```
   - [ ] Conversation deleted
   - [ ] Not in conversation list
   - [ ] Messages still exist (soft delete)

---

## Verification Checklist

### Code Quality Verification

- [ ] **DRY Compliance:**
  - [ ] No duplicate error response creation (uses `buildErrorResponse`)
  - [ ] No duplicate validation logic (uses `HeaderValidator`, `PathValidator`)
  - [ ] No duplicate DTO mapping (uses `EntityMapper`)
  - [ ] No duplicate logging (centralized in exception handler)

- [ ] **Controller Verification:**
  - [ ] Controllers are thin (only orchestration)
  - [ ] No business logic in controllers
  - [ ] All methods use validation utilities
  - [ ] Proper HTTP status codes returned

- [ ] **Exception Handling:**
  - [ ] All exceptions handled by `GlobalExceptionHandler`
  - [ ] Appropriate HTTP status codes
  - [ ] Error responses match LLD format
  - [ ] No stack traces exposed to clients

### Functional Verification

- [ ] **Authentication:**
  - [ ] Signup creates user with hashed password
  - [ ] Login validates credentials correctly
  - [ ] Duplicate username prevented
  - [ ] Validation errors returned correctly

- [ ] **Conversations:**
  - [ ] Conversations created successfully
  - [ ] User ownership validated
  - [ ] Soft delete works correctly
  - [ ] Limit enforcement (50 conversations)

- [ ] **Messages:**
  - [ ] Messages saved correctly
  - [ ] Linked-list structure maintained
  - [ ] AI responses generated (if API key valid)
  - [ ] Message history ordered correctly
  - [ ] Limit enforcement (10,000 messages)

### API Contract Verification

- [ ] **Request Formats:**
  - [ ] All endpoints accept correct request formats
  - [ ] Headers validated correctly
  - [ ] Path variables validated correctly
  - [ ] Request bodies validated correctly

- [ ] **Response Formats:**
  - [ ] All responses match LLD specifications
  - [ ] HTTP status codes match LLD
  - [ ] Error responses match LLD format
  - [ ] Response times acceptable (< 5 seconds)

- [ ] **Error Handling:**
  - [ ] All error codes match LLD
  - [ ] Error messages are user-friendly
  - [ ] No sensitive information leaked
  - [ ] Consistent error format

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: Server Won't Start

**Symptoms:**
- `mvn spring-boot:run` fails
- Port 8080 already in use

**Solutions:**
```bash
# Check if port is in use
# Windows:
netstat -ano | findstr :8080

# Linux:
lsof -i :8080

# Kill process or change port in application.properties
```

---

#### Issue 2: Database Connection Failed

**Symptoms:**
- Error: "Connection refused"
- Error: "FATAL: password authentication failed"

**Solutions:**
```bash
# Verify PostgreSQL is running
pg_isready

# Check database exists
psql -l | grep ai_chat

# Verify credentials in application.properties
# Test connection manually
psql -h localhost -U postgres -d ai_chat
```

---

#### Issue 3: Tests Fail with 500 Errors

**Symptoms:**
- All requests return 500
- Error: "AI_SERVICE_ERROR"

**Solutions:**
```bash
# Check Gemini API key is set
echo $GEMINI_API_KEY

# Verify API key is valid
# Check backend logs for detailed error
```

---

#### Issue 4: Validation Errors Not Caught

**Symptoms:**
- Invalid input returns 500 instead of 400
- No validation error messages

**Solutions:**
- Verify `@Valid` annotations on request DTOs
- Check `GlobalExceptionHandler` has `MethodArgumentNotValidException` handler
- Verify validation annotations have `message` attribute

---

#### Issue 5: Postman Environment Variables Not Set

**Symptoms:**
- Tests fail with "userId is undefined"
- Variables not updating

**Solutions:**
- Verify environment is selected in Postman
- Check post-request scripts save variables correctly
- Manually set variables if needed:
  - `userId`: 1 (or actual user ID)
  - `conversationId`: 1 (or actual conversation ID)

---

#### Issue 6: CORS Errors (if testing from browser)

**Symptoms:**
- CORS policy errors in browser console

**Solutions:**
- Add CORS configuration in `WebConfig.java` if needed
- Or use Postman/curl instead of browser

---

## Test Results Log

### Test Execution Log

**Date:** _______________  
**Tester:** _______________  
**Environment:** _______________

#### Authentication Tests
- [ ] 1.1 Signup - Valid: _______________
- [ ] 1.2 Signup - Duplicate: _______________
- [ ] 1.3 Signup - Invalid Username (short): _______________
- [ ] 1.4 Signup - Invalid Username (special): _______________
- [ ] 1.5 Signup - Password Too Short: _______________
- [ ] 1.6 Login - Valid: _______________
- [ ] 1.7 Login - Invalid Password: _______________
- [ ] 1.8 Login - Non-existent User: _______________

#### Conversation Tests
- [ ] 2.1 Create - With Title: _______________
- [ ] 2.2 Create - Without Title: _______________
- [ ] 2.3 Create - Missing Header: _______________
- [ ] 2.4 Create - Invalid User ID: _______________
- [ ] 2.5 List - Valid: _______________
- [ ] 2.6 List - Empty: _______________
- [ ] 2.7 Update Title - Valid: _______________
- [ ] 2.8 Update Title - Empty: _______________
- [ ] 2.9 Update Title - Too Long: _______________
- [ ] 2.10 Delete - Valid: _______________
- [ ] 2.11 Delete - Already Deleted: _______________

#### Message Tests
- [ ] 3.1 Get - Empty: _______________
- [ ] 3.2 Get - Populated: _______________
- [ ] 3.3 Get - Invalid ID: _______________
- [ ] 3.4 Get - Unauthorized: _______________
- [ ] 3.5 Send - Valid: _______________
- [ ] 3.6 Send - Empty Text: _______________
- [ ] 3.7 Send - Too Long: _______________
- [ ] 3.8 Send - Multiple: _______________
- [ ] 3.9 Send - AI Error: _______________

#### Error Scenarios
- [ ] 4.1 Conversation Limit: _______________
- [ ] 4.2 Message Limit: _______________
- [ ] 4.3 DB Failure: _______________
- [ ] 4.4 Invalid JSON: _______________
- [ ] 4.5 Missing Body: _______________

#### Edge Cases
- [ ] Boundary Values: _______________
- [ ] Integration Flow: _______________

### Issues Found

**Issue #1:**
- Description: _______________
- Severity: _______________
- Status: _______________

**Issue #2:**
- Description: _______________
- Severity: _______________
- Status: _______________

### Overall Test Results

- **Total Tests:** 33+
- **Passed:** _______
- **Failed:** _______
- **Skipped:** _______
- **Pass Rate:** _______%

### Sign-off

- [ ] All critical tests passed
- [ ] No blocking issues found
- [ ] Code ready for next phase
- **Tester Signature:** _______________
- **Date:** _______________

---

## Quick Reference Commands

### Start Backend
```bash
cd Final_Project/aichat-backend
mvn spring-boot:run
```

### Check Server Status
```bash
curl http://localhost:8080/api/v1/auth/login
```

### View Logs
```bash
# Logs appear in console when running with mvn spring-boot:run
# Or check application.log if configured
```

### Database Queries
```bash
# Connect to database
psql ai_chat

# View users
SELECT * FROM app_user;

# View conversations
SELECT * FROM conversation WHERE is_deleted = FALSE;

# View messages
SELECT * FROM message ORDER BY ts ASC LIMIT 10;
```

---

**End of Testing Guide**

This document should be used as a comprehensive reference when testing Phase 4 implementation. Update the Test Results Log section as you complete testing.

