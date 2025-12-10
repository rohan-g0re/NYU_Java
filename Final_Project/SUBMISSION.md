# AI Chat Desktop Application - Submission Document

## What the Project Does

This is a **desktop chat application** that allows users to have conversations with an AI assistant powered by Google's Gemini API. The application demonstrates advanced Java programming concepts through a full-stack implementation.

### Core Features

- **User Authentication**: Secure signup and login with BCrypt password hashing
- **Multiple Conversations**: Users can create, rename, and delete separate chat threads
- **AI-Powered Chat**: Real-time conversations with Google Gemini AI
- **Persistent Message Storage**: All conversations and messages stored in PostgreSQL with a linked-list structure
- **Non-Blocking UI**: Background threading ensures the interface remains responsive during AI calls
- **Modern Desktop GUI**: Java Swing-based interface with message bubbles, conversation sidebar, and intuitive controls

## How It Works

### Architecture

The application follows a **client-server architecture**:

```
[ Java Swing Client ]
       |  (HTTP REST)
       v
[ Spring Boot Server ]
       |  (JPA)
       v
[ PostgreSQL Database ]

+ Server also calls → Google Gemini API for AI responses
```

1. **Backend (Spring Boot REST API)**
   - Handles user authentication and authorization with BCrypt password hashing
   - Manages conversations and messages in PostgreSQL database
   - Integrates with Google Gemini API to generate AI responses
   - Provides RESTful endpoints for all operations
   - Uses transactional operations to maintain linked-list message integrity

2. **Frontend (Java Swing Client)**
   - Desktop GUI built with Java Swing components
   - Communicates with backend via HTTP REST API using HttpURLConnection
   - Uses ExecutorService thread pool for non-blocking API calls
   - Displays conversations, messages, and handles user input
   - Updates UI on Event Dispatch Thread (EDT) using SwingUtilities.invokeLater

### Key Components

**Backend Services:**
- `AuthService`: User signup/login with BCrypt password hashing
- `ChatService`: Transactional management of conversations and messages
- `GeminiService`: Integration with Google Gemini API, including response cleaning (removes <think> tags)
- `ValidationUtil`: Input validation for usernames, passwords, message content

**Backend Entities:**
- `User`: User accounts with hashed passwords
- `Conversation`: Chat threads with head/tail message pointers
- `Message`: Individual messages with linked-list structure (prev_message_id, next_message_id)

**Frontend Components:**
- `LoginFrame`: User authentication interface with signup option
- `MainChatFrame`: Main window orchestrating all UI panels
- `ConversationPanel`: Sidebar displaying list of conversations with right-click menu
- `MessagePanel`: Scrollable message history with proper alignment
- `InputPanel`: Text area with placeholder and send button
- `MessageBubble`: Custom message display with word wrapping and timestamps
- `ApiClient`: HTTP client for all REST API communication

### Advanced Java Topics Demonstrated

1. **Java GUI (Swing)**
   - Custom components (MessageBubble, PlaceholderTextArea)
   - Layout managers (BoxLayout, FlowLayout, BorderLayout)
   - Event handling and listeners
   - Context menus (JPopupMenu) for conversation actions

2. **Databases & JDBC**
   - PostgreSQL database with complex schema
   - Spring Data JPA for object-relational mapping
   - Custom repository queries
   - Linked-list data structure in database (prev/next pointers, head/tail tracking)

3. **Spring Framework**
   - REST API with Spring Boot
   - Dependency injection and IoC
   - Transactional service methods (@Transactional)
   - Exception handling with @ControllerAdvice
   - Configuration management with @Value

4. **Multithreading**
   - ExecutorService thread pool in Swing client for non-blocking API calls
   - SwingUtilities.invokeLater for safe UI updates from background threads
   - Spring Boot's built-in thread pool for handling concurrent HTTP requests

5. **Networking**
   - HTTP REST communication between client and server
   - JSON serialization/deserialization (Gson for client, Jackson for server)
   - HTTPS calls from server to Google Gemini API
   - Proper connection management and timeouts

6. **Security**
   - BCrypt password hashing (never storing plain text passwords)
   - Input validation and sanitization
   - SQL injection prevention through JPA/prepared statements
   - Error messages that don't leak sensitive information

### Linked-List Message Structure

Messages in each conversation are stored using a **doubly-linked list** structure:

- Each `Message` has `prev_message_id` and `next_message_id` fields
- Each `Conversation` maintains `head_message_id` (first message) and `last_message_id` (most recent message)
- When adding a new message:
  1. Create new message with prev_message_id = current last_message_id
  2. Update previous message's next_message_id to point to new message
  3. Update conversation's last_message_id
  4. All done in a single transaction to maintain integrity

This structure allows efficient traversal of message history and demonstrates advanced data structure implementation in a database context.

### AI Response Processing

When the backend receives a user message:
1. Store user message in database with linked-list pointers
2. Fetch last 6 messages for context
3. Build prompt with system instructions (plain text formatting)
4. Call Gemini API with context
5. Clean response by removing <think> tags and trimming whitespace
6. Store cleaned AI response in database with linked-list pointers
7. Return both messages to client

### Data Flow

1. **Authentication**: User signs up/logs in → Backend validates credentials and creates session
2. **Create Conversation**: User clicks "+ New Chat" → Backend creates conversation with auto-generated title
3. **Send Message**: User types and sends message → Client immediately displays user bubble → Background thread calls API → Server stores user message, generates AI response, stores AI message → Client receives and displays AI bubble
4. **Switch Conversations**: User selects different chat → Client fetches full message history → Displays all messages in order
5. **Rename/Delete**: User right-clicks conversation → Shows context menu → Calls appropriate API endpoint

## How to Run It

### Prerequisites

- Java 8 or higher
- Maven installed and in PATH
- PostgreSQL database running
- Google Gemini API key

### Setup Steps

1. **Database Setup:**
   - Create PostgreSQL database: `CREATE DATABASE ai_chat;`
   - Run schema script: `psql ai_chat < aichat-backend/src/main/resources/db/schema.sql`
   - Update database credentials in `aichat-backend/src/main/resources/application.properties`

2. **Configure Gemini API Key:**
   Edit `aichat-backend/src/main/resources/application.properties` and add:
   ```properties
   gemini.api.key=your_api_key_here
   ```

3. **Start Backend:**
   ```bash
   cd aichat-backend
   mvn spring-boot:run
   ```
   Backend will start on `http://localhost:8080`

4. **Start Client (in a new terminal):**
   ```bash
   cd aichat-swing-client
   mvn compile exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
   ```

### Usage

1. Launch the client application
2. Sign up with a new username and password, or log in with existing credentials
3. Click "+ New Chat" to create a conversation
4. Type a message and click "Send" to chat with the AI
5. Right-click on conversation names to rename or delete them

### Technology Stack

- **Backend**: Java 8, Spring Boot 2.7, Spring Data JPA, PostgreSQL, BCrypt
- **Frontend**: Java 8, Java Swing, Gson
- **AI Integration**: Google Gemini API

