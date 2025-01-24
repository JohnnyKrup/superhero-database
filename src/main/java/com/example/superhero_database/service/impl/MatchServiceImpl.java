package com.example.superhero_database.service.impl;

import com.example.superhero_database.dto.battle.BattleResult;
import com.example.superhero_database.dto.battle.HeroData;
import com.example.superhero_database.dto.battle.HeroStats;
import com.example.superhero_database.dto.battle.TeamStats;
import com.example.superhero_database.model.Match;
import com.example.superhero_database.model.User;
import com.example.superhero_database.repository.MatchRepository;
import com.example.superhero_database.service.MatchService;
import com.example.superhero_database.service.PlayerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    @Value("${superhero.api.key}")
    private String apiKey;

    private final PlayerStatsService playerStatsService;
    private final MatchRepository matchRepository;
    private final RestTemplate restTemplate;


    @Override
    public Map<String, Object> generateBattle(List<String> playerTeamIds) {
        List<HeroData> playerTeam = fetchFullHeroData(playerTeamIds);
        List<HeroData> aiTeam = generateRandomTeam(2);

        return Map.of(
                "playerTeam", playerTeam,
                "aiTeam", aiTeam
        );
    }

    @Override
    public Map<String, Object> simulateBattle(List<HeroData> playerTeam, List<HeroData> aiTeam) {
        TeamStats playerTeamStats = calculateTeamStats(playerTeam);
        TeamStats aiTeamStats = calculateTeamStats(aiTeam);

        double team1Survival = playerTeamStats.survivalTime(aiTeamStats);
        double team2Survival = aiTeamStats.survivalTime(playerTeamStats);

        // Create the structure the frontend expects
        Map<String, Object> battleResult = new HashMap<>();
        battleResult.put("matchId", UUID.randomUUID().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("victory", team1Survival > team2Survival);
        result.put("team1SurvivalTime", team1Survival);
        result.put("team2SurvivalTime", team2Survival);
        battleResult.put("result", result);

        Map<String, Object> teamStats = new HashMap<>();

        // Player team stats
        Map<String, Object> playerStats = new HashMap<>();
        playerStats.put("offensiveScore", playerTeamStats.damagePerSecond());
        playerStats.put("defensiveScore", playerTeamStats.defense());

        // AI team stats
        Map<String, Object> aiStats = new HashMap<>();
        aiStats.put("offensiveScore", aiTeamStats.damagePerSecond());
        aiStats.put("defensiveScore", aiTeamStats.defense());

        teamStats.put("player", playerStats);
        teamStats.put("ai", aiStats);
        battleResult.put("teamStats", teamStats);

        return battleResult;
    }


    @Override
    public Match createMatch(User player, List<HeroData> playerTeam, List<HeroData> aiTeam, boolean victory) {
        Match match = new Match();
        match.setPlayer(player);
        match.setPlayerHeroIds(playerTeam.stream().map(HeroData::id).collect(Collectors.toList()));
        match.setOpponentHeroIds(aiTeam.stream().map(HeroData::id).collect(Collectors.toList()));
        match.setVictory(victory);
        match.setMatchDate(new Date());

        // Add survival times
        TeamStats playerStats = calculateTeamStats(playerTeam);
        TeamStats aiStats = calculateTeamStats(aiTeam);
        match.setSurvivalTimePlayer(playerStats.survivalTime(aiStats));
        match.setSurvivalTimeOpponent(aiStats.survivalTime(playerStats));

        // Update Player Stats
        playerStatsService.updateStats(player, victory);

        return matchRepository.save(match);
    }


    @Override
    public List<HeroData> fetchFullHeroData(List<String> heroIds) {
        return heroIds.stream()
                .map(id -> {
                    String url = String.format("https://superheroapi.com/api/%s/%s", apiKey, id);
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                    Map<String, String> image = (Map<String, String>) response.get("image");
                    return new HeroData(
                            id,
                            (String) response.get("name"),
                            image.get("url"),
                            HeroStats.fromApi((Map<String, String>)response.get("powerstats"))
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Match> findByPlayer_Username(String username) {
        System.out.println("MatchService.findByPlayer_Username: " + username);
        return matchRepository.findByPlayer_Email(username);
    }

    @Override
    public Optional<String> findMostUsedHeroByUsername(String username) {
        return matchRepository.findMostUsedHeroByUsername(username);
    }


    private BattleResult calculateBattle(List<HeroData> team1, List<HeroData> team2) {
        TeamStats team1Stats = calculateTeamStats(team1);
        TeamStats team2Stats = calculateTeamStats(team2);

        double team1Survival = team1Stats.survivalTime(team2Stats);
        double team2Survival = team2Stats.survivalTime(team1Stats);

        return new BattleResult(
                team1Survival > team2Survival,
                team1Survival,
                team2Survival
        );
    }

    private TeamStats calculateTeamStats(List<HeroData> team) {
        double totalOffense = team.stream()
                .mapToDouble(hero -> hero.stats().getOffensiveScore())
                .sum();

        double avgSpeed = team.stream()
                .mapToDouble(hero -> hero.stats().speed())
                .average()
                .orElse(0);

        double totalDefense = team.stream()
                .mapToDouble(hero -> hero.stats().getDefensiveScore())
                .sum();

        // Calculate damage per second
        double dps = (totalOffense * avgSpeed) / 100;

        return new TeamStats(dps, (int) totalDefense);
    }

    private List<HeroData> generateRandomTeam(int size) {
        List<HeroData> team = new ArrayList<>();

        while (team.size() < size) {
            String id = String.valueOf((int)(Math.random() * 731) + 1);
            String url = String.format("https://superheroapi.com/api/%s/%s", apiKey, id);

            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "success".equals(response.get("response"))) {
                    Map<String, String> image = (Map<String, String>) response.get("image");
                    team.add(new HeroData(
                            id,
                            (String) response.get("name"),
                            image.get("url"),
                            HeroStats.fromApi((Map<String, String>)response.get("powerstats"))
                    ));
                }
            } catch (Exception e) {
                continue;
            }
        }

        return team;
    }
}