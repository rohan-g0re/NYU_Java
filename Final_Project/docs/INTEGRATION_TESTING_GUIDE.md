# Integration Testing Guide: Swing Client & Spring Boot Backend

## Document Purpose
This guide provides instructions for testing the full integration between the Java Swing Client and the Spring Boot Backend. Use this to verify the end-to-end functionality of the AI Chat application.

**Last Updated:** 2025-11-22
**Phase:** Phase 7 - Integration
**Status:** Ready for Validation

---

## 1. Environment Setup

### Backend Prerequisites
1. **Database**: PostgreSQL running on `localhost:5432`, database `ai_chat` created.
2. **Schema**: Apply `schema.sql` to `ai_chat`.
3. **API Key**: Set `GEMINI_API_KEY` environment variable.
4. **Backend Build**:
   ```bash
   cd Final_Project/aichat-backend
   mvn clean package
   mvn spring-boot:run
   ```
   Verify backend is running at `http://localhost:8080`.

### Client Prerequisites
1. **Configuration**: Ensure `Final_Project/aichat-swing-client/src/main/resources/config.properties` has:
   ```properties
   api.baseUrl=http://localhost:8080/api/v1
   ```
2. **Client Build**:
   ```bash
   cd Final_Project/aichat-swing-client
   mvn clean package
   java -jar target/aichat-swing-client-1.0.0.jar
   ```

---

## 2. Manual Integration Test Cases

### Test Suite A: Authentication

| ID | Test Case | Steps | Expected Result | Pass/Fail |
|----|-----------|-------|-----------------|-----------|
| A1 | **Successful Signup** | 1. Launch Client.<br>2. Check "New user? Sign up".<br>3. Enter valid username & password.<br>4. Click "Sign Up". | Client transitions to Main Chat window. Backend logs show new user creation. | |
| A2 | **Duplicate Signup** | 1. Restart Client.<br>2. Try to sign up with the username from A1. | Error message "Username already exists" displayed in red. | |
| A3 | **Successful Login** | 1. Uncheck "New user".<br>2. Enter credentials from A1.<br>3. Click "Login". | Client transitions to Main Chat window. | |
| A4 | **Invalid Login** | 1. Enter valid username but wrong password. | Error message "Invalid credentials" or similar displayed. | |

### Test Suite B: Conversation Management

| ID | Test Case | Steps | Expected Result | Pass/Fail |
|----|-----------|-------|-----------------|-----------|
| B1 | **Create New Chat** | 1. Log in.<br>2. Click "+ New Chat". | New "New Chat" item appears at top of list. Chat area clears. | |
| B2 | **Load History** | 1. Select an existing chat from the list. | Message history loads in the right panel. | |
| B3 | **Delete Chat** | 1. Right-click a conversation.<br>2. Select "Delete".<br>3. Confirm dialog. | Conversation disappears from list. Backend marks as deleted. | |

### Test Suite C: Messaging Flow

| ID | Test Case | Steps | Expected Result | Pass/Fail |
|----|-----------|-------|-----------------|-----------|
| C1 | **Send User Message** | 1. Select a chat.<br>2. Type "Hello".<br>3. Press Enter or Click Send. | Message appears immediately (blue bubble, right). Input disabled briefly. | |
| C2 | **Receive AI Reply** | 1. Wait for response to C1. | "Sending..." changes to "Send". AI reply appears (gray bubble, left). | |
| C3 | **Context Awareness** | 1. Reply "My name is Bob".<br>2. Ask "What is my name?". | AI replies "Your name is Bob" (verifying context window). | |
| C4 | **Error Handling** | 1. Stop Backend (`Ctrl+C`).<br>2. Try to send a message. | Error popup "Failed to send message: Connection refused/Network error". | |

---

## 3. Troubleshooting Integration Issues

### Common Issues

1. **Client cannot connect (Network Error)**
   - **Check**: Is Backend running on port 8080?
   - **Check**: Does `api.baseUrl` in `config.properties` match the backend URL?
   - **Check**: Are there firewall rules blocking `localhost`?

2. **Login Fails (401/404)**
   - **Check**: Did you run the SQL schema?
   - **Check**: Are you using the correct credentials?

3. **AI Reponse Error (500)**
   - **Check**: Is `GEMINI_API_KEY` set in the **Backend** environment?
   - **Check**: Backend logs for `AiServiceException`.

4. **UI Glitches**
   - **Issue**: Messages don't scroll to bottom.
   - **Fix**: Verify `MessagePanel.scrollToBottom()` is called on EDT.

## 4. Future Automated Tests (To Be Implemented)

- **Integration Tests**: Use `RestAssured` to test backend endpoints with a mock database.
- **UI Tests**: Use `AssertJ Swing` to automate client UI interactions.


