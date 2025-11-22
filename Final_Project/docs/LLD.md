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
8. [API Specifications](#8-api-specifications)
9. [Sequence Diagrams](#9-sequence-diagrams)
10. [Threading Model](#10-threading-model)
11. [Error Handling Strategy](#11-error-handling-strategy)
12. [Gemini Integration](#12-gemini-integration)
13. [Linked-List Integrity Rules](#13-linked-list-integrity-rules)
14. [Security Implementation](#14-security-implementation)

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
│              Model: gemini-pro                                  │
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
│   └── aichat-client/                     # Swing Desktop Maven Project
│       ├── pom.xml
│       ├── src/
│       │   └── main/
│       │       └── java/
│       │           └── com/nyu/aichat/client/
│       │               ├── Main.java
│       │               ├── api/
│       │               ├── model/
│       │               ├── ui/
│       │               └── util/
│       ├── resources/
│       │   └── config.properties
│       └── README.md
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
| `role`            | TEXT         | NOT NULL, CHECK (role IN ('user','assistant')) | Message sender role    |
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
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MODEL = "gemini-pro";
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
    private String title;  // Optional, defaults to "New Chat"
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
├── Main.java                          # Entry point
│
├── api/
│   └── ApiClient.java                 # HTTP client wrapper
│
├── model/
│   ├── UserSession.java               # Stores userId + username
│   ├── ConversationView.java          # Conversation data model
│   └── MessageView.java               # Message data model
│
├── ui/
│   ├── LoginFrame.java                # Login/signup window
│   ├── MainChatFrame.java             # Main chat interface
│   ├── ConversationPanel.java         # Left sidebar component
│   ├── MessagePanel.java              # Chat messages display
│   ├── MessageBubble.java             # Individual message UI component
│   └── InputPanel.java                # Message input + send button
│
└── util/
    ├── JsonParser.java                # JSON parsing (Gson wrapper)
    └── ConfigLoader.java              # Loads config.properties
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
    
    public ApiClient();
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
    private String sendGetRequest(String endpoint, Long userId) throws IOException;
    private String sendPostRequest(String endpoint, Long userId, Object body) throws IOException;
    private String sendPutRequest(String endpoint, Long userId, Object body) throws IOException;
    private String sendDeleteRequest(String endpoint, Long userId) throws IOException;
    private HttpURLConnection createConnection(String endpoint, String method, Long userId) throws IOException;
}
```

**Key Features:**
- Base URL configurable via `config.properties`
- All methods throw `ApiException` (checked exception)
- Sets `X-User-Id` header automatically
- Uses `HttpURLConnection` for HTTP calls
- JSON serialization/deserialization via Gson

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

## 8. API Specifications

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
*(title is optional; defaults to "New Chat" if null, "Untitled Chat" if empty)*

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
     │             │             │             │ gemini-pro  │
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

## 10. Threading Model

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

## 11. Error Handling Strategy

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

## 12. Gemini Integration

### 12.1 API Configuration

**Environment Variable:**
```bash
export GEMINI_API_KEY=your_api_key_here
```

**API Endpoint:**
```
https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
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
   - Set `Authorization: Bearer {GEMINI_API_KEY}` header
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

## 13. Linked-List Integrity Rules

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

## 14. Security Implementation

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
- Can be empty (defaults to "Untitled Chat")

---

## 15. Configuration Files

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
```

---

### 15.2 Frontend `config.properties`

```properties
# API Configuration
api.base.url=http://localhost:8080/api/v1

# UI Configuration (optional)
ui.message.max.width=400
ui.message.bubble.padding=10
```

---

## 16. Database Initialization Script

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
    role TEXT CHECK (role IN ('user','assistant')) NOT NULL,
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

## 17. Implementation Checklist

### Phase 1: Backend Setup
- [ ] Create Spring Boot project (Maven)
- [ ] Configure `application.properties`
- [ ] Create database schema (`schema.sql`)
- [ ] Set up PostgreSQL connection
- [ ] Test database connectivity

### Phase 2: Backend Entities & Repositories
- [ ] Create `User` entity
- [ ] Create `Conversation` entity
- [ ] Create `Message` entity
- [ ] Create repository interfaces
- [ ] Test basic CRUD operations

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
- [ ] Test all endpoints with Postman

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

### Phase 7: Integration
- [ ] Connect frontend to backend
- [ ] Test login/signup flow
- [ ] Test conversation creation
- [ ] Test message sending
- [ ] Test Gemini integration
- [ ] Test error handling

### Phase 8: Polish
- [ ] Add loading indicators
- [ ] Improve error messages
- [ ] Test edge cases
- [ ] Write README files
- [ ] Create demo script

---

## 18. Testing Strategy

### 18.1 Backend Testing

**Manual Testing (Postman/curl):**
1. Test signup with valid/invalid usernames
2. Test login with correct/incorrect credentials
3. Test conversation creation (with/without title)
4. Test message sending
5. Test conversation listing
6. Test message history retrieval
7. Test conversation renaming
8. Test conversation deletion (soft delete)
9. Test authorization (accessing other user's conversations)

**Edge Cases:**
- Username already exists
- Password too short
- Empty message content
- Message too long (>4000 chars)
- Conversation limit reached (50)
- Gemini API timeout
- Database connection failure

---

### 18.2 Frontend Testing

**Manual Testing:**
1. Login with valid credentials
2. Signup with new username
3. Create new conversation
4. Send multiple messages
5. Switch between conversations
6. Send message while AI is responding (concurrent)
7. Test network error handling
8. Test empty state (no conversations)
9. Test long messages (word wrap)
10. Restart app and verify persistence

**UI Testing:**
- Verify messages align correctly (user right, assistant left)
- Verify timestamps display
- Verify scrolling works
- Verify input validation (empty message prevention)
- Verify loading indicators appear

---

## 19. Deployment Instructions

### 19.1 Backend Deployment

1. **Prerequisites:**
   - JDK 1.8 installed
   - PostgreSQL installed and running
   - Gemini API key obtained

2. **Database Setup:**
   ```bash
   createdb ai_chat
   psql ai_chat < schema.sql
   ```

3. **Environment Variables:**
   ```bash
   export GEMINI_API_KEY=your_key_here
   ```

4. **Build & Run:**
   ```bash
   cd aichat-backend
   mvn clean package
   java -jar target/aichat-backend-1.0.0.jar
   ```

5. **Verify:**
   - Check logs for "Started AichatApplication"
   - Test: `curl http://localhost:8080/api/v1/auth/login`

---

### 19.2 Frontend Deployment

1. **Prerequisites:**
   - JDK 1.8 installed
   - Backend running on `localhost:8080`

2. **Configuration:**
   - Edit `config.properties` if backend URL differs

3. **Build & Run:**
   ```bash
   cd aichat-client
   mvn clean package
   java -jar target/aichat-client-1.0.0.jar
   ```

4. **Verify:**
   - Login window appears
   - Can connect to backend

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