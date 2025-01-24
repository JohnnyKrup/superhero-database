package com.example.superhero_database.dto.battle;

public record BattleResult (
    boolean victory,
    double team1SurvivalTime,
    double team2SurvivalTime
){}
