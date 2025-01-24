package com.example.superhero_database.dto.battle;

import java.util.Map;

public record HeroStats(
        int strength,
        int power,
        int speed,
        int intelligence,
        int durability
) {

    public static HeroStats fromApi(Map<String, String> apiStats) {
        return new HeroStats(
                parseOrRandom(apiStats.get("strength")),
                parseOrRandom(apiStats.get("power")),
                parseOrRandom(apiStats.get("speed")),
                parseOrRandom(apiStats.get("intelligence")),
                parseOrRandom(apiStats.get("durability"))
        );
    }

    private static int parseOrRandom(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return (int)(Math.random() * 70) + 5;
        }
    }


    public int getOffensiveScore() {
        return strength + power;
    }

    public int getDefensiveScore() {
        return intelligence + durability;
    }
}