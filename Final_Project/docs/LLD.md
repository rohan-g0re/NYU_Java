Generating the complete LLD document.


[1 tool called]


# Low-Level Design (LLD) Document
## AI Chat Desktop Application

---

## Table of Contents

1. [High-Level Architecture](#1-high-level-architecture)
2. [Project Structure](#2-project-structure)
3. [Database Schema](#3-database-schema)
4. [Backend Package Structure](#4-backend-package-structure)
5. [Backend Class Design](#5-backend-class-design)
6. [Frontend Package Structure](#6-frontend-package-structure)
7. [Frontend Class Design](#7-frontend-class-design)
8. [Client-Backend Integration](#8-client-backend-integration)
9. [API Specifications](#9-api-specifications)
10. [Sequence Diagrams](#10-sequence-diagrams)
11. [Threading Model](#11-threading-model)
12. [Error Handling Strategy](#12-error-handling-strategy)
13. [Gemini Integration](#13-gemini-integration)
14. [Linked-List Integrity Rules](#14-linked-list-integrity-rules)
15. [Security Implementation](#15-security-implementation)
16. [Configuration Files](#16-configuration-files)
17. [Database Initialization Script](#17-database-initialization-script)
18. [Implementation Checklist](#18-implementation-checklist)
19. [Testing Strategy](#19-testing-strategy)
20. [Deployment Instructions](#20-deployment-instructions)
21. [Summary](#21-summary)

---

## 1. High-Level Architecture

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
│  │ AuthController│ │ChatController│  │GeminiClient  │         │
│  │              │  │              │  │              │         │
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
          │ (External API Call)
          ▼
┌─────────────────────────────────────────────────────────────────┐
│              Google Gemini API (HTTPS)                          │
│              Model: gemini-2.5-flash                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Project Structure

```
NYU_Java/
├── Final_Project/
│   ├── aichat-backend/                    # Spring Boot Maven Project
│   │   ├── pom.xml
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/nyu/aichat/
│   │   │   │   │       ├── AichatApplication.java
│   │   │   │   │       ├── config/
│   │   │   │   │       ├── controller/
│   │   │   │   │       ├── dto/
│   │   │   │   │       ├── entity/
│   │   │   │   │       ├── exception/
│   │   │   │   │       ├── repository/
│   │   │   │   │       ├── service/
│   │   │   │   │       └── util/
│   │   │   │   └── resources/
│   │   │   │       ├── application.properties
│   │   │   │       └── db/
│   │   │   │           └── schema.sql
│   │   │   └── test/
│   │   └── README.md
│   │
│   └── aichat-swing-client/               # Swing Desktop Maven Project
│       ├── pom.xml
│       ├── README.md
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/nyu/aichat/client/
│       │   │   │       ├── Main.java
│       │   │   │       ├── api/
│       │   │   │       │   ├── ApiClient.java
│       │   │   │       │   └── ApiException.java
│       │   │   │       ├── model/
│       │   │   │       │   ├── UserSession.java
│       │   │   │       │   ├── ConversationView.java
│       │   │   │       │   └── MessageView.java
│       │   │   │       ├── ui/
│       │   │   │       │   ├── LoginFrame.java
│       │   │   │       │   ├── MainChatFrame.java
│       │   │   │       │   ├── ConversationPanel.java
│       │   │   │       │   ├── MessagePanel.java
│       │   │   │       │   ├── MessageBubble.java
│       │   │   │       │   └── InputPanel.java
│       │   │   │       └── util/
│       │   │   │           ├── ConfigLoader.java
│       │   │   │           └── JsonParser.java
│       │   │   └── resources/
│       │   │       └── config.properties
│       │   └── test/
│       │       └── java/
│       └── target/                         # Maven build output
```

---

## 3. Database Schema

### 3.1 Table: `app_user`

| Column      | Type         | Constraints                    | Description                    |
|-------------|--------------|--------------------------------|--------------------------------|
| `id`        | SERIAL       | PRIMARY KEY                    | Auto-incrementing user ID      |
| `username`  | TEXT         | UNIQUE, NOT NULL               | Username (3-20 chars, alphanumeric + underscore) |
| `pass_hash` | TEXT         | NOT NULL                       | BCrypt hashed password          |
| `created_at`| TIMESTAMPTZ  | DEFAULT now()                  | Account creation timestamp      |

**Indexes:**
- `CREATE UNIQUE INDEX idx_user_username ON app_user(username);`

---

### 3.2 Table: `conversation`

| Column            | Type         | Constraints                    | Description                    |
|-------------------|--------------|--------------------------------|--------------------------------|
| `id`              | SERIAL       | PRIMARY KEY                    | Auto-incrementing conversation ID |
| `user_id`         | INT          | NOT NULL, REFERENCES app_user(id) | Owner of the conversation     |
| `title`           | TEXT         | NOT NULL                       | Conversation title             |
| `created_at`      | TIMESTAMPTZ  | DEFAULT now()                  | Creation timestamp             |
| `head_message_id` | BIGINT       | NULL, REFERENCES message(id)  | First message in linked list   |
| `last_message_id` | BIGINT       | NULL, REFERENCES message(id)  | Last message in linked list    |
| `is_deleted`      | BOOLEAN      | DEFAULT FALSE                  | Soft delete flag               |

**Indexes:**
- `CREATE INDEX idx_conv_user ON conversation(user_id, created_at DESC);`
- `CREATE INDEX idx_conv_deleted ON conversation(user_id, is_deleted) WHERE is_deleted = FALSE;`

**Constraints:**
- Max 50 conversations per user (enforced in application logic)
- Cascade delete disabled (soft delete only)

---

### 3.3 Table: `message`

| Column            | Type         | Constraints                    | Description                    |
|-------------------|--------------|--------------------------------|--------------------------------|
| `id`              | BIGSERIAL    | PRIMARY KEY                    | Auto-incrementing message ID   |
| `conv_id`         | INT          | NOT NULL, REFERENCES conversation(id) | Parent conversation        |
| `role`            | TEXT         | NOT NULL, CHECK (role IN ('USER','ASSISTANT')) | Message sender role (uppercase)    |
| `content`         | TEXT         | NOT NULL                       | Message content (max 4000 chars) |
| `ts`              | TIMESTAMPTZ  | DEFAULT now()                  | Message timestamp              |
| `prev_message_id` | BIGINT       | NULL, REFERENCES message(id)  | Previous message in linked list |
| `next_message_id` | BIGINT       | NULL, REFERENCES message(id)  | Next message in linked list    |

**Indexes:**
- `CREATE INDEX idx_message_conv_ts ON message(conv_id, ts ASC, id ASC);`
- `CREATE INDEX idx_message_prev ON message(conv_id, prev_message_id);`
- `CREATE INDEX idx_message_next ON message(conv_id, next_message_id);`

**Constraints:**
- Max 10,000 messages per conversation (enforced in application logic)
- Content length: 1-4000 characters

---

## 4. Backend Package Structure

```
com.nyu.aichat/
├── AichatApplication.java              # Spring Boot main class
│
├── config/
│   ├── SecurityConfig.java             # BCryptPasswordEncoder bean
│   └── WebConfig.java                  # CORS configuration (if needed)
│
├── controller/
│   ├── AuthController.java             # /api/v1/auth endpoints
│   ├── ChatController.java             # /api/v1/conversations endpoints
│   └── GlobalExceptionHandler.java     # @ControllerAdvice for error handling
│
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── SignupRequest.java
│   │   ├── CreateConversationRequest.java
│   │   ├── SendMessageRequest.java
│   │   └── UpdateTitleRequest.java
│   └── response/
│       ├── LoginResponse.java
│       ├── ConversationDto.java
│       └── MessageDto.java
│
├── entity/
│   ├── User.java                       # Maps to app_user
│   ├── Conversation.java               # Maps to conversation
│   └── Message.java                    # Maps to message
│
├── exception/
│   ├── ApiException.java               # Base exception
│   ├── UserNotFoundException.java
│   ├── ConversationNotFoundException.java
│   ├── UnauthorizedException.java
│   └── ValidationException.java
│
├── repository/
│   ├── UserRepository.java             # JpaRepository<User, Long>
│   ├── ConversationRepository.java      # JpaRepository<Conversation, Long>
│   └── MessageRepository.java          # JpaRepository<Message, Long>
│
├── service/
│   ├── AuthService.java                # Signup, login logic
│   ├── ChatService.java                # Conversation & message management
│   └── GeminiService.java               # Gemini API integration
│
└── util/
    ├── TextCleaner.java                 # Clean Gemini responses
    └── ValidationUtil.java              # Input validation helpers
```

---

## 5. Backend Class Design

### 5.1 Entity Classes

#### `User.java`
```java
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "pass_hash", nullable = false)
    private String passHash;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    // Constructors, getters, setters
}
```

#### `Conversation.java`
```java
@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "head_message_id")
    private Long headMessageId;
    
    @Column(name = "last_message_id")
    private Long lastMessageId;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    // Constructors, getters, setters
}
```

#### `Message.java`
```java
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conv_id", nullable = false)
    private Conversation conversation;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageRole role;  // enum: USER, ASSISTANT
    
    @Column(nullable = false, length = 4000)
    private String content;
    
    @Column(name = "ts")
    private Instant timestamp;
    
    @Column(name = "prev_message_id")
    private Long prevMessageId;
    
    @Column(name = "next_message_id")
    private Long nextMessageId;
    
    // Constructors, getters, setters
}
```

---

### 5.2 Repository Interfaces

#### `UserRepository.java`
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

#### `ConversationRepository.java`
```java
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    Optional<Conversation> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);
    long countByUserIdAndIsDeletedFalse(Long userId);
}
```

#### `MessageRepository.java`
```java
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByTimestampAscIdAsc(Long conversationId);
    List<Message> findTop6ByConversationIdOrderByTimestampDescIdDesc(Long conversationId);
    long countByConversationId(Long conversationId);
}
```

---

### 5.3 Service Classes

#### `AuthService.java`
```java
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public LoginResponse signup(String username, String rawPassword);
    public LoginResponse login(String username, String rawPassword);
    private void validateUsername(String username);
    private void validatePassword(String password);
}
```

**Key Methods:**
- `signup()`: Validates username/password, checks uniqueness, hashes password, creates user
- `login()`: Finds user, verifies password, returns user info

#### `ChatService.java`
```java
@Service
@Transactional
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final GeminiService geminiService;
    
    public ConversationDto createConversation(Long userId, String title);
    public List<ConversationDto> getUserConversations(Long userId);
    public List<MessageDto> getConversationHistory(Long conversationId, Long userId);
    public MessageDto sendUserMessageAndGetAiReply(Long conversationId, Long userId, String userText);
    public void updateConversationTitle(Long conversationId, Long userId, String newTitle);
    public void deleteConversation(Long conversationId, Long userId);
    
    @Transactional
    private Message addMessage(Long conversationId, MessageRole role, String content);
    private void validateConversationOwnership(Long conversationId, Long userId);
    private void enforceLimits(Long userId, Long conversationId);
}
```

**Key Methods:**
- `createConversation()`: Creates new conversation, validates user limit (50)
- `addMessage()`: Maintains linked-list integrity, updates conversation head/tail
- `sendUserMessageAndGetAiReply()`: Saves user message, calls Gemini, saves AI reply
- `getConversationHistory()`: Returns messages ordered by `ts, id` (not linked-list traversal)

#### `GeminiService.java`
```java
@Service
public class GeminiService {
    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    private static final String MODEL = "gemini-2.5-flash";
    private static final int TIMEOUT_SECONDS = 10;
    private static final int CONTEXT_MESSAGES = 6;
    
    public String generateResponse(String userMessage, List<Message> contextMessages);
    private String buildPrompt(String userMessage, List<Message> contextMessages);
    private String callGeminiApi(String prompt);
    private String cleanResponse(String rawResponse);
}
```

**Key Methods:**
- `generateResponse()`: Takes user message + last 6 messages, calls Gemini API
- `cleanResponse()`: Removes `<think>...</think>`, trims whitespace
- `callGeminiApi()`: HTTP client to Gemini API with timeout

---

### 5.4 Controller Classes

#### `AuthController.java`
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody SignupRequest request);
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request);
}
```

#### `ChatController.java`
```java
@RestController
@RequestMapping("/api/v1/conversations")
public class ChatController {
    private final ChatService chatService;
    
    @PostMapping
    public ResponseEntity<ConversationDto> createConversation(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody CreateConversationRequest request
    );
    
    @GetMapping
    public ResponseEntity<List<ConversationDto>> getUserConversations(
        @RequestHeader("X-User-Id") Long userId
    );
    
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId
    );
    
    @PostMapping("/{id}/messages")
    public ResponseEntity<SendMessageResponse> sendMessage(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody SendMessageRequest request
    );
    
    @PutMapping("/{id}/title")
    public ResponseEntity<Void> updateTitle(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody UpdateTitleRequest request
    );
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId
    );
}
```

#### `GlobalExceptionHandler.java`
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex);
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex);
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex);
}
```

---

### 5.5 DTO Classes

#### Request DTOs

**`SignupRequest.java`**
```java
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;
    
    @NotBlank
    @Size(min = 6)
    private String password;
}
```

**`LoginRequest.java`**
```java
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}
```

**`CreateConversationRequest.java`**
```java
public class CreateConversationRequest {
    @Size(max = 200)
    private String title;  // Optional, defaults to serial numbering: "New Chat 1", "New Chat 2", etc.
}
```

**`SendMessageRequest.java`**
```java
public class SendMessageRequest {
    @NotBlank
    @Size(min = 1, max = 4000)
    private String text;
}
```

**`UpdateTitleRequest.java`**
```java
public class UpdateTitleRequest {
    @NotBlank
    @Size(max = 200)
    private String title;
}
```

#### Response DTOs

**`LoginResponse.java`**
```java
public class LoginResponse {
    private Long userId;
    private String username;
}
```

**`ConversationDto.java`**
```java
public class ConversationDto {
    private Long id;
    private String title;
    private Instant createdAt;
}
```

**`MessageDto.java`**
```java
public class MessageDto {
    private Long id;
    private String role;  // "user" or "assistant"
    private String content;
    private Instant ts;
}
```

**`SendMessageResponse.java`**
```java
public class SendMessageResponse {
    private MessageDto assistantMessage;
}
```

**`ErrorResponse.java`**
```java
public class ErrorResponse {
    private String error;      // Error code (e.g., "USER_NOT_FOUND")
    private String message;    // Human-readable message
}
```

---

## 6. Frontend Package Structure

```
com.nyu.aichat.client/
├── Main.java                          # Entry point - launches LoginFrame
│
├── api/
│   ├── ApiClient.java                 # HTTP client wrapper for backend API
│   └── ApiException.java              # Custom exception for API errors
│
├── model/
│   ├── UserSession.java               # Immutable session data (userId + username)
│   ├── ConversationView.java          # Conversation data model (maps from ConversationDto)
│   └── MessageView.java               # Message data model (maps from MessageDto)
│
├── ui/
│   ├── LoginFrame.java                # Login/signup window with validation
│   ├── MainChatFrame.java             # Main chat interface (split pane layout)
│   ├── ConversationPanel.java         # Left sidebar with conversation list
│   ├── MessagePanel.java              # Scrollable chat messages display
│   ├── MessageBubble.java             # Individual message UI component (styled bubbles)
│   └── InputPanel.java                # Message input + send button (Enter key support)
│
└── util/
    ├── ConfigLoader.java              # Loads config.properties (API URL, timeouts, UI settings)
    └── JsonParser.java                # JSON parsing utilities (Gson wrapper with Instant support)
```

---

## 7. Frontend Class Design

### 7.1 API Client

#### `ApiClient.java`
```java
public class ApiClient {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";
    private final String baseUrl;
    private final ExecutorService executorService;
    private final Gson gson;  // Configured with Instant deserializer
    
    public ApiClient();  // Uses ConfigLoader.getApiBaseUrl()
    public ApiClient(String baseUrl);
    
    // Auth endpoints
    public LoginResponse login(String username, String password) throws ApiException;
    public LoginResponse signup(String username, String password) throws ApiException;
    
    // Conversation endpoints
    public List<ConversationView> getConversations(Long userId) throws ApiException;
    public ConversationView createConversation(Long userId, String title) throws ApiException;
    public void updateConversationTitle(Long userId, Long conversationId, String title) throws ApiException;
    public void deleteConversation(Long userId, Long conversationId) throws ApiException;
    
    // Message endpoints
    public List<MessageView> getMessages(Long conversationId, Long userId) throws ApiException;
    public MessageView sendMessage(Long conversationId, Long userId, String text) throws ApiException;
    
    // HTTP helper methods
    private String sendGetRequest(String endpoint, Long userId) throws ApiException;
    private String sendPostRequest(String endpoint, Long userId, Object body) throws ApiException;
    private String sendPutRequest(String endpoint, Long userId, Object body) throws ApiException;
    private String sendDeleteRequest(String endpoint, Long userId) throws ApiException;
    private HttpURLConnection createConnection(String endpoint, String method, Long userId) throws IOException;
    private String readResponse(HttpURLConnection conn) throws IOException;
    private ApiException parseErrorResponse(HttpURLConnection conn, int responseCode) throws IOException;
    
    public void shutdown();  // Shutdown executor service
    
    // Inner class for LoginResponse
    public static class LoginResponse {
        private Long userId;
        private String username;
    }
}
```

**Key Features:**
- Base URL configurable via `config.properties` (defaults to `http://localhost:8080/api/v1`)
- All methods throw `ApiException` (checked exception) with error code and HTTP status
- Sets `X-User-Id` header automatically for authenticated endpoints
- Uses `HttpURLConnection` for HTTP calls (no external HTTP library)
- JSON serialization/deserialization via Gson with custom `Instant` adapter
- Configurable timeout (default 30 seconds)
- Proper error handling: parses backend `ErrorResponse` format `{"error": "CODE", "message": "text"}`
- Handles network errors, HTTP errors, and JSON parsing errors

#### `ApiException.java`
```java
public class ApiException extends Exception {
    private final String errorCode;      // Backend error code (e.g., "USER_NOT_FOUND")
    private final int httpStatus;        // HTTP status code (e.g., 404)
    
    public ApiException(String errorCode, String message, int httpStatus);
    public ApiException(String errorCode, String message);
    
    public String getErrorCode();
    public int getHttpStatus();
}
```

**Error Handling:**
- Network errors: `errorCode = "NETWORK_ERROR"`
- Backend errors: Parsed from `ErrorResponse` JSON
- Unknown errors: `errorCode = "UNKNOWN_ERROR"`

---

### 7.2 Model Classes

#### `UserSession.java`
```java
public class UserSession {
    private final Long userId;
    private final String username;
    
    public UserSession(Long userId, String username);
    // Getters
}
```

#### `ConversationView.java`
```java
public class ConversationView {
    private Long id;
    private String title;
    private Instant createdAt;
    
    // Constructors, getters, setters
}
```

#### `MessageView.java`
```java
public class MessageView {
    private Long id;
    private String role;  // "user" or "assistant"
    private String content;
    private Instant ts;
    
    // Constructors, getters, setters
}
```

---

### 7.3 UI Classes

#### `LoginFrame.java`
```java
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JCheckBox signupCheckbox;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    public LoginFrame();
    private void setupUI();
    private void onLoginClick();
    private void onSignupClick();
    private void handleAuthSuccess(LoginResponse response);
    private void handleAuthError(String errorMessage);
}
```

**UI Layout:**
- Username field (top)
- Password field (middle)
- "New user? Sign up" checkbox (below password)
- Login/Signup button (bottom)
- Error message label (red, below button)

#### `MainChatFrame.java`
```java
public class MainChatFrame extends JFrame {
    private UserSession userSession;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    private JSplitPane mainSplitPane;
    private ConversationPanel conversationPanel;  // Left sidebar
    private MessagePanel messagePanel;            // Right top (messages)
    private InputPanel inputPanel;                // Right bottom (input)
    
    private Long currentConversationId;
    
    public MainChatFrame(UserSession userSession);
    private void setupUI();
    private void loadConversations();
    private void onConversationSelected(Long conversationId);
    private void onSendMessage(String text);
    private void refreshCurrentConversation();
}
```

**UI Layout:**
- `JSplitPane` (horizontal):
  - Left: `ConversationPanel` (200px width)
  - Right: `JPanel` (BorderLayout):
    - Center: `MessagePanel` (scrollable)
    - South: `InputPanel`

#### `ConversationPanel.java`
```java
public class ConversationPanel extends JPanel {
    private JList<ConversationView> conversationList;
    private DefaultListModel<ConversationView> listModel;
    private JButton newChatButton;
    private Consumer<Long> onConversationSelected;
    
    public ConversationPanel(Consumer<Long> onConversationSelected);
    public void setConversations(List<ConversationView> conversations);
    public void addConversation(ConversationView conversation);
    public void removeConversation(Long conversationId);
    private void setupUI();
}
```

#### `MessagePanel.java`
```java
public class MessagePanel extends JScrollPane {
    private JPanel contentPanel;
    private BoxLayout boxLayout;
    private Long currentConversationId;
    
    public MessagePanel();
    public void setMessages(List<MessageView> messages);
    public void addMessage(MessageView message, boolean isUser);
    public void clearMessages();
    public void setCurrentConversation(Long conversationId);
    private MessageBubble createMessageBubble(MessageView message, boolean isUser);
}
```

#### `MessageBubble.java`
```java
public class MessageBubble extends JPanel {
    private JLabel contentLabel;
    private JLabel timestampLabel;
    private boolean isUserMessage;
    
    public MessageBubble(String content, Instant timestamp, boolean isUserMessage);
    private void setupUI();
}
```

**Styling:**
- User messages: Right-aligned, blue background
- Assistant messages: Left-aligned, gray background
- Max width: 400px
- Word wrap enabled
- Timestamp below content (smaller font)

#### `InputPanel.java`
```java
public class InputPanel extends JPanel {
    private JTextArea messageTextArea;
    private JButton sendButton;
    private Consumer<String> onSendMessage;
    private boolean isWaitingForResponse;
    
    public InputPanel(Consumer<String> onSendMessage);
    private void setupUI();
    private void onSendClick();
    private void setWaitingForResponse(boolean waiting);
}
```

---

## 8. Client-Backend Integration

### 8.1 Integration Architecture

**Communication Flow:**
```
Swing Client (Java)
    │
    │ HTTP/REST (JSON)
    │ X-User-Id Header
    ▼
Spring Boot Backend (Java)
    │
    │ JDBC
    ▼
PostgreSQL Database
```

**Key Integration Points:**

1. **Authentication:**
   - Client sends username/password to `/api/v1/auth/login` or `/api/v1/auth/signup`
   - Backend validates credentials and returns `LoginResponse` with `userId` and `username`
   - Client stores `UserSession` (immutable) for subsequent requests
   - No session tokens - uses `X-User-Id` header for authentication

2. **API Communication:**
   - All API calls use JSON for request/response bodies
   - Content-Type: `application/json`
   - Accept: `application/json`
   - Authenticated endpoints require `X-User-Id: {userId}` header
   - Base URL configurable via `config.properties`

3. **Data Mapping:**
   - Backend DTOs → Client View Models:
     - `LoginResponse` → `UserSession`
     - `ConversationDto` → `ConversationView`
     - `MessageDto` → `MessageView`
     - `SendMessageResponse.assistantMessage` → `MessageView`
   - Field names match exactly (Gson handles mapping)
   - `Instant` fields serialized as ISO-8601 strings, deserialized with custom adapter

4. **Error Handling:**
   - Backend returns `ErrorResponse`: `{"error": "CODE", "message": "text"}`
   - Client parses error response and creates `ApiException`
   - UI displays error messages via `JOptionPane` or error labels
   - Network errors handled separately (connection refused, timeout)

### 8.2 Request/Response Flow Examples

#### Login Request Flow:
```
LoginFrame.onLoginClick()
  → ExecutorService.execute()
    → ApiClient.login(username, password)
      → HTTP POST /api/v1/auth/login
        → Backend: AuthController.login()
          → Backend: AuthService.login()
            → Database: UserRepository.findByUsername()
            → BCrypt: passwordEncoder.matches()
          → Returns: LoginResponse
      → Client: Parse JSON to LoginResponse
    → SwingUtilities.invokeLater()
      → LoginFrame.handleAuthSuccess()
        → Create UserSession
        → Dispose LoginFrame
        → Open MainChatFrame
```

#### Send Message Flow:
```
InputPanel.onSendClick()
  → MainChatFrame.onSendMessage(text)
    → Optimistic Update: Add user message to UI immediately
    → ExecutorService.execute()
      → ApiClient.sendMessage(convId, userId, text)
        → HTTP POST /api/v1/conversations/{id}/messages
          → Backend: ChatController.sendMessage()
            → Backend: ChatService.sendUserMessageAndGetAiReply()
              → Save user message to database
              → GeminiService.generateResponse()
                → HTTP POST to Gemini API
                → Clean response
              → Save assistant message to database
              → Returns: SendMessageResponse
          → Client: Parse JSON, extract assistantMessage
        → SwingUtilities.invokeLater()
          → MessagePanel.addMessage(assistantMessage)
            → Create MessageBubble
            → Add to UI
            → Scroll to bottom
```

### 8.3 Threading Model for Integration

**Client Threading:**
- **EDT (Event Dispatch Thread):** All UI operations
- **Background Threads:** All API calls via `ExecutorService`

**Pattern:**
```java
// In UI component (runs on EDT)
executorService.execute(() -> {
    try {
        // API call (runs on background thread)
        MessageView response = apiClient.sendMessage(convId, userId, text);
        
        // UI update (must run on EDT)
        SwingUtilities.invokeLater(() -> {
            messagePanel.addMessage(response, false);
        });
    } catch (ApiException e) {
        // Error handling (must run on EDT)
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        });
    }
});
```

**Backend Threading:**
- Each HTTP request handled in separate thread (Tomcat thread pool)
- Database operations thread-safe via connection pooling
- Gemini API calls blocking (within HTTP request thread)

### 8.4 Configuration Synchronization

**Backend Configuration:**
- Port: `server.port=8080` (in `application.properties`)
- Database: PostgreSQL connection settings
- Gemini API: `gemini.api.key=your_api_key` (in `application.properties`)

**Client Configuration:**
- API URL: `api.baseUrl=http://localhost:8080/api/v1` (in `config.properties`)
- Must match backend port
- Timeout: `api.timeout.ms=30000` (should be > Gemini timeout)

**Verification:**
1. Start backend: `cd aichat-backend && mvn spring-boot:run`
2. Verify backend: `curl http://localhost:8080/api/v1/auth/login`
3. Start client: `cd aichat-swing-client && mvn exec:java`
4. Test connection: Attempt login (should connect successfully)

### 8.5 Data Flow Diagrams

#### Conversation List Loading:
```
MainChatFrame constructor
  → loadConversations()
    → ExecutorService.execute()
      → ApiClient.getConversations(userId)
        → HTTP GET /api/v1/conversations
          → Backend: ChatController.getUserConversations()
            → Backend: ChatService.getUserConversations()
              → Database: ConversationRepository.findByUserIdAndIsDeletedFalse()
              → Returns: List<ConversationDto>
          → Client: Parse JSON to List<ConversationView>
        → SwingUtilities.invokeLater()
          → ConversationPanel.setConversations()
            → Update JList model
            → Refresh UI
```

#### Message History Loading:
```
ConversationPanel.onConversationSelected(convId)
  → MainChatFrame.onConversationSelected(convId)
    → ExecutorService.execute()
      → ApiClient.getMessages(convId, userId)
        → HTTP GET /api/v1/conversations/{id}/messages
          → Backend: ChatController.getMessages()
            → Backend: ChatService.getConversationHistory()
              → Database: MessageRepository.findByConversationIdOrderByTimestampAscIdAsc()
              → Returns: List<MessageDto>
          → Client: Parse JSON to List<MessageView>
        → SwingUtilities.invokeLater()
          → MessagePanel.setMessages()
            → Clear existing messages
            → Create MessageBubble for each message
            → Add to UI
            → Scroll to bottom
```

### 8.6 Error Scenarios and Handling

**Network Errors:**
- Backend offline: `ApiException("NETWORK_ERROR", "Failed to connect to server")`
- Timeout: `ApiException` with timeout message
- Client handling: Show `JOptionPane` with error message

**HTTP Errors:**
- 400 Bad Request: Parse `ErrorResponse`, show validation error
- 401 Unauthorized: Show login error, stay on login screen
- 403 Forbidden: Show access denied, refresh conversation list
- 404 Not Found: Show "Resource not found" message
- 500 Internal Server Error: Show "Server error, please try again"

**Data Errors:**
- Invalid JSON: Catch `JsonSyntaxException`, show parse error
- Missing fields: Gson uses defaults (null for missing fields)
- Type mismatches: Gson throws exception, caught as `ApiException`

---

## 9. API Specifications

### 8.1 Authentication Endpoints

#### `POST /api/v1/auth/signup`

**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "username": "john_doe"
}
```

**Error Responses:**
- `400 Bad Request`: Username already exists or validation failed
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Username must be 3-20 characters and contain only letters, digits, and underscores."
}
```

---

#### `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "username": "john_doe"
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid credentials
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Username or password is incorrect."
}
```

- `404 Not Found`: User doesn't exist
```json
{
  "error": "USER_NOT_FOUND",
  "message": "No user exists with this username."
}
```

---

### 8.2 Conversation Endpoints

#### `POST /api/v1/conversations`

**Headers:**
```
X-User-Id: 1
Content-Type: application/json
```

**Request:**
```json
{
  "title": "My First Chat"
}
```
*(title is optional; if null or empty, generates serial title: "New Chat 1", "New Chat 2", etc. based on user's conversation count)*

**Response (201 Created):**
```json
{
  "id": 5,
  "title": "My First Chat",
  "createdAt": "2025-02-21T10:30:00Z"
}
```

**Error Responses:**
- `400 Bad Request`: User has reached 50 conversation limit
```json
{
  "error": "LIMIT_EXCEEDED",
  "message": "Maximum 50 conversations allowed per user."
}
```

---

#### `GET /api/v1/conversations`

**Headers:**
```
X-User-Id: 1
```

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "title": "My First Chat",
    "createdAt": "2025-02-21T10:30:00Z"
  },
  {
    "id": 3,
    "title": "Python Help",
    "createdAt": "2025-02-20T15:20:00Z"
  }
]
```
*(Ordered by createdAt DESC, excludes soft-deleted conversations)*

---

#### `GET /api/v1/conversations/{id}/messages`

**Headers:**
```
X-User-Id: 1
```

**Response (200 OK):**
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
*(Ordered by ts ASC, id ASC)*

**Error Responses:**
- `403 Forbidden`: Conversation doesn't belong to user
```json
{
  "error": "UNAUTHORIZED",
  "message": "You do not have access to this conversation."
}
```

- `404 Not Found`: Conversation doesn't exist or is deleted
```json
{
  "error": "CONVERSATION_NOT_FOUND",
  "message": "Conversation not found."
}
```

---

#### `POST /api/v1/conversations/{id}/messages`

**Headers:**
```
X-User-Id: 1
Content-Type: application/json
```

**Request:**
```json
{
  "text": "What is Java?"
}
```

**Response (200 OK):**
```json
{
  "assistantMessage": {
    "id": 12,
    "role": "assistant",
    "content": "Java is a high-level, object-oriented programming language...",
    "ts": "2025-02-21T10:32:00Z"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Message too long or empty
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Message must be between 1 and 4000 characters."
}
```

- `500 Internal Server Error`: Gemini API failure
```json
{
  "error": "AI_SERVICE_ERROR",
  "message": "I'm sorry, I couldn't generate a response."
}
```

---

#### `PUT /api/v1/conversations/{id}/title`

**Headers:**
```
X-User-Id: 1
Content-Type: application/json
```

**Request:**
```json
{
  "title": "Updated Title"
}
```

**Response (200 OK):** *(Empty body)*

---

#### `DELETE /api/v1/conversations/{id}`

**Headers:**
```
X-User-Id: 1
```

**Response (200 OK):** *(Empty body)*
*(Soft deletes conversation; messages remain in database)*

---

## 9. Sequence Diagrams

### 9.1 Login Flow

```
┌──────────┐         ┌──────────┐         ┌──────────┐         ┌──────────┐
│LoginFrame│         │ApiClient │         │Backend   │         │Database  │
└────┬─────┘         └────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │                    │
     │ onLoginClick()     │                    │                    │
     │───────────────────>│                    │                    │
     │                    │                    │                    │
     │                    │ POST /api/v1/auth/login                │
     │                    │───────────────────>│                    │
     │                    │                    │                    │
     │                    │                    │ findByUsername()   │
     │                    │                    │───────────────────>│
     │                    │                    │<───────────────────│
     │                    │                    │                    │
     │                    │                    │ passwordEncoder.   │
     │                    │                    │ matches()          │
     │                    │                    │                    │
     │                    │<───────────────────│                    │
     │                    │ 200 OK             │                    │
     │                    │ {userId, username} │                    │
     │                    │                    │                    │
     │<───────────────────│                    │                    │
     │ LoginResponse      │                    │                    │
     │                    │                    │                    │
     │ handleAuthSuccess()                     │                    │
     │ - Store UserSession                     │                    │
     │ - Dispose LoginFrame                    │                    │
     │ - Open MainChatFrame                    │                    │
     │                    │                    │                    │
```

---

### 9.2 Create Conversation Flow

```
┌──────────┐         ┌──────────┐         ┌──────────┐         ┌──────────┐
│MainChat  │         │ApiClient │         │Backend   │         │Database  │
│Frame     │         │          │         │          │         │          │
└────┬─────┘         └────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │                    │
     │ onNewChatClick()    │                    │                    │
     │ - Show title dialog │                    │                    │
     │                    │                    │                    │
     │ POST /api/v1/      │                    │                    │
     │ conversations      │                    │                    │
     │ X-User-Id: 1       │                    │                    │
     │ {title: "..."}     │                    │                    │
     │───────────────────>│                    │                    │
     │                    │                    │                    │
     │                    │                    │ validateUser()      │
     │                    │                    │ checkLimit(50)     │
     │                    │                    │───────────────────>│
     │                    │                    │<───────────────────│
     │                    │                    │                    │
     │                    │                    │ createConversation()│
     │                    │                    │───────────────────>│
     │                    │                    │ INSERT conversation │
     │                    │                    │───────────────────>│
     │                    │                    │<───────────────────│
     │                    │                    │                    │
     │                    │<───────────────────│                    │
     │                    │ 201 Created        │                    │
     │                    │ ConversationDto    │                    │
     │                    │                    │                    │
     │<───────────────────│                    │                    │
     │ ConversationView   │                    │                    │
     │                    │                    │                    │
     │ addToSidebar()     │                    │                    │
     │ onConversation     │                    │                    │
     │ Selected()         │                    │                    │
     │                    │                    │                    │
```

---

### 9.3 Send Message → Get AI Reply Flow

```
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│MainChat  │  │ApiClient │  │Backend   │  │Database  │  │Gemini API│
│Frame     │  │          │  │          │  │          │  │          │
└────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │             │             │             │
     │ onSendClick()│             │             │             │
     │ - Add user   │             │             │             │
     │   msg to UI  │             │             │             │
     │ - Clear input│             │             │             │
     │             │             │             │             │
     │ POST /api/v1/│             │             │             │
     │ conversations│             │             │             │
     │ /{id}/messages│            │             │             │
     │ X-User-Id: 1 │             │             │             │
     │ {text: "..."}│             │             │             │
     │─────────────>│             │             │             │
     │             │             │             │             │
     │             │             │ validateOwnership()        │
     │             │             │─────────────>│             │
     │             │             │<─────────────│             │
     │             │             │             │             │
     │             │             │ addMessage(  │             │
     │             │             │  "user", ...)│             │
     │             │             │─────────────>│             │
     │             │             │ INSERT message│            │
     │             │             │─────────────>│             │
     │             │             │ UPDATE prev  │             │
     │             │             │  message     │             │
     │             │             │─────────────>│             │
     │             │             │ UPDATE conv  │             │
     │             │             │  last_msg_id │             │
     │             │             │─────────────>│             │
     │             │             │<─────────────│             │
     │             │             │             │             │
     │             │             │ getLast6Messages()        │
     │             │             │─────────────>│             │
     │             │             │<─────────────│             │
     │             │             │             │             │
     │             │             │ geminiService.            │
     │             │             │ generateResponse()        │
     │             │             │───────────────────────────>│
     │             │             │             │             │
     │             │             │             │ HTTP POST   │
     │             │             │             │ /v1/models/ │
     │             │             │             │ gemini-2.5-flash │
     │             │             │             │<────────────│
     │             │             │             │ Response    │
     │             │             │<───────────────────────────│
     │             │             │             │             │
     │             │             │ cleanResponse()           │
     │             │             │             │             │
     │             │             │ addMessage(  │             │
     │             │             │  "assistant")│            │
     │             │             │─────────────>│             │
     │             │             │ INSERT message│            │
     │             │             │─────────────>│             │
     │             │             │ UPDATE prev  │             │
     │             │             │  message     │             │
     │             │             │─────────────>│             │
     │             │             │ UPDATE conv  │             │
     │             │             │─────────────>│             │
     │             │             │<─────────────│             │
     │             │             │             │             │
     │             │<────────────│             │             │
     │             │ 200 OK      │             │             │
     │             │ {assistantMessage}        │             │
     │             │             │             │             │
     │<────────────│             │             │             │
     │ MessageView │             │             │             │
     │             │             │             │             │
     │ SwingUtilities.           │             │             │
     │ invokeLater(() =>         │             │             │
     │   addAssistantBubble())   │             │             │
     │             │             │             │             │
```

**Note:** User message is added to UI immediately (optimistic update). AI reply is added when response arrives.

---

## 11. Threading Model

### 10.1 Backend Threading

**Spring Boot Default Thread Pool:**
- Main thread: Application startup
- HTTP request threads: Tomcat thread pool (default: 200 threads)
- Each HTTP request handled in its own thread
- Gemini API calls: Blocking (synchronous) within HTTP request thread
- Timeout: 10 seconds per Gemini call

**Transaction Management:**
- `@Transactional` methods run in the same thread as the HTTP request
- Database operations are thread-safe via connection pooling (HikariCP)

---

### 10.2 Frontend Threading

**Thread Architecture:**
```
Main Thread (Event Dispatch Thread - EDT)
├── UI Rendering
├── Event Handling (button clicks, etc.)
└── SwingUtilities.invokeLater() calls

Background Thread Pool (ExecutorService - 4 threads)
├── HTTP API calls (ApiClient methods)
├── JSON parsing
└── Long-running operations
```

**Thread Safety Rules:**
1. All UI updates must run on EDT via `SwingUtilities.invokeLater()`
2. Background threads never directly modify Swing components
3. `ApiClient` methods run in background threads
4. When API response arrives, wrap UI update in `SwingUtilities.invokeLater()`

**Example Pattern:**
```java
executorService.submit(() -> {
    try {
        MessageView response = apiClient.sendMessage(convId, userId, text);
        SwingUtilities.invokeLater(() -> {
            // Safe to modify UI here
            messagePanel.addMessage(response, false);
        });
    } catch (ApiException e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        });
    }
});
```

**Concurrent Message Handling:**
- Multiple messages can be sent simultaneously
- Each message gets its own background thread
- UI updates are queued via `invokeLater()`
- If user switches conversations, check `currentConversationId` before updating UI

---

## 12. Error Handling Strategy

### 11.1 Backend Error Handling

**Exception Hierarchy:**
```
ApiException (base)
├── UserNotFoundException
├── ConversationNotFoundException
├── UnauthorizedException
├── ValidationException
└── AiServiceException
```

**Error Response Format:**
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable error message"
}
```

**HTTP Status Code Mapping:**
- `400 Bad Request`: Validation errors, bad input
- `401 Unauthorized`: Invalid credentials
- `403 Forbidden`: User doesn't own resource
- `404 Not Found`: Resource doesn't exist
- `500 Internal Server Error`: Server errors, Gemini failures

**Global Exception Handler:**
- `@ControllerAdvice` catches all exceptions
- Converts to standardized `ErrorResponse` JSON
- Logs exceptions for debugging
- Returns appropriate HTTP status code

---

### 11.2 Frontend Error Handling

**Error Types:**
1. Network errors (connection refused, timeout)
2. HTTP errors (4xx, 5xx responses)
3. JSON parsing errors
4. UI errors (null pointer, etc.)

**Error Display:**
- Critical errors: `JOptionPane.showMessageDialog()` (modal popup)
- Minor errors: Red label below input field
- Network errors: Retry once, then show popup

**Error Recovery:**
- Network failures: Show "Server unreachable" message
- 401/403 errors: Logout user, return to login screen
- 500 errors: Show "Server error - please try again"
- Gemini failures: Show "AI couldn't respond" message in chat

---

## 13. Gemini Integration

### 12.1 API Configuration

**Configuration:**
Edit `application.properties`:
```properties
gemini.api.key=your_api_key_here
```

**API Endpoint:**
```
https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
```

**Request Format:**
```json
{
  "contents": [
    {
      "parts": [
        {"text": "Previous user message 1"},
        {"text": "Previous assistant reply 1"},
        {"text": "Previous user message 2"},
        {"text": "Previous assistant reply 2"},
        {"text": "Current user message"}
      ]
    }
  ]
}
```

**Response Format:**
```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {"text": "AI response text here..."}
        ]
      }
    }
  ]
}
```

---

### 12.2 Implementation Steps

1. **Build Context:**
   - Fetch last 6 messages from database (ordered by timestamp)
   - Format as alternating user/assistant messages
   - Add current user message at end

2. **Call API:**
   - Create HTTP POST request to Gemini endpoint
   - API key passed as query parameter: `?key={geminiApiKey}`
   - Set timeout to 10 seconds
   - Send JSON request body

3. **Parse Response:**
   - Extract `text` from `candidates[0].content.parts[0].text`
   - Handle empty/null responses

4. **Clean Response:**
   - Remove `<think>...</think>` blocks (regex)
   - Remove empty lines
   - Trim leading/trailing whitespace

5. **Save to Database:**
   - Create new `Message` entity with role="assistant"
   - Link to conversation via linked-list structure
   - Return `MessageDto` to controller

**Error Handling:**
- API timeout: Return "I'm sorry, I couldn't generate a response."
- API error (4xx/5xx): Log error, return generic failure message
- Empty response: Return "I'm sorry, I couldn't generate a response."

---

## 14. Linked-List Integrity Rules

### 13.1 Message Insertion Algorithm

**When adding a new message:**

1. **Load conversation:**
   - Fetch `Conversation` entity by ID
   - Read `lastMessageId` (may be null if first message)

2. **Create new message:**
   - Set `prevMessageId = conversation.lastMessageId`
   - Set `nextMessageId = null`
   - Set `role`, `content`, `timestamp`
   - Save message (gets auto-generated `id`)

3. **Update previous message (if exists):**
   - If `lastMessageId != null`:
     - Load message with `id = lastMessageId`
     - Set its `nextMessageId = newMessage.id`
     - Save previous message

4. **Update conversation:**
   - If `conversation.headMessageId == null`:
     - Set `conversation.headMessageId = newMessage.id` (first message)
   - Always set `conversation.lastMessageId = newMessage.id`
   - Save conversation

**Transaction Boundary:**
- All steps wrapped in `@Transactional` method
- If any step fails, entire transaction rolls back
- Ensures linked-list remains consistent

---

### 13.2 Message Retrieval

**API returns messages ordered by timestamp, NOT by linked-list traversal:**

```sql
SELECT * FROM message 
WHERE conv_id = ? 
ORDER BY ts ASC, id ASC;
```

**Why:**
- Linked-list structure is for data integrity and academic demonstration
- Timestamp ordering is simpler and more reliable for UI rendering
- Avoids complex recursive SQL queries

**Linked-list is maintained for:**
- Data structure demonstration
- Potential future features (message deletion, reordering)
- Database design complexity (shows advanced understanding)

---

### 13.3 Soft Delete Impact

**When conversation is soft-deleted:**
- `conversation.is_deleted = true`
- Messages remain in database with intact linked-list
- No cascade deletion
- Linked-list pointers remain valid

**Benefits:**
- No broken links
- Data recovery possible
- Simpler implementation
- No transaction complexity

---

## 15. Security Implementation

### 14.1 Password Security

**Hashing Algorithm:**
- BCrypt with strength 10 (default)
- Salt automatically generated per password
- One-way hashing (cannot be reversed)

**Implementation:**
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode(rawPassword);
boolean matches = encoder.matches(rawPassword, hash);
```

**Password Requirements:**
- Minimum length: 6 characters
- No complexity requirements (for simplicity)
- Stored as `TEXT` in database (BCrypt hashes are ~60 chars)

---

### 14.2 Authentication

**Header-Based Authentication:**
- Custom header: `X-User-Id: {userId}`
- Sent with every authenticated request
- Backend validates:
  1. User exists
  2. Resource belongs to user (for conversation/message operations)

**Authorization Checks:**
```java
// In ChatController methods
@GetMapping("/{id}/messages")
public ResponseEntity<List<MessageDto>> getMessages(
    @PathVariable Long id,
    @RequestHeader("X-User-Id") Long userId
) {
    // Service validates ownership
    chatService.validateConversationOwnership(id, userId);
    // ... rest of method
}
```

**Limitations:**
- No token expiration
- No session management
- User ID can be spoofed (acceptable for local project)
- For production, would use JWT tokens

---

### 14.3 Input Validation

**Username Validation:**
- Pattern: `^[a-zA-Z0-9_]+$` (letters, digits, underscore only)
- Length: 3-20 characters
- Case-insensitive uniqueness check

**Password Validation:**
- Minimum length: 6 characters
- No maximum length (handled by database TEXT type)

**Message Content Validation:**
- Minimum length: 1 character (non-empty)
- Maximum length: 4000 characters
- Trimmed before validation

**Conversation Title Validation:**
- Maximum length: 200 characters
- Can be empty (defaults to serial numbering: "New Chat 1", "New Chat 2", etc.)

---

## 16. Configuration Files

### 15.1 Backend `application.properties`

```properties
# Server Configuration
server.port=8080
spring.application.name=aichat-backend

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_chat
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Logging
logging.level.com.nyu.aichat=INFO
logging.level.org.springframework.web=INFO

# Gemini API Configuration
gemini.api.key=your_api_key_here
```

---

### 15.2 Frontend `config.properties`

**File:** `src/main/resources/config.properties`

```properties
# API Configuration
api.baseUrl=http://localhost:8080/api/v1
api.timeout.ms=30000

# UI Configuration
ui.window.width=1200
ui.window.height=800
ui.conversation.panel.width=250
```

**Configuration Details:**
- `api.baseUrl`: Backend API base URL (must match backend server port)
- `api.timeout.ms`: HTTP request timeout in milliseconds (default: 30000)
- `ui.window.width`: Main window width in pixels (default: 1200)
- `ui.window.height`: Main window height in pixels (default: 800)
- `ui.conversation.panel.width`: Left sidebar width in pixels (default: 250)

**Loading:**
- Loaded via `ConfigLoader` class using `ClassLoader.getResourceAsStream()`
- Defaults provided if file missing or property not found
- Loaded once at class initialization (static block)

---

## 17. Database Initialization Script

### `schema.sql`

```sql
-- Create database (run manually)
-- CREATE DATABASE ai_chat;

-- Create app_user table
CREATE TABLE IF NOT EXISTS app_user (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    pass_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username ON app_user(username);

-- Create conversation table
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
CREATE INDEX IF NOT EXISTS idx_conv_deleted ON conversation(user_id, is_deleted) WHERE is_deleted = FALSE;

-- Create message table
CREATE TABLE IF NOT EXISTS message (
    id BIGSERIAL PRIMARY KEY,
    conv_id INT NOT NULL REFERENCES conversation(id),
    role TEXT CHECK (role IN ('USER','ASSISTANT')) NOT NULL,
    content TEXT NOT NULL,
    ts TIMESTAMPTZ DEFAULT now(),
    prev_message_id BIGINT NULL REFERENCES message(id),
    next_message_id BIGINT NULL REFERENCES message(id)
);

CREATE INDEX IF NOT EXISTS idx_message_conv_ts ON message(conv_id, ts ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_message_prev ON message(conv_id, prev_message_id);
CREATE INDEX IF NOT EXISTS idx_message_next ON message(conv_id, next_message_id);
```

---

## 18. Implementation Checklist

### Phase 1: Backend Setup
- [ ] Create Spring Boot project (Maven)
- [ ] Configure `application.properties`
- [ ] Create database schema (`schema.sql`)
- [ ] Set up PostgreSQL connection

### Phase 2: Backend Entities & Repositories
- [ ] Create `User` entity
- [ ] Create `Conversation` entity
- [ ] Create `Message` entity
- [ ] Create repository interfaces

### Phase 3: Backend Services
- [ ] Implement `AuthService` (signup, login)
- [ ] Implement `ChatService` (conversations, messages)
- [ ] Implement `GeminiService` (API integration)
- [ ] Add input validation
- [ ] Add error handling

### Phase 4: Backend Controllers
- [ ] Implement `AuthController`
- [ ] Implement `ChatController`
- [ ] Implement `GlobalExceptionHandler`

### Phase 5: Frontend Setup
- [ ] Create Swing client project (Maven)
- [ ] Add Gson dependency
- [ ] Create `ApiClient` class
- [ ] Create model classes (`UserSession`, `ConversationView`, `MessageView`)

### Phase 6: Frontend UI
- [ ] Implement `LoginFrame`
- [ ] Implement `MainChatFrame`
- [ ] Implement `ConversationPanel`
- [ ] Implement `MessagePanel`
- [ ] Implement `MessageBubble`
- [ ] Implement `InputPanel`

### Phase 7: Deployment Setup
- [ ] **Backend Setup:**
  - [ ] Start Spring Boot backend server (`mvn spring-boot:run`)
  - [ ] Verify backend is running on `http://localhost:8080`
  - [ ] Ensure database is initialized and accessible
  - [ ] Configure `gemini.api.key` in `application.properties`
  
- [ ] **Client Configuration:**
  - [ ] Verify `config.properties` has correct `api.baseUrl`
  - [ ] Ensure backend URL matches backend server port

### Phase 8: Documentation
- [ ] Write README files
- [ ] Document configuration requirements

---

## 19. Deployment Instructions

### 19.1 Backend Setup

1. **Prerequisites:**
   - JDK 1.8 installed
   - PostgreSQL installed and running
   - Gemini API key obtained

2. **Database Setup:**
   ```bash
   createdb ai_chat
   psql ai_chat < schema.sql
   ```

3. **Configure Gemini API Key:**
   Edit `src/main/resources/application.properties`:
   ```properties
   gemini.api.key=your_key_here
   ```

4. **Build & Run:**
   ```bash
   cd aichat-backend
   mvn clean package
   java -jar target/aichat-backend-1.0.0.jar
   ```

5. **Startup Verification:**
   - Check logs for "Started AichatApplication"
   - Backend should be accessible at `http://localhost:8080`

---

### 19.2 Frontend Setup

1. **Prerequisites:**
   - JDK 1.8 installed
   - Maven 3.6+ installed
   - Backend running on `localhost:8080` (or configured port)

2. **Configuration:**
   - Edit `src/main/resources/config.properties` if backend URL differs:
     ```properties
     api.baseUrl=http://localhost:8080/api/v1
     ```
   - Adjust UI settings if needed (window size, panel width)

3. **Build:**
   ```bash
   cd aichat-swing-client
   mvn clean package
   ```
   This creates `target/aichat-swing-client-1.0.0.jar`

4. **Run:**
   ```bash
   # Option 1: Run JAR directly
   java -jar target/aichat-swing-client-1.0.0.jar
   
   # Option 2: Run with Maven
   mvn exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
   ```

5. **Startup:**
   - Login window should appear
   - Ensure backend is running before launching client

---

## 20. Summary

This LLD provides:

- Architecture: Client-server with REST API
- Database: PostgreSQL with linked-list message structure
- Backend: Spring Boot with JPA, BCrypt, Gemini integration
- Frontend: Java Swing with multithreaded HTTP client
- Security: Password hashing, header-based authentication
- Error handling: Standardized error responses, graceful degradation
- Threading: Background threads for API calls, EDT for UI updates

The design balances:
- Academic requirements (linked-list, multithreading, networking)
- Practical simplicity (timestamp ordering, soft deletes)
- Professional structure (package organization, error handling)
- Implementation feasibility (lean but complete)

Ready for implementation.

---

**Document Version:** 1.0  
**Last Updated:** 2025-02-21  
**Author:** AI Chat Project Team

---

This LLD document is complete and ready for implementation. You can save this as `LLD.md` or convert it to PDF/DOCX for submission.