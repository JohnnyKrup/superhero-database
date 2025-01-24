package com.example.superhero_database.service;

import com.example.superhero_database.model.PlayerStats;
import com.example.superhero_database.model.User;

public interface PlayerStatsService {

    PlayerStats getOrCreateStats(User user);
    PlayerStats updateStats(User user, boolean victory);
    PlayerStats getStatsByUsername(String username);
}
