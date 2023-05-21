# About

This project is a client-server system implemented in Java using TCP sockets. The system allows users to authenticate and participate in a text-based game. The server handles user connections, authentication, team formation, and game management.
 The system uses TCP sockets in Java to allow users to authenticate and play text-based games with each other. The main components of the system are:

- ```Game.java```: Represents a game instance and requires a specified number of connected players to start. It handles the game logic and execution.

- ```Client.java```: Users connect to the system using Java client code. They first authenticate, then enter a queue for the game, and finally play the game until completion.

- ```Server.java```: The server accepts and authenticates user connections. It forms teams of players for the game and assigns a thread from a thread pool to create and manage each game instance.

# Additional Implemented Features

## Fault Tolerance
The implementation includes fault tolerance to handle broken connections when users are queuing and waiting for the game to start. A protocol between the client and server allows clients to resume broken connections without losing their position in the game wait queue. Each player is assigned a token to facilitate this process.

## Concurrency

Concurrency is a crucial aspect of the project. The design adheres to the following goals:

- Eliminate race conditions in accessing shared data structures and synchronization between threads.
- Custom implementations should be used for thread-safe data structures instead of relying on Java's concurrent collection classes from java.util.concurrent.locks.
- Minimize thread overhead by avoiding unnecessary thread creation and termination. The number of threads should be kept to a minimum to scale the system efficiently.

## User Registration and Authentication

The project provides the option for user registration using a sub-protocol. Registration data can be persisted in a file or provided via a file. Authentication can be decoupled from game playing, allowing players to send their credentials once and participate in multiple game instances.

# Setup and execution

This guide provides instructions on how to run the Server.java and Client.java classes of the client-server system.

## Prerequisites
Before running the system, ensure that you have the following:

```
- Java Development Kit (JDK) installed on your machine.
- The compiled .class files for Server.java and Client.java.
```

## Server
To run the server, follow these steps:

1. Open a terminal or command prompt.

2. Navigate to the directory where the Server.class file is located.

Run the following command to start the server:

```bash
java Server <port>
```
Replace <port> with the desired port number for the server to listen on.

## Client
To run the client, follow these steps:

1. Open a terminal or command prompt.
2. Navigate to the directory where the Client.class file is located.
3. Run the following command to start the client:

```bash
java Client <serverIP> <port>  
```