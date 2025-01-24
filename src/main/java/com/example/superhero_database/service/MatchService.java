package com.example.superhero_database.service;

import com.example.superhero_database.dto.battle.HeroData;
import com.example.superhero_database.model.Match;
import com.example.superhero_database.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MatchService {
    Map<String, Object> generateBattle(List<String> playerTeamIds);
    Map<String, Object> simulateBattle(List<HeroData> playerTeam, List<HeroData> aiTeam);
    Match createMatch(User player, List<HeroData> playerTeam, List<HeroData> aiTeam, boolean victory);
    List<HeroData> fetchFullHeroData(List<String> heroIds);
    List<Match> findByPlayer_Username(String username);
    Optional<String> findMostUsedHeroByUsername(String username);
}
