# System Architecture

## Project Structure Overview

The SuperHero Database application follows a layered architecture pattern with clear separation of concerns between components.

![System Architecture](./images/system-architecture.png)

## Class Diagram

![Class Diagram](./images/class-diagram.png)

## Main Components

### Controllers Layer
```mermaid
graph TD
    A[AuthController] --> B[AuthService]
    C[BattleController] --> D[BattleService]
    E[UserController] --> F[UserService]
    G[SuperheroController] --> H[SuperheroService]
```

### Service Layer
The service layer contains business logic and integrates with external APIs:
- AuthService: Handles user authentication
- BattleService: Manages battle mechanics
- UserService: User management
- SuperheroService: Superhero data management

### Repository Layer
Interfaces with the database using Spring Data JPA:
- UserRepository
- BattleRepository
- UserStatsRepository

[← Back to README](../README.md)
