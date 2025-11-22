Here’s the detailed implementation plan, broken down into granular steps. Since the document export failed, you can copy this into Word/Google Docs and save it as a file (I’ll keep the structure “document-like” so it’s ready to paste).

---

# AI Chat Desktop Application – Detailed Implementation Plan

## 1. Project Overview & Goals

Build a Java-based AI chat system consisting of:

* A **Spring Boot backend** (Java 8 compatible) exposing REST APIs for:

  * user authentication,
  * conversation management,
  * message storage (linked structure),
  * AI integration via the **Gemini SDK**.
* A **Java Swing desktop client** for:

  * login,
  * multi-chat management,
  * chatting with AI.

All messages are stored in **PostgreSQL** with a **linked-list style structure per conversation** (prev/next pointers + head/tail per chat) so chat history is easy to reconstruct and also non-trivial from a DB-design point of view.

---

## 2. Tech Stack & Tools

* **Backend**

  1. Java 8 (JDK 1.8)
  2. Spring Boot 2.7.x (Spring Web, Spring Data JPA, Validation)
  3. PostgreSQL + JDBC (through Spring Data)
  4. Gemini Java SDK (or HTTP client to Gemini API)
  5. BCrypt (password hashing)

* **Frontend**

  1. Java Swing (desktop UI)
  2. `HttpURLConnection` or a small HTTP helper

* **Database**

  * PostgreSQL DB `ai_chat` with tables: `app_user`, `conversation`, `message`.

---

## 3. Incremental Implementation Plan

### 3.1 Environment & Project Setup

1. Verify **JDK 1.8** is installed (`java -version`).
2. Install **PostgreSQL**, create database `ai_chat`.
3. Generate a **Spring Boot 2.7.x** project via Spring Initializr with:

   * Spring Web
   * Spring Data JPA
   * PostgreSQL Driver
   * Validation
4. Open the backend project in IntelliJ / VS Code.

   * Run the main class and confirm the application starts with no errors.
5. Create a **separate Java project** for the **Swing client**:

   * Simple `public static void main` entry point.
   * Add `gson` (or similar) if you want to parse JSON.

---

### 3.2 Database Schema & JPA Entities

**Goal:** Linked-list structure for messages per conversation.

1. Create table `app_user`:

   * `id SERIAL PRIMARY KEY`
   * `username TEXT UNIQUE NOT NULL`
   * `pass_hash TEXT NOT NULL`
   * `created_at TIMESTAMPTZ DEFAULT now()`
2. Create table `conversation`:

   * `id SERIAL PRIMARY KEY`
   * `user_id INT NOT NULL REFERENCES app_user(id)`
   * `title TEXT NOT NULL`
   * `created_at TIMESTAMPTZ DEFAULT now()`
   * `head_message_id BIGINT NULL`
   * `last_message_id BIGINT NULL`
3. Create table `message`:

   * `id BIGSERIAL PRIMARY KEY`
   * `conv_id INT NOT NULL REFERENCES conversation(id)`
   * `role TEXT CHECK (role IN ('user','assistant')) NOT NULL`
   * `content TEXT NOT NULL`
   * `ts TIMESTAMPTZ DEFAULT now()`
   * `prev_message_id BIGINT NULL REFERENCES message(id)`
   * `next_message_id BIGINT NULL REFERENCES message(id)`
4. Add indexes:

   * `CREATE INDEX idx_message_conv_ts ON message (conv_id, ts);`
   * Optional: indexes on `(conv_id, prev_message_id)` and `(conv_id, next_message_id)`.
5. In Spring Boot, create **JPA entities**:

   * `User` → `app_user`
   * `Conversation` → `conversation`
   * `Message` → `message`
   * Use `@ManyToOne` from `Conversation` to `User`, and from `Message` to `Conversation`.
   * `head_message_id` and `last_message_id` can be mapped as `Long` fields in `Conversation`.

---

### 3.3 Spring Data Repositories

1. `UserRepo extends JpaRepository<User, Long>`:

   * `Optional<User> findByUsername(String username);`
2. `ConversationRepo extends JpaRepository<Conversation, Long>`:

   * `List<Conversation> findByUserIdOrderByCreatedAtDesc(Long userId);`
3. `MessageRepo extends JpaRepository<Message, Long>`:

   * `List<Message> findByConvIdOrderByTsAsc(Long convId);`
4. Optional: repository method to get **last N messages** for context.

---

### 3.4 Authentication & Security Layer

**Goal:** Simple password hashing; optional JWT later.

1. Add `BCryptPasswordEncoder` bean in a `@Configuration` class.
2. Implement `AuthService`:

   * `signup(username, rawPassword)`:

     * Check if username exists via `UserRepo.findByUsername`.
     * Hash password with `BCryptPasswordEncoder.encode`.
     * Create and save `User`.
   * `login(username, rawPassword)`:

     * Load `User` by username.
     * Compare using `encoder.matches(rawPassword, user.getPassHash())`.
     * If correct, return user (or user info).
3. Create DTOs:

   * `LoginRequest { username, password }`
   * `SignupRequest { username, password }`
   * `LoginResponse { userId, username }` (token optional).
4. Build `AuthController`:

   * `POST /api/signup` → calls `AuthService.signup`.
   * `POST /api/login` → calls `AuthService.login`.
5. Initially, let the Swing client store `userId` and `username` in memory.

   * Later, you can wrap it in a JWT if you want extra marks.

---

### 3.5 Conversation & Message Management

**Goal:** Linked messages per conversation, updated transactionally.

1. Implement `ChatService` (Spring `@Service`):
2. `createConversation(User user, String title)`:

   * Create `Conversation` with user, title.
   * `headMessageId = null`, `lastMessageId = null`.
   * Save with `ConversationRepo`.
3. `getUserConversations(Long userId)`:

   * Use `ConversationRepo.findByUserIdOrderByCreatedAtDesc(userId)`.
4. `getConversationHistory(Long convId)`:

   * Use `MessageRepo.findByConvIdOrderByTsAsc(convId)` to return full ordered list.
5. `@Transactional addMessage(Long convId, String role, String content)`:

   * Load `Conversation` by ID (throw if not found).
   * Read `conversation.lastMessageId`.
   * Create new `Message`:

     * Set `conv`, `role`, `content`, `prevMessageId = lastMessageId`, `nextMessageId = null`.
   * Save the new `Message`.
   * If there *was* a last message:

     * Load that `Message`, set its `nextMessageId` to new message’s `id`, save.
   * If this is the **first** message in conversation:

     * Set `conversation.headMessageId = newId`.
   * Always set `conversation.lastMessageId = newId` and save `conversation`.

---

### 3.6 Gemini Integration & Response Cleaning

1. Create `GeminiClient` (or `AiService`):

   * Uses Gemini SDK to call the model.
   * Method: `String complete(String prompt, List<Message> context)`.
2. Implement a helper method:

   * `String cleanModelText(String raw)`:

     * Remove `<think>...</think>` blocks with regex: `(?s)<think>.*?</think>`.
     * Trim whitespace.
3. In `ChatService`, implement:

   * `sendUserMessageAndGetAiReply(Long convId, String userText)`:

     1. Call `addMessage(convId, "user", userText)` to store user message.
     2. Optionally fetch last N messages for context.
     3. Call `GeminiClient.complete(...)` to get raw AI text.
     4. Run `cleanModelText(raw)` → `cleaned`.
     5. Call `addMessage(convId, "assistant", cleaned)` → store assistant message.
     6. Return the assistant `Message` or a DTO to the controller.

---

### 3.7 REST Controllers (Conversations & Messages)

1. Create `ChatController @RestController @RequestMapping("/api")`.
2. Endpoints:

   * `POST /api/conversations`

     * Input: `userId`, `title`.
     * Uses `ChatService.createConversation`.
   * `GET /api/conversations?userId=...`

     * Returns list of user’s conversations (DTO with id, title, createdAt).
   * `GET /api/conversations/{id}/messages`

     * Returns ordered messages for the conversation (list of DTOs).
   * `POST /api/conversations/{id}/messages`

     * Body: `{ text: "..." }`, plus `userId` or get user from session/token.
     * Calls `ChatService.sendUserMessageAndGetAiReply`.
     * Returns the assistant message (and maybe the user message too).
3. Use DTOs like:

   * `ConversationDto { id, title, createdAt }`
   * `MessageDto { id, role, content, ts }`

---

### 3.8 Swing Client – HTTP Helper & Data Models

1. Create `ApiClient` class in Swing project:

   * Base URL: `http://localhost:8080/api`.
   * Methods:

     * `LoginResponse login(String username, String password)`
     * `List<ConversationView> getConversations(Long userId)`
     * `List<MessageView> getMessages(Long convId)`
     * `MessageView sendMessage(Long convId, Long userId, String text)`
   * Implement using `HttpURLConnection` (set method, headers, write JSON body, read JSON response).
2. Create model classes:

   * `UserSession { Long userId; String username; }`
   * `ConversationView { Long id; String title; Instant createdAt; }`
   * `MessageView { Long id; String role; String content; Instant ts; }`

---

### 3.9 Swing Client – Login UI

1. Implement `LoginFrame`:

   * Fields: username (JTextField), password (JPasswordField).
   * Buttons: **Login**, (optional) **Signup**.
2. On **Login** click:

   * Collect username & password.
   * Use an `ExecutorService` to call `ApiClient.login(...)` on a background thread.
   * If success:

     * Store `UserSession`.
     * Dispose `LoginFrame`, open `MainChatFrame`.
   * If error, show `JOptionPane.showMessageDialog` with error text.

---

### 3.10 Swing Client – Main Chat Window & Layout

1. Implement `MainChatFrame`:

   * Use a `JSplitPane`:

     * Left: `JList<ConversationView>` or `JPanel` with list of buttons.
     * Right:

       * Top: `JScrollPane` with a `JPanel` (BoxLayout Y) for messages.
       * Bottom: `JTextArea` for input + **Send** button.
2. On frame initialization:

   * Call `ApiClient.getConversations(userId)` in a background thread.
   * On completion, populate JList with conversations (using `SwingUtilities.invokeLater`).
3. When user selects a conversation:

   * Clear message panel.
   * Background thread: call `getMessages(convId)`.
   * On completion, update message panel by iterating messages and adding UI components:

     * A custom message bubble `JPanel` aligned left for `user`, right for `assistant`.

---

### 3.11 Sending Messages & Background Threads

1. Create an `ExecutorService` in the client:

   * e.g., `Executors.newFixedThreadPool(4)`.
2. On **Send** button:

   * Read current conversation ID and input text.
   * Immediately append the user message to the message panel (so UI feels instant).
   * Clear the input box.
   * Submit background task:

     1. Call `ApiClient.sendMessage(convId, userId, text)` (POST to backend).
     2. Receive assistant message.
     3. On EDT (`SwingUtilities.invokeLater`), add assistant bubble to message panel.
3. User **can switch chats** while this is happening:

   * If they’re on a different chat when reply arrives, you can:

     * either update silently (DB already has it), or
     * optionally check if the current open chat = convId before adding directly.
   * When they return to the original chat, the **GET /messages** call will always show correct history from DB.

---

### 3.12 Chat Switching & Refresh

1. On conversation selection change:

   * Cancel any “loading indicator” for previous chat if you use one.
   * Clear messages panel.
   * In background:

     * Call `/api/conversations/{id}/messages`.
     * On completion, rebuild message UI in correct order.
2. This design guarantees:

   * No UI blocking.
   * If responses come late, they’re saved in DB and will appear when chat is reopened.

---

### 3.13 Error Handling, Logging, Testing

1. **Backend**

   * Add basic logs in `AuthService`, `ChatService`, and `GeminiClient`.
   * Use `@ControllerAdvice` with an exception handler to turn exceptions into JSON with `error` + `message`.
   * Validate inputs (non-empty username/password, non-empty text).
2. **Testing Backend**

   * Use Postman / curl for:

     * Signup / Login
     * Create conversation
     * Send message
     * List conversations + messages
3. **Client Tests**

   * Run through:

     * Signup → login.
     * Create multiple chats.
     * Send multiple messages per chat.
     * Switch between chats quickly.
     * Restart the app → confirm history is persisted.

---

## 4. Finalization & Documentation

1. Write a **README**:

   * How to configure PostgreSQL and `application.properties`.
   * How to run Spring Boot backend.
   * How to run Swing client.
2. Document:

   * DB schema and linked-list message structure (`head_message_id`, `last_message_id`, `prev_message_id`, `next_message_id`).
   * Gemini response cleaning: strip `<think>...</think>` before storage.
3. Prepare a **demo script**:

   * Login / signup.
   * Create chat.
   * Send messages, show non-blocking UI.
   * Switch chats while a response is pending.
   * Restart and show persisted history.

---

You can drop this whole plan into a `.docx`/`.pdf` and submit it as your implementation strategy. If you want, next I can:

* turn specific sections into **checklist-style TODOs**, or
* start writing the **actual Spring Boot skeleton code** (entities, repos, basic controllers).
