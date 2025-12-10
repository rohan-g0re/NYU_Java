# AI Chat Swing Client

Java Swing desktop client for the AI Chat application. This client connects to the Spring Boot backend API to provide a full-featured chat interface.

## Prerequisites

- JDK 1.8 or higher
- Maven 3.6+
- Backend server running at `http://localhost:8080` (configurable via `config.properties`)

## Building

```bash
mvn clean package
```

This will create an executable JAR file in the `target/` directory.

## Running

### Option 1: Run with Maven
```bash
mvn exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
```

### Option 2: Run the JAR
```bash
java -jar target/aichat-swing-client-1.0.0.jar
```

## Configuration

Edit `src/main/resources/config.properties` to configure:

- **API Base URL**: `api.baseUrl` (default: `http://localhost:8080/api/v1`)
- **API Timeout**: `api.timeout.ms` (default: 30000ms)
- **Window Size**: `ui.window.width` and `ui.window.height`
- **Conversation Panel Width**: `ui.conversation.panel.width`

## Features

- **User Authentication**: Login and signup with validation
- **Conversation Management**: Create, view, and delete conversations
- **Real-time Chat**: Send messages and receive AI responses
- **Message History**: View conversation history with timestamps
- **Modern UI**: Clean, responsive Swing interface

## Project Structure

```
aichat-swing-client/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nyu/aichat/client/
│   │   │       ├── Main.java                    # Entry point
│   │   │       ├── api/
│   │   │       │   ├── ApiClient.java           # HTTP client for backend API
│   │   │       │   └── ApiException.java        # Custom exception
│   │   │       ├── model/
│   │   │       │   ├── UserSession.java         # User session data
│   │   │       │   ├── ConversationView.java   # Conversation model
│   │   │       │   └── MessageView.java         # Message model
│   │   │       ├── ui/
│   │   │       │   ├── LoginFrame.java          # Login/signup window
│   │   │       │   ├── MainChatFrame.java       # Main chat window
│   │   │       │   ├── ConversationPanel.java  # Conversation list panel
│   │   │       │   ├── MessagePanel.java        # Message display panel
│   │   │       │   ├── MessageBubble.java       # Individual message bubble
│   │   │       │   └── InputPanel.java          # Message input panel
│   │   │       └── util/
│   │   │           ├── ConfigLoader.java        # Configuration loader
│   │   │           └── JsonParser.java          # JSON parsing utilities
│   │   └── resources/
│   │       └── config.properties                # Configuration file
│   └── test/
│       └── java/                                # Test classes
```

## API Integration

The client communicates with the backend using the following endpoints:

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/signup` - User registration
- `GET /api/v1/conversations` - List user conversations
- `POST /api/v1/conversations` - Create new conversation
- `GET /api/v1/conversations/{id}/messages` - Get conversation messages
- `POST /api/v1/conversations/{id}/messages` - Send message
- `DELETE /api/v1/conversations/{id}` - Delete conversation

All authenticated endpoints require the `X-User-Id` header.

## Dependencies

- **Gson 2.10.1**: JSON serialization/deserialization
- **JUnit 4.13.2**: Testing framework (test scope)

## Usage

1. **Start the backend server** (see backend README)
2. **Launch the Swing client**
3. **Login or Signup** with your credentials
4. **Create a new conversation** or select an existing one
5. **Start chatting** with the AI assistant

## Troubleshooting

### Connection Errors
- Ensure the backend server is running
- Check `config.properties` for correct API URL
- Verify network connectivity

### Authentication Errors
- Check username/password format
- Ensure backend database is properly configured
- Verify backend authentication endpoints are accessible

### UI Issues
- Ensure Java 8+ is installed
- Check system look and feel compatibility
- Verify Swing components are rendering correctly

## Development

### Building from Source
```bash
git clone <repository>
cd aichat-swing-client
mvn clean install
```

## License

This project is part of the NYU Java course final project.

