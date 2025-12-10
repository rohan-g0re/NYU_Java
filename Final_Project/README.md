# AI Chat Desktop Application

Java-based desktop chat application with Spring Boot backend and Swing client.

## Setup

### Backend
```bash
cd aichat-backend
mvn spring-boot:run
```

### Client
```bash
cd aichat-swing-client
mvn compile exec:java -Dexec.mainClass="com.nyu.aichat.client.Main"
```