package com.example.superhero_database.repository;

import com.example.superhero_database.model.Match;
import com.example.superhero_database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // Find all matches for a specific player
    List<Match> findByPlayerOrderByMatchDateDesc(User player);

    // Find player's winning matches
    List<Match> findByPlayerAndVictoryTrue(User player);

    // Find player's losing matches
    List<Match> findByPlayerAndVictoryFalse(User player);

    // Get win/loss count
    @Query("SELECT COUNT(m) FROM Match m WHERE m.player = ?1 AND m.victory = true")
    long countWins(User player);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.player = ?1 AND m.victory = false")
    long countLosses(User player);

    // Get current win streak
    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE m.player = ?1 
        AND m.victory = true 
        AND m.id > (
            SELECT MAX(m2.id) FROM Match m2 
            WHERE m2.player = ?1 
            AND m2.victory = false
        )
    """)
    long getCurrentStreak(User player);

    List<Match> findByPlayer_Username(String username);

    List<Match> findByPlayer_Email(String email);

    @Query("""
        SELECT ph FROM Match m 
        JOIN m.playerHeroIds ph
        WHERE m.player.username = :username 
        GROUP BY ph
        ORDER BY COUNT(ph) DESC 
        LIMIT 1
    """)
    Optional<String> findMostUsedHeroByUsername(@Param("username") String username);

}
