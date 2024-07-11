# JustEatTakeaway Code Challenge

## Table of Contents

1. Introduction
2. Features
3. Architecture
4. Getting Started
5. How to Play the Game

## Introduction

Welcome to the JustEatTakeaway Code Challenge application. This project simulates a number game where players take turns
making moves whether +1, -1 or do nothing then divide by 3 until a user reaches 1, at which point the game concludes. The application uses RabbitMQ for
messaging, MongoDB for persistence, and is built using Spring Boot.

## Features

- Start a new game
- Make moves in manual or automatic mode
- Game state management
- Real-time updates using RabbitMQ
- Persistence using MongoDB
- Comprehensive test coverage

## Architecture

The application is structured into several components:

- **Controllers**: Handle HTTP requests and responses.
- **Services**: Contain business logic.
- **Repositories**: Interact with the database.
- **Models**: Represent the data structures.
- **Messaging**: Handles RabbitMQ messaging.

## Getting Started

### Prerequisites

Ensure you have the following installed:

- Java 21 or higher
- Maven
- Docker (for RabbitMQ and MongoDB)

### Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/Kareem-Ismail/scoober-app.git
    ```

2. **Start the application with one command:**

    ```bash
   ./start-game
    ```
3. **If you get access denied exception the use:**

    ```bash
   chmod +x start-game.sh
    ```

### Running the Application
To check the available APIs go to [Swagger](http://localhost:8080/swagger-ui/index.html)

## How to play the game

1. **Start a New Game**:
   - Use the POST request `/start/create-game` from the Swagger UI to create a new game.
   - Specify the playing mode (automatic or manual) when creating the game.

2. **Set Player Mode**:
   - The other player should set their gaming mode using the PATCH request `/start/set-player-mode`.

3. **Game Play**:
   - If both players select **Automatic Mode**, the game will play and finish automatically, displaying the winner and the number of moves taken.
   - If one or both players choose **Manual Mode**, use the POST request `/play` to specify the next operation. The operation must generate a number divisible by three.

