package com.example.superhero_database.service.impl;

import com.example.superhero_database.model.DashboardStats;
import com.example.superhero_database.model.Match;
import com.example.superhero_database.model.PlayerStats;
import com.example.superhero_database.service.DashboardService;
import com.example.superhero_database.service.PlayerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MatchServiceImpl matchService;
    private final PlayerStatsService playerStatsService;

    @Override
    public DashboardStats getStatsForUsername(String username) {
        List<Match> userMatches = matchService.findByPlayer_Username(username);
        PlayerStats playerStats = playerStatsService.getStatsByUsername(username);

        DashboardStats stats = new DashboardStats();
        stats.setMatchesPlayed(userMatches.size());
        stats.setWins(playerStats.getWins());
        stats.setTotalLosses(playerStats.getLosses());
        stats.setCurrentStreak(playerStats.getCurrentStreak());
        stats.setWinRatio(calculateWinRatio(playerStats.getWins(), playerStats.getLosses()));
        stats.setMostUsedHero(matchService.findMostUsedHeroByUsername(username).orElse("No matches yet"));

        return stats;
    }

    private String calculateWinRatio(int wins, int losses) {
        int total = wins + losses;
        if (total == 0) return "0.00";
        return String.format("%.2f", (double) wins / total);
    }

}
