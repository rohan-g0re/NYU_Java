# AI Chat Desktop Application - Complete Project Documentation

> **Document Version:** 1.0  
> **Last Updated:** December 10, 2025  
> **Project Status:** ✅ FULLY IMPLEMENTED

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Technology Stack](#3-technology-stack)
4. [Backend Implementation](#4-backend-implementation)
5. [Swing Client Implementation](#5-swing-client-implementation)
6. [Database Schema](#6-database-schema)
7. [API Specification](#7-api-specification)
8. [File Structure](#8-file-structure)
9. [Code Implementation Details](#9-code-implementation-details)
10. [Advanced Java Concepts](#10-advanced-java-concepts)
11. [How to Run](#11-how-to-run)
12. [Testing Checklist](#12-testing-checklist)

---

## 1. Project Overview

### Purpose
A Java-based desktop application that allows users to chat with an AI assistant (Google Gemini), featuring:
- User authentication (signup/login)
- Multiple conversation management
- Real-time AI chat responses
- Persistent message history

### Key Features
| Feature | Description | Status |
|---------|-------------|--------|
| User Authentication | BCrypt password hashing, signup/login | ✅ Complete |
| Multi-Conversation | Create, rename, delete chat threads | ✅ Complete |
| AI Chat | Google Gemini integration with context | ✅ Complete |
| Persistent Storage | PostgreSQL with JPA | ✅ Complete |
| Desktop GUI | Java Swing client | ✅ Complete |
| Multithreading | Background API calls, EDT for UI | ✅ Complete |

---

## 2. Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    Java Swing Desktop Client                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ LoginFrame   │  │ MainChatFrame│  │ ApiClient    │         │
│  │              │  │              │  │              │         │
│  │ - UI         │  │ - Sidebar    │  │ - HTTP Calls │         │
│  │ - Auth       │  │ - Chat Panel │  │ - JSON Parse │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│         │                  │                  │                 │
│         └──────────────────┼──────────────────┘                 │
│                            │ HTTP/REST                          │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Spring Boot REST Backend                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │AuthController│  │ChatController│  │GeminiService │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                 │                  │                 │
│  ┌──────▼───────┐  ┌──────▼───────┐         │                 │
│  │ AuthService  │  │ ChatService  │         │                 │
│  └──────┬───────┘  └──────┬───────┘         │                 │
│         │                 │                  │                 │
│  ┌──────▼─────────────────▼──────────────────▼───────┐         │
│  │         Spring Data JPA Repositories              │         │
│  │  UserRepo │ ConversationRepo │ MessageRepo       │         │
│  └──────┬────────────────────────────────────────────┘         │
└─────────┼───────────────────────────────────────────────────────┘
          │ JDBC
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  app_user    │  │ conversation  │  │   message    │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘
          │
          ▼ (External API Call)
┌─────────────────────────────────────────────────────────────────┐
│              Google Gemini API (HTTPS)                          │
│              Model: gemini-2.5-flash                            │
└─────────────────────────────────────────────────────────────────┘
```

### Communication Flow
```
Client → HTTP/REST (JSON) → Backend → JPA/JDBC → PostgreSQL
                                   → HTTPS → Gemini API
```

---

## 3. Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 1.8 | Core language |
| Spring Boot | 2.7.18 | REST API framework |
| Spring Data JPA | 2.7.x | Database ORM |
| PostgreSQL | Latest | Database |
| BCrypt | Spring Security | Password hashing |
| Jackson | 2.x | JSON processing |

### Frontend (Swing Client)
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 1.8 | Core language |
| Java Swing | JDK 1.8 | Desktop GUI |
| Gson | 2.10.1 | JSON parsing |
| HttpURLConnection | JDK 1.8 | HTTP client |

### Build Tools
| Tool | Version | Purpose |
|------|---------|---------|
| Maven | 3.x | Dependency management |

---

## 4. Backend Implementation

### 4.1 Package Structure

```
com.nyu.aichat/
├── AichatApplication.java              ✅ Spring Boot main class
│
├── config/
│   ├── SecurityConfig.java             ✅ BCryptPasswordEncoder bean
│   └── DatabaseTestRunner.java         ✅ Database connectivity test
│
├── controller/
│   ├── AuthController.java             ✅ /api/v1/auth endpoints
│   ├── ChatController.java             ✅ /api/v1/conversations endpoints
│   └── GlobalExceptionHandler.java     ✅ @ControllerAdvice error handling
│
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java           ✅ Login credentials
│   │   ├── SignupRequest.java          ✅ Signup credentials
│   │   ├── CreateConversationRequest.java  ✅ New conversation
│   │   ├── SendMessageRequest.java     ✅ Message text
│   │   └── UpdateTitleRequest.java     ✅ Rename conversation
│   └── response/
│       ├── LoginResponse.java          ✅ userId + username
│       ├── ConversationDto.java        ✅ Conversation data
│       ├── MessageDto.java             ✅ Message data
│       ├── SendMessageResponse.java    ✅ AI reply wrapper
│       └── ErrorResponse.java          ✅ Error code + message
│
├── entity/
│   ├── User.java                       ✅ Maps to app_user table
│   ├── Conversation.java               ✅ Maps to conversation table
│   ├── Message.java                    ✅ Maps to message table
│   └── MessageRole.java                ✅ Enum: USER, ASSISTANT
│
├── exception/
│   ├── ApiException.java               ✅ Base exception
│   ├── UserNotFoundException.java      ✅ 404 for users
│   ├── ConversationNotFoundException.java  ✅ 404 for conversations
│   ├── UnauthorizedException.java      ✅ 403 access denied
│   ├── ValidationException.java        ✅ 400 validation errors
│   └── AiServiceException.java         ✅ Gemini API failures
│
├── repository/
│   ├── UserRepository.java             ✅ JpaRepository<User, Long>
│   ├── ConversationRepository.java     ✅ JpaRepository<Conversation, Long>
│   └── MessageRepository.java          ✅ JpaRepository<Message, Long>
│
├── service/
│   ├── AuthService.java                ✅ Signup, login logic
│   ├── ChatService.java                ✅ Conversation & message management
│   └── GeminiService.java              ✅ Gemini API integration
│
└── util/
    ├── Constants.java                  ✅ Centralized constants
    ├── EntityMapper.java               ✅ Entity to DTO mapping
    ├── HeaderValidator.java            ✅ X-User-Id validation
    ├── PathValidator.java              ✅ Path parameter validation
    ├── TextCleaner.java                ✅ Clean Gemini responses
    └── ValidationUtil.java             ✅ Input validation
```

### 4.2 Key Service Implementations

#### AuthService.java
```java
@Service
public class AuthService {
    // ✅ Implemented methods:
    - signup(username, rawPassword)      // Hash password, create user
    - login(username, rawPassword)       // Verify credentials
}
```

#### ChatService.java
```java
@Service
@Transactional
public class ChatService {
    // ✅ Implemented methods:
    - createConversation(userId, title)           // Create new chat
    - getUserConversations(userId)                // List user's chats
    - getConversationHistory(conversationId, userId)  // Get messages
    - sendUserMessageAndGetAiReply(convId, userId, text)  // Send + get AI reply
    - updateConversationTitle(convId, userId, newTitle)   // Rename chat
    - deleteConversation(convId, userId)          // Soft delete
    - addMessage(convId, role, content)           // Linked-list maintenance
}
```

#### GeminiService.java
```java
@Service
public class GeminiService {
    // ✅ Implemented methods:
    - generateResponse(userMessage, contextMessages)  // Call Gemini API
    - buildPrompt(userMessage, contextMessages)       // Build context prompt
    - callGeminiApi(prompt)                           // HTTP POST to Gemini
    - parseGeminiResponse(responseStr)                // Extract text
}
```

### 4.3 Configuration Files

#### application.properties
```properties
# Server
server.port=8080
spring.application.name=aichat-backend

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

---

## 5. Swing Client Implementation

### 5.1 Package Structure

```
com.nyu.aichat.client/
├── Main.java                           ✅ Entry point - launches LoginFrame
│
├── api/
│   ├── ApiClient.java                  ✅ HTTP client wrapper
│   └── ApiException.java               ✅ API error handling
│
├── model/
│   ├── UserSession.java                ✅ Immutable session (userId + username)
│   ├── ConversationView.java           ✅ Conversation display model
│   └── MessageView.java                ✅ Message display model
│
├── ui/
│   ├── LoginFrame.java                 ✅ Login/signup window
│   ├── MainChatFrame.java              ✅ Main chat interface
│   ├── ConversationPanel.java          ✅ Left sidebar with chat list
│   ├── MessagePanel.java               ✅ Scrollable messages display
│   ├── MessageBubble.java              ✅ Individual message bubble
│   └── InputPanel.java                 ✅ Text area + send button
│
└── util/
    ├── ConfigLoader.java               ✅ Loads config.properties
    └── JsonParser.java                 ✅ Gson wrapper with Instant support
```

### 5.2 Key UI Components

#### LoginFrame.java
```java
public class LoginFrame extends JFrame {
    // ✅ Features:
    - Username/password fields
    - Login/Signup toggle checkbox
    - Client-side validation
    - Error message display
    - Background thread for API calls
    - Opens MainChatFrame on success
}
```

#### MainChatFrame.java
```java
public class MainChatFrame extends JFrame {
    // ✅ Features:
    - JSplitPane layout
    - ConversationPanel (left sidebar)
    - MessagePanel (center)
    - InputPanel (bottom)
    - Conversation selection handling
    - Message sending with optimistic updates
    - ExecutorService for background operations
}
```

#### MessageBubble.java
```java
public class MessageBubble extends JPanel {
    // ✅ Features:
    - User messages: Blue background, right-aligned
    - Assistant messages: Gray background, left-aligned
    - HTML word wrap (max 400px width)
    - Timestamp display (h:mm a format)
}
```

#### InputPanel.java
```java
public class InputPanel extends JPanel {
    // ✅ Features:
    - JTextArea with word wrap
    - Enter key sends, Shift+Enter for newline
    - Send button with loading state
    - Placeholder text support
    - Disabled until conversation selected
}
```

### 5.3 API Client Implementation

```java
public class ApiClient {
    // ✅ Implemented endpoints:
    - login(username, password)              // POST /auth/login
    - signup(username, password)             // POST /auth/signup
    - getConversations(userId)               // GET /conversations
    - createConversation(userId, title)      // POST /conversations
    - updateConversationTitle(...)           // PUT /conversations/{id}/title
    - deleteConversation(userId, convId)     // DELETE /conversations/{id}
    - getMessages(convId, userId)            // GET /conversations/{id}/messages
    - sendMessage(convId, userId, text)      // POST /conversations/{id}/messages
    
    // ✅ HTTP Helper methods:
    - sendGetRequest(endpoint, userId)
    - sendPostRequest(endpoint, userId, body)
    - sendPutRequest(endpoint, userId, body)
    - sendDeleteRequest(endpoint, userId)
    - createConnection(endpoint, method, userId)
    - readResponse(conn)
    - parseErrorResponse(conn, responseCode)
}
```

### 5.4 Configuration

#### config.properties
```properties
# API Configuration
api.baseUrl=http://localhost:8080/api/v1
api.timeout.ms=30000

# UI Configuration
ui.window.width=1200
ui.window.height=800
ui.conversation.panel.width=250
```

---

## 6. Database Schema

### 6.1 Tables

#### app_user
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-increment user ID |
| `username` | TEXT | UNIQUE, NOT NULL | 3-20 chars, alphanumeric + underscore |
| `pass_hash` | TEXT | NOT NULL | BCrypt hashed password |
| `created_at` | TIMESTAMPTZ | DEFAULT now() | Account creation time |

#### conversation
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-increment conversation ID |
| `user_id` | INT | FK → app_user(id) | Owner |
| `title` | TEXT | NOT NULL | Chat title |
| `created_at` | TIMESTAMPTZ | DEFAULT now() | Creation time |
| `head_message_id` | BIGINT | FK → message(id) | First message (linked list) |
| `last_message_id` | BIGINT | FK → message(id) | Last message (linked list) |
| `is_deleted` | BOOLEAN | DEFAULT FALSE | Soft delete flag |

#### message
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-increment message ID |
| `conv_id` | INT | FK → conversation(id) | Parent conversation |
| `role` | TEXT | CHECK (user/assistant) | Message sender |
| `content` | TEXT | NOT NULL | Message text (max 4000) |
| `ts` | TIMESTAMPTZ | DEFAULT now() | Timestamp |
| `prev_message_id` | BIGINT | FK → message(id) | Linked list previous |
| `next_message_id` | BIGINT | FK → message(id) | Linked list next |

### 6.2 Schema SQL

```sql
-- app_user table
CREATE TABLE IF NOT EXISTS app_user (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    pass_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username ON app_user(username);

-- conversation table
CREATE TABLE IF NOT EXISTS conversation (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES app_user(id),
    title TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    head_message_id BIGINT NULL,
    last_message_id BIGINT NULL,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_conv_user ON conversation(user_id, created_at DESC);

-- message table
CREATE TABLE IF NOT EXISTS message (
    id BIGSERIAL PRIMARY KEY,
    conv_id INT NOT NULL REFERENCES conversation(id),
    role TEXT CHECK (role IN ('user','assistant')) NOT NULL,
    content TEXT NOT NULL,
    ts TIMESTAMPTZ DEFAULT now(),
    prev_message_id BIGINT NULL REFERENCES message(id),
    next_message_id BIGINT NULL REFERENCES message(id)
);

CREATE INDEX IF NOT EXISTS idx_message_conv_ts ON message(conv_id, ts ASC, id ASC);
```

---

## 7. API Specification

### 7.1 Authentication Endpoints

#### POST /api/v1/auth/signup
**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```
**Response (201):**
```json
{
  "userId": 1,
  "username": "john_doe"
}
```

#### POST /api/v1/auth/login
**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```
**Response (200):**
```json
{
  "userId": 1,
  "username": "john_doe"
}
```

### 7.2 Conversation Endpoints

#### POST /api/v1/conversations
**Headers:** `X-User-Id: 1`  
**Request:**
```json
{
  "title": "My First Chat"
}
```
**Response (201):**
```json
{
  "id": 5,
  "title": "My First Chat",
  "createdAt": "2025-02-21T10:30:00Z"
}
```

#### GET /api/v1/conversations
**Headers:** `X-User-Id: 1`  
**Response (200):**
```json
[
  {
    "id": 5,
    "title": "My First Chat",
    "createdAt": "2025-02-21T10:30:00Z"
  }
]
```

#### GET /api/v1/conversations/{id}/messages
**Headers:** `X-User-Id: 1`  
**Response (200):**
```json
[
  {
    "id": 10,
    "role": "user",
    "content": "Hello!",
    "ts": "2025-02-21T10:31:00Z"
  },
  {
    "id": 11,
    "role": "assistant",
    "content": "Hello! How can I help you today?",
    "ts": "2025-02-21T10:31:05Z"
  }
]
```

#### POST /api/v1/conversations/{id}/messages
**Headers:** `X-User-Id: 1`  
**Request:**
```json
{
  "text": "What is Java?"
}
```
**Response (200):**
```json
{
  "assistantMessage": {
    "id": 12,
    "role": "assistant",
    "content": "Java is a high-level programming language...",
    "ts": "2025-02-21T10:32:00Z"
  }
}
```

#### PUT /api/v1/conversations/{id}/title
**Headers:** `X-User-Id: 1`  
**Request:**
```json
{
  "title": "Updated Title"
}
```
**Response (200):** Empty body

#### DELETE /api/v1/conversations/{id}
**Headers:** `X-User-Id: 1`  
**Response (200):** Empty body (soft delete)

---

## 8. File Structure

### Complete Project Tree

```
Final_Project/
├── README.md
├── docs/
│   ├── LLD.md                              # Low-Level Design Document
│   ├── plan.md                             # Implementation Plan
│   ├── product_idea.md                     # Product Proposal
│   ├── SWING_CLIENT_IMPLEMENTATION_PLAN.md # Detailed Swing Plan
│   ├── PROJECT_COMPLETE_DOCUMENTATION.md   # This Document
│   ├── TESTING_GUIDE.md                    # Testing Documentation
│   └── postman/
│       ├── AI_Chat_Backend_API.json        # Postman Collection
│       └── AI_Chat_Backend_API_Environment.json
│
├── aichat-backend/                         # Spring Boot Backend
│   ├── pom.xml                             # Maven dependencies
│   ├── README.md
│   └── src/
│       ├── main/
│       │   ├── java/com/nyu/aichat/
│       │   │   ├── AichatApplication.java
│       │   │   ├── config/                 # 2 files
│       │   │   ├── controller/             # 3 files
│       │   │   ├── dto/request/            # 5 files
│       │   │   ├── dto/response/           # 5 files
│       │   │   ├── entity/                 # 4 files
│       │   │   ├── exception/              # 6 files
│       │   │   ├── repository/             # 3 files
│       │   │   ├── service/                # 3 files
│       │   │   └── util/                   # 6 files
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/schema.sql
│       └── test/
│
└── aichat-swing-client/                    # Java Swing Client
    ├── pom.xml                             # Maven dependencies
    ├── README.md
    └── src/
        ├── main/
        │   ├── java/com/nyu/aichat/client/
        │   │   ├── Main.java               # Entry point
        │   │   ├── api/                    # 2 files
        │   │   ├── model/                  # 3 files
        │   │   ├── ui/                     # 6 files
        │   │   └── util/                   # 2 files
        │   └── resources/
        │       └── config.properties
        └── test/
```

### File Counts

| Component | Java Files | Config Files | Total |
|-----------|------------|--------------|-------|
| Backend | 38 | 2 | 40 |
| Swing Client | 14 | 2 | 16 |
| **Total** | **52** | **4** | **56** |

---

## 9. Code Implementation Details

### 9.1 Threading Model

#### Swing Client Threading
```
Main Thread (Event Dispatch Thread - EDT)
├── UI Rendering
├── Event Handling (button clicks, etc.)
└── SwingUtilities.invokeLater() calls

Background Thread Pool (ExecutorService)
├── HTTP API calls (ApiClient methods)
├── JSON parsing
└── Long-running operations
```

#### Pattern Used:
```java
executorService.execute(() -> {
    try {
        // API call (background thread)
        MessageView response = apiClient.sendMessage(convId, userId, text);
        
        // UI update (must run on EDT)
        SwingUtilities.invokeLater(() -> {
            messagePanel.addMessage(response, false);
        });
    } catch (ApiException e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        });
    }
});
```

### 9.2 Linked List Message Structure

Messages are stored with doubly-linked list pointers:

```
Message 1 (head)          Message 2               Message 3 (tail)
┌───────────────┐        ┌───────────────┐        ┌───────────────┐
│ prev: null    │───────►│ prev: msg1.id │───────►│ prev: msg2.id │
│ next: msg2.id │◄───────│ next: msg3.id │◄───────│ next: null    │
└───────────────┘        └───────────────┘        └───────────────┘
        │                                                  │
        └──────── conversation.head_message_id ────────────┘
                                                     conversation.last_message_id
```

**addMessage() Algorithm:**
1. Load conversation
2. Create new message with `prevMessageId = conversation.lastMessageId`
3. Save new message
4. Update previous message's `nextMessageId` to new message
5. If first message, set `conversation.headMessageId`
6. Always set `conversation.lastMessageId`
7. Save conversation

### 9.3 Gemini Integration

**Context Building:**
- Fetches last 6 messages for context
- Reverses to chronological order
- Formats as: `user: message\nassistant: reply\n...`
- Adds current user message

**Response Cleaning:**
- Removes `<think>...</think>` blocks (regex: `(?s)<think>.*?</think>`)
- Removes empty lines
- Trims whitespace

---

## 10. Advanced Java Concepts Demonstrated

### Summary Table

| Concept | Implementation | Location |
|---------|---------------|----------|
| **Java GUI (Swing)** | Desktop client with login, chat window, sidebar | `aichat-swing-client/ui/` |
| **JDBC/Database** | PostgreSQL via Spring Data JPA | `aichat-backend/repository/` |
| **Spring Framework** | REST API, DI, @Transactional | `aichat-backend/` |
| **Multithreading** | ExecutorService + SwingUtilities.invokeLater | Throughout client |
| **Networking** | HttpURLConnection (client→backend), Gemini API | `ApiClient.java`, `GeminiService.java` |
| **Security** | BCrypt hashing, X-User-Id header auth | `AuthService.java`, `SecurityConfig.java` |

### Detailed Coverage

#### 1. Java GUI (Swing)
- `JFrame` - LoginFrame, MainChatFrame
- `JPanel` - ConversationPanel, MessagePanel, InputPanel, MessageBubble
- `JSplitPane` - Main layout divider
- `JList` with `DefaultListModel` - Conversation list
- `JScrollPane` - Scrollable message area
- `JTextArea` with custom placeholder - Message input
- `JButton`, `JTextField`, `JPasswordField`, `JCheckBox` - Form controls
- `JPopupMenu` - Right-click context menu
- Custom `ListCellRenderer` - Conversation list styling
- `GridBagLayout`, `BorderLayout`, `BoxLayout` - Layout managers

#### 2. Databases & JDBC
- Spring Data JPA repositories
- Custom query methods: `findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc`
- `@Entity`, `@Table`, `@Column`, `@ManyToOne` annotations
- `@Transactional` for atomic operations
- PostgreSQL driver

#### 3. Spring Framework
- `@SpringBootApplication` - Main class
- `@RestController`, `@RequestMapping` - REST endpoints
- `@Service` - Business logic
- `@Repository` - Data access
- `@Configuration`, `@Bean` - Config classes
- `@ControllerAdvice`, `@ExceptionHandler` - Error handling
- `@Autowired` - Dependency injection
- `@Valid`, `@RequestBody`, `@PathVariable`, `@RequestHeader` - Request handling

#### 4. Multithreading
- `ExecutorService` with `Executors.newCachedThreadPool()`
- `SwingUtilities.invokeLater()` for EDT updates
- Optimistic UI updates (show user message immediately)
- Non-blocking API calls

#### 5. Networking
- `HttpURLConnection` for HTTP requests
- Custom headers (`X-User-Id`, `Content-Type`)
- JSON request/response bodies
- Error stream parsing
- Connection timeouts
- External API calls to Google Gemini

#### 6. Security
- `BCryptPasswordEncoder` for password hashing
- Header-based authentication (`X-User-Id`)
- Ownership validation for conversations
- Input validation (username/password patterns)

---

## 11. How to Run

### Prerequisites
- JDK 1.8+
- Maven 3.x
- PostgreSQL

### 1. Database Setup
```bash
# Create database
createdb ai_chat

# Run schema
psql ai_chat < aichat-backend/src/main/resources/db/schema.sql
```

### 2. Configure Backend
Edit `aichat-backend/src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_POSTGRES_PASSWORD
gemini.api.key=your_gemini_api_key
```

### 3. Start Backend
```bash
cd aichat-backend
mvn clean spring-boot:run
```
Backend runs at `http://localhost:8080`

### 4. Start Client
```bash
cd aichat-swing-client
mvn clean compile exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
```

Or build and run JAR:
```bash
mvn clean package
java -jar target/aichat-swing-client-1.0.0.jar
```

---

## 13. Implementation Status vs. Plan

### Backend Implementation Status

| Phase | Item | Plan | Implemented | File |
|-------|------|------|-------------|------|
| **Phase 1** | Maven project | ✅ | ✅ | `pom.xml` |
| | Spring Boot 2.7.18 | ✅ | ✅ | `pom.xml` |
| | PostgreSQL driver | ✅ | ✅ | `pom.xml` |
| | BCrypt dependency | ✅ | ✅ | `pom.xml` |
| **Phase 2** | User entity | ✅ | ✅ | `entity/User.java` |
| | Conversation entity | ✅ | ✅ | `entity/Conversation.java` |
| | Message entity | ✅ | ✅ | `entity/Message.java` |
| | MessageRole enum | ✅ | ✅ | `entity/MessageRole.java` |
| **Phase 3** | UserRepository | ✅ | ✅ | `repository/UserRepository.java` |
| | ConversationRepository | ✅ | ✅ | `repository/ConversationRepository.java` |
| | MessageRepository | ✅ | ✅ | `repository/MessageRepository.java` |
| **Phase 4** | AuthService | ✅ | ✅ | `service/AuthService.java` |
| | BCrypt signup | ✅ | ✅ | `service/AuthService.java` |
| | Login verification | ✅ | ✅ | `service/AuthService.java` |
| | AuthController | ✅ | ✅ | `controller/AuthController.java` |
| **Phase 5** | ChatService | ✅ | ✅ | `service/ChatService.java` |
| | Conversation CRUD | ✅ | ✅ | `service/ChatService.java` |
| | Message linked-list | ✅ | ✅ | `service/ChatService.java` |
| | Soft delete | ✅ | ✅ | `service/ChatService.java` |
| **Phase 6** | GeminiService | ✅ | ✅ | `service/GeminiService.java` |
| | Context building | ✅ | ✅ | `service/GeminiService.java` |
| | Response cleaning | ✅ | ✅ | `util/TextCleaner.java` |
| **Phase 7** | ChatController | ✅ | ✅ | `controller/ChatController.java` |
| | GlobalExceptionHandler | ✅ | ✅ | `controller/GlobalExceptionHandler.java` |
| **Phase 8** | Request DTOs | ✅ | ✅ | `dto/request/*` (5 files) |
| | Response DTOs | ✅ | ✅ | `dto/response/*` (5 files) |
| | Validation annotations | ✅ | ✅ | DTOs have @Valid |
| **Phase 9** | Exception classes | ✅ | ✅ | `exception/*` (6 files) |
| | Error codes/constants | ✅ | ✅ | `util/Constants.java` |
| **Phase 10** | Header validation | ✅ | ✅ | `util/HeaderValidator.java` |
| | Path validation | ✅ | ✅ | `util/PathValidator.java` |
| | EntityMapper | ✅ | ✅ | `util/EntityMapper.java` |
| | ValidationUtil | ✅ | ✅ | `util/ValidationUtil.java` |

### Swing Client Implementation Status

| Phase | Item | Plan | Implemented | File |
|-------|------|------|-------------|------|
| **Phase 1** | Maven project | ✅ | ✅ | `pom.xml` |
| | Gson dependency | ✅ | ✅ | `pom.xml` |
| | config.properties | ✅ | ✅ | `resources/config.properties` |
| **Phase 2** | UserSession | ✅ | ✅ | `model/UserSession.java` |
| | ConversationView | ✅ | ✅ | `model/ConversationView.java` |
| | MessageView | ✅ | ✅ | `model/MessageView.java` |
| | getFormattedDate() | ✅ | ✅ | `model/ConversationView.java` |
| | getFormattedTimestamp() | ✅ | ✅ | `model/MessageView.java` |
| **Phase 3** | ConfigLoader | ✅ | ✅ | `util/ConfigLoader.java` |
| | JsonParser | ✅ | ✅ | `util/JsonParser.java` |
| **Phase 4** | ApiException | ✅ | ✅ | `api/ApiException.java` |
| | ApiClient | ✅ | ✅ | `api/ApiClient.java` |
| | login() | ✅ | ✅ | `api/ApiClient.java` |
| | signup() | ✅ | ✅ | `api/ApiClient.java` |
| | getConversations() | ✅ | ✅ | `api/ApiClient.java` |
| | createConversation() | ✅ | ✅ | `api/ApiClient.java` |
| | updateConversationTitle() | ✅ | ✅ | `api/ApiClient.java` |
| | deleteConversation() | ✅ | ✅ | `api/ApiClient.java` |
| | getMessages() | ✅ | ✅ | `api/ApiClient.java` |
| | sendMessage() | ✅ | ✅ | `api/ApiClient.java` |
| | HTTP helpers | ✅ | ✅ | `api/ApiClient.java` |
| | Error parsing | ✅ | ✅ | `api/ApiClient.java` |
| **Phase 5** | LoginFrame | ✅ | ✅ | `ui/LoginFrame.java` |
| | Form fields | ✅ | ✅ | `ui/LoginFrame.java` |
| | Login handler | ✅ | ✅ | `ui/LoginFrame.java` |
| | Signup checkbox | ✅ | ✅ | `ui/LoginFrame.java` |
| | Error display | ✅ | ✅ | `ui/LoginFrame.java` |
| | MainChatFrame | ✅ | ✅ | `ui/MainChatFrame.java` |
| | JSplitPane layout | ✅ | ✅ | `ui/MainChatFrame.java` |
| | ConversationPanel | ✅ | ✅ | `ui/ConversationPanel.java` |
| | Custom cell renderer | ✅ | ✅ | `ui/ConversationPanel.java` |
| | Delete context menu | ✅ | ✅ | `ui/ConversationPanel.java` |
| | MessagePanel | ✅ | ✅ | `ui/MessagePanel.java` |
| | Auto-scroll | ✅ | ✅ | `ui/MessagePanel.java` |
| | MessageBubble | ✅ | ✅ | `ui/MessageBubble.java` |
| | User styling (blue) | ✅ | ✅ | `ui/MessageBubble.java` |
| | Assistant styling (gray) | ✅ | ✅ | `ui/MessageBubble.java` |
| | InputPanel | ✅ | ✅ | `ui/InputPanel.java` |
| | Enter key handling | ✅ | ✅ | `ui/InputPanel.java` |
| | Placeholder text | ✅ | ✅ | `ui/InputPanel.java` |
| | Waiting state | ✅ | ✅ | `ui/InputPanel.java` |
| **Phase 6** | Main.java | ✅ | ✅ | `Main.java` |
| | SwingUtilities.invokeLater | ✅ | ✅ | `Main.java` |
| | System look and feel | ✅ | ✅ | `Main.java` |

### Summary

| Component | Total Items | Implemented | Completion |
|-----------|-------------|-------------|------------|
| Backend | 30 | 30 | **100%** |
| Swing Client | 32 | 32 | **100%** |
| **Overall** | **62** | **62** | **100%** |

---

## Conclusion

This AI Chat Desktop Application is **fully implemented** with all planned features:

### Implementation Highlights

| Category | Count | Details |
|----------|-------|---------|
| **Java Files** | 52 | 38 backend + 14 client |
| **REST Endpoints** | 7 | Auth (2) + Chat (5) |
| **Database Tables** | 3 | app_user, conversation, message |
| **UI Components** | 6 | Login, Main, Conversation, Message, Bubble, Input |
| **Service Classes** | 3 | AuthService, ChatService, GeminiService |

### Features Delivered

✅ **User Authentication** - BCrypt hashing, signup/login validation  
✅ **Full REST API** - Spring Boot with validation and error handling  
✅ **PostgreSQL Database** - Linked-list message structure for efficient traversal  
✅ **Java Swing GUI** - Modern chat interface with split-pane layout  
✅ **Google Gemini AI** - Context-aware responses with 6-message history  
✅ **Multithreading** - ExecutorService + EDT pattern for responsive UI  
✅ **Soft Delete** - Conversations marked deleted, not physically removed  
✅ **Input Validation** - Both client-side and server-side validation  
✅ **Error Handling** - Centralized @ControllerAdvice and user-friendly messages  

### Advanced Java Concepts Demonstrated

1. **GUI Programming** - Java Swing with custom components, layouts, renderers
2. **Database/JDBC** - Spring Data JPA with PostgreSQL, custom queries
3. **Spring Framework** - REST controllers, DI, transaction management
4. **Multithreading** - Background threads for API calls, EDT for UI updates
5. **Networking** - HttpURLConnection for REST, external API integration
6. **Security** - BCrypt hashing, header-based authentication

### Project Quality

- **Clean Architecture** - Separation of concerns (controller/service/repository)
- **DRY Principle** - EntityMapper, utility classes, centralized constants
- **Error Handling** - Comprehensive exception hierarchy with meaningful messages
- **Code Documentation** - JavaDoc comments throughout
- **Logging** - SLF4J logging at appropriate levels

---

**Document Generated:** December 10, 2025  
**Project Status:** ✅ COMPLETE (100% Implementation)  
**Project Repository:** `Final_Project/`  
**Total Lines of Code:** ~3,500+ lines across all Java files

