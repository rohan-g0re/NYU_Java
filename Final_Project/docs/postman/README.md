# Postman Collection for AI Chat Backend API

This directory contains the Postman collection and environment files for testing the AI Chat Backend API.

## Files

- `AI_Chat_Backend_API.json` - Postman collection with 33+ test cases
- `AI_Chat_Backend_API_Environment.json` - Environment variables for the collection

## Setup Instructions

1. **Import Collection:**
   - Open Postman
   - Click "Import" button
   - Select `AI_Chat_Backend_API.json`
   - Collection will be imported with all test cases

2. **Import Environment:**
   - Click "Import" button
   - Select `AI_Chat_Backend_API_Environment.json`
   - Environment will be created with variables:
     - `baseUrl`: `http://localhost:8080/api/v1`
     - `userId`: (set automatically after signup/login)
     - `conversationId`: (set automatically after creating conversation)

3. **Select Environment:**
   - In Postman, select the "AI Chat Backend API Environment" from the environment dropdown

4. **Start Backend Server:**
   - Ensure the backend is running on `http://localhost:8080`
   - Database should be configured and running
   - Gemini API key should be set (for message tests)

## Test Execution Order

### Recommended Execution Flow:

1. **Authentication Tests:**
   - Run "1.1 Signup - Valid" to create a test user
   - Run "1.6 Login - Valid" to authenticate (sets `userId` automatically)
   - Run other authentication tests as needed

2. **Conversation Tests:**
   - Run "2.1 Create - With Title" to create a conversation (sets `conversationId` automatically)
   - Run other conversation tests

3. **Message Tests:**
   - Run "3.5 Send - Valid" to send messages
   - Run other message tests

4. **Error Scenarios:**
   - Run error scenario tests to verify error handling

### Running All Tests:

Use Postman Collection Runner:
1. Click on the collection
2. Click "Run" button
3. Select tests to run
4. Click "Run AI Chat Backend API - Phase 4"

## Test Categories

### 1. Authentication (8 tests)
- Valid signup/login
- Duplicate username
- Invalid username patterns
- Password validation
- Invalid credentials

### 2. Conversations (11 tests)
- Create with/without title
- List conversations
- Update title
- Delete conversation
- Header validation
- Invalid inputs

### 3. Messages (9 tests)
- Get messages (empty/populated)
- Send valid/invalid messages
- Unauthorized access
- AI service errors
- Multiple messages

### 4. Error Scenarios (5 tests)
- Conversation limit (50)
- Message limit (10,000)
- Database connection failure
- Invalid JSON
- Missing request body

## Environment Variables

The collection uses the following environment variables:

- `baseUrl`: Base URL for the API (default: `http://localhost:8080/api/v1`)
- `userId`: User ID (set automatically after signup/login)
- `conversationId`: Conversation ID (set automatically after creating conversation)

## Notes

- Some tests require specific setup (e.g., 50 conversations for limit test)
- AI service tests require valid `GEMINI_API_KEY` environment variable
- Database failure tests require PostgreSQL to be stopped
- Tests include assertions for status codes, response formats, and data validation

## Troubleshooting

- **Tests failing:** Ensure backend is running and database is accessible
- **Environment variables not set:** Run signup/login tests first
- **AI tests failing:** Check `GEMINI_API_KEY` is set correctly
- **404 errors:** Verify conversation/user IDs are valid

