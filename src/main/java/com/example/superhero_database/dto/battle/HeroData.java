package com.example.superhero_database.dto.battle;

public record HeroData(
        String id,
        String name,
        String imageUrl,
        HeroStats stats
) {}