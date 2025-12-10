# AI Chat Desktop Application - Submission Document

## What the Project Does

This is a **desktop chat application** that allows users to have conversations with an AI assistant powered by Google's Gemini API. The application features:

- **User Authentication**: Secure signup and login with password hashing
- **Multiple Conversations**: Users can create and manage multiple chat conversations
- **AI-Powered Chat**: Real-time conversations with Google Gemini AI
- **Message History**: All conversations and messages are persisted in a PostgreSQL database
- **Modern UI**: Java Swing-based desktop interface with responsive design

## How It Works

### Architecture

The application follows a **client-server architecture**:

1. **Backend (Spring Boot REST API)**
   - Handles user authentication and authorization
   - Manages conversations and messages in PostgreSQL database
   - Integrates with Google Gemini API to generate AI responses
   - Provides RESTful endpoints for all operations

2. **Frontend (Java Swing Client)**
   - Desktop GUI built with Java Swing
   - Communicates with backend via HTTP REST API
   - Uses multithreading to keep UI responsive during API calls
   - Displays conversations, messages, and handles user input

### Key Components

**Backend:**
- `AuthService`: Handles user signup/login with BCrypt password hashing
- `ChatService`: Manages conversations and message flow
- `GeminiService`: Integrates with Google Gemini API for AI responses
- `Message` entities use a linked-list structure (prev/next pointers) for academic demonstration

**Frontend:**
- `LoginFrame`: User authentication interface
- `MainChatFrame`: Main chat window with conversation list and message area
- `MessageBubble`: Displays individual messages with proper word wrapping
- `ApiClient`: Handles all HTTP communication with backend

### Data Flow

1. User signs up/logs in → Backend validates and creates session
2. User creates a conversation → Backend stores in database
3. User sends a message → Backend saves message, calls Gemini API, saves AI response
4. Client displays both messages in the chat interface

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

