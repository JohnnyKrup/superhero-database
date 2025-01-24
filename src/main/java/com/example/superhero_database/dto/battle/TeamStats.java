package com.example.superhero_database.dto.battle;

public record TeamStats(
        double damagePerSecond,
        int defense
) {
    public double survivalTime(TeamStats opponent) {
        return defense / opponent.damagePerSecond;
    }
}
