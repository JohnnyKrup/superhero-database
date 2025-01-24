package com.example.superhero_database.service.impl;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.model.PlayerStats;
import com.example.superhero_database.model.User;
import com.example.superhero_database.repository.PlayerStatsRepository;
import com.example.superhero_database.repository.UserRepository;
import com.example.superhero_database.service.PlayerStatsService;
import com.example.superhero_database.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerStatsServiceImpl implements PlayerStatsService {

    private final PlayerStatsRepository playerStatsRepository;
    private final UserService userService;


    @Override
    @Transactional
    public PlayerStats getOrCreateStats(User user) {
        return playerStatsRepository.findByUser(user)
                .orElseGet(() -> {
                    PlayerStats newStats = new PlayerStats();
                    newStats.setUser(user);
                    return playerStatsRepository.save(newStats);
                });
    }

    @Override
    @Transactional
    public PlayerStats updateStats(User user, boolean victory) {
        PlayerStats stats = getOrCreateStats(user);

        if (victory) {
            stats.setWins(stats.getWins() + 1);
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else {
            stats.setLosses(stats.getLosses() + 1);
            stats.setCurrentStreak(0);
        }

        return playerStatsRepository.save(stats);
    }

    @Override
    public PlayerStats getStatsByUsername(String username) {
        // the passed in username is an email
        User user = userService.getUserEntityByEmail(username);

        return playerStatsRepository.findByUser(user)
                .orElseGet(() -> createInitialStats(user));
    }


    private PlayerStats createInitialStats(User user) {
        PlayerStats newStats = new PlayerStats();
        newStats.setUser(user);  // This will set the user_id
        newStats.setWins(0);
        newStats.setLosses(0);
        newStats.setCurrentStreak(0);
        return playerStatsRepository.save(newStats);
    }
}
