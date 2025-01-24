package com.example.superhero_database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class DashboardStats {
    private int matchesPlayed;
    private long wins;
    private String winRatio;
    private String mostUsedHero;
    private int currentStreak;
    private int totalLosses;
}