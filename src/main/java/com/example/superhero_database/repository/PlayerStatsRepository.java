package com.example.superhero_database.repository;

import com.example.superhero_database.model.PlayerStats;
import com.example.superhero_database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

    Optional<PlayerStats> findByUser(User user);
    Optional<PlayerStats> findByUser_Username(String username);
    Optional<PlayerStats> findByUser_Email(String email);

}
