# AI Chat Desktop Application

Complete Java-based desktop application for chatting with an AI assistant, built according to the Low-Level Design (LLD) specification.

## Project Structure

```
Final_Project/
├── aichat-backend/          # Spring Boot REST API Backend
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nyu/aichat/
│   │   │   │   ├── AichatApplication.java
│   │   │   │   ├── config/          # SecurityConfig
│   │   │   │   ├── controller/     # AuthController, ChatController, GlobalExceptionHandler
│   │   │   │   ├── dto/            # Request/Response DTOs
│   │   │   │   ├── entity/         # User, Conversation, Message
│   │   │   │   ├── exception/      # Custom exceptions
│   │   │   │   ├── repository/     # JPA repositories
│   │   │   │   ├── service/        # AuthService, ChatService, GeminiService
│   │   │   │   └── util/           # TextCleaner, ValidationUtil
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/schema.sql
│   │   └── test/
│   └── README.md
│
├── aichat-client/            # Java Swing Desktop Client
│   ├── pom.xml
│   ├── src/
│   │   └── main/
│   │       ├── java/com/nyu/aichat/client/
│   │       │   ├── Main.java
│   │       │   ├── api/            # ApiClient
│   │       │   ├── model/          # UserSession, ConversationView, MessageView
│   │       │   ├── ui/             # LoginFrame, MainChatFrame, etc.
│   │       │   └── util/           # ConfigLoader, JsonParser
│   │       └── resources/
│   │           └── config.properties
│   └── README.md
│
└── docs/
    ├── LLD.md              # Low-Level Design Document
    ├── plan.md              # Implementation Plan
    └── product_idea.md      # Product Proposal
```

## Quick Start

### 1. Backend Setup

```bash
cd aichat-backend

# Create PostgreSQL database
createdb ai_chat
psql ai_chat < src/main/resources/db/schema.sql

# Configure database in application.properties
# Set GEMINI_API_KEY environment variable
export GEMINI_API_KEY=your_api_key_here

# Build and run
mvn clean package
java -jar target/aichat-backend-1.0.0.jar
```

### 2. Client Setup

```bash
cd aichat-client

# Build and run
mvn clean package
java -jar target/aichat-client-1.0.0.jar
```

## Features

- ✅ User authentication (signup/login with BCrypt password hashing)
- ✅ Multiple conversation management
- ✅ Real-time AI chat using Google Gemini API
- ✅ Persistent message history with linked-list structure
- ✅ Modern Swing UI with multithreaded operations
- ✅ RESTful API with proper error handling
- ✅ Soft delete for conversations
- ✅ Input validation and security

## Technology Stack

### Backend
- Java 8
- Spring Boot 2.7.x
- Spring Data JPA
- PostgreSQL
- BCrypt for password hashing
- Google Gemini API

### Frontend
- Java 8
- Java Swing
- Gson for JSON parsing
- HttpURLConnection for HTTP requests

## Architecture

- **Client-Server Architecture**: Swing client communicates with Spring Boot backend via REST API
- **Database**: PostgreSQL with linked-list message structure for academic demonstration
- **Threading**: Background threads for API calls, EDT for UI updates
- **Security**: BCrypt password hashing, header-based authentication

## Documentation

- See `docs/LLD.md` for complete Low-Level Design specification
- See `docs/plan.md` for detailed implementation plan
- Individual README files in each project directory

## Development Status

✅ Complete project structure created
✅ All classes and interfaces implemented
✅ Configuration files in place
⏳ Ready for testing and refinement

## Next Steps

1. Set up PostgreSQL database
2. Configure Gemini API key
3. Test backend endpoints with Postman
4. Run client application and test full flow
5. Refine UI/UX based on testing
6. Add unit tests if required

