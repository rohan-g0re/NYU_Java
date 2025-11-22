# AI Chat Client

Java Swing desktop client for the AI Chat Application.

## Prerequisites

- JDK 1.8 or higher
- Maven 3.6+
- Backend server running on `http://localhost:8080`

## Configuration

Edit `src/main/resources/config.properties` to change the backend URL if needed:

```properties
api.base.url=http://localhost:8080/api/v1
```

## Build and Run

```bash
mvn clean package
java -jar target/aichat-client-1.0.0.jar
```

Or run directly:
```bash
mvn exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
```

## Features

- User authentication (login/signup)
- Multiple conversation management
- Real-time chat with AI assistant
- Message history persistence
- Clean, modern UI

## Usage

1. Start the backend server first
2. Run the client application
3. Login or sign up with a new account
4. Create a new conversation or select an existing one
5. Start chatting with the AI assistant!

