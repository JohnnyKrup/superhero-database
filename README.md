# SuperHeroAPI Backend Documentation

## Overview
SuperHeroAPI is a Spring Boot application that powers the SuperHero Database and Battle game. It provides RESTful endpoints for user authentication, superhero data management, and battle mechanics.

## Technologies Used
- Java 17
- Spring Boot 3.2.x
- Spring Security with JWT Authentication
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- MapStruct

## Prerequisites
- JDK 17+
- Maven 3.8+
- PostgreSQL 14+
- Git

## Architecture
[System Architecture](docs/architecture.md)

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/JohnnyKrup/superhero-database.git
cd superhero-api
```

### Database Configuration
Create a PostgreSQL database and update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/superherodb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Battles Table
```sql
CREATE TABLE battles (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT REFERENCES users(id),
    team1_heroes VARCHAR(255)[], -- Array of hero IDs
    team2_heroes VARCHAR(255)[], -- AI team hero IDs
    winner INTEGER NOT NULL, -- 1 for player, 2 for AI
    battle_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User_Stats Table
```sql
CREATE TABLE user_stats (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    matches_played INTEGER DEFAULT 0,
    wins INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    most_used_hero VARCHAR(255)
);
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/verify` - Verify JWT token

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `PUT /api/users/password` - Update password

### Battles
- `POST /api/battle/start` - Initialize battle
- `POST /api/battle/simulate` - Simulate battle
- `GET /api/battle/history` - Get battle history

### Dashboard
- `GET /api/dashboard` - Get user statistics

### SuperHero Data
- `GET /api/superheroapi/random` - Get random heroes
- `GET /api/superheroapi/search/{name}` - Search heroes by name
- `GET /api/superheroapi/{id}` - Get hero by ID

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### JWT Configuration
```java
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // JWT configuration methods
}
```

## Battle Mechanics

Battles are simulated using a scoring system based on hero stats:

1. **Offensive Score**: Calculated from strength, power, and combat stats
2. **Defensive Score**: Based on durability and intelligence
3. **Team Synergy**: Additional modifiers based on hero alignment and publisher
4. **Random Factor**: Small randomization to prevent predictable outcomes

### Battle Simulation Process
```java
public BattleResult simulateBattle(List<Hero> team1, List<Hero> team2) {
    double team1Score = calculateTeamScore(team1);
    double team2Score = calculateTeamScore(team2);
    
    // Battle simulation logic
    return new BattleResult(winner, survivialTime, teamStats);
}
```

## Error Handling

The API uses a global exception handler for consistent error responses:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

Error Response Format:
```json
{
    "status": 400,
    "message": "Error message",
    "timestamp": "2024-01-24T10:00:00Z"
}
```

## Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

## Testing

Run tests using Maven:
```bash
mvn test
```

Example test class:
```java
@SpringBootTest
public class BattleServiceTest {
    @Autowired
    private BattleService battleService;
    
    @Test
    public void testBattleSimulation() {
        // Test battle mechanics
    }
}
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License.
