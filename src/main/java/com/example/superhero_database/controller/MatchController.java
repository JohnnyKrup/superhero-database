package com.example.superhero_database.controller;

import com.example.superhero_database.dto.battle.BattleResult;
import com.example.superhero_database.dto.battle.HeroData;
import com.example.superhero_database.model.Match;
import com.example.superhero_database.model.User;
import com.example.superhero_database.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/battle")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/start")
    public ResponseEntity<?> startBattle(@RequestBody Map<String, List<String>> request) {
        List<String> playerTeam = request.get("playerTeam");
        // Generate AI team and return both teams
        return ResponseEntity.ok(matchService.generateBattle(playerTeam));
    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateBattle(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, List<String>> request) {

        List<String> playerTeamIds = request.get("playerTeam");
        List<String> aiTeamIds = request.get("aiTeam");

        // Convert IDs to HeroData
        List<HeroData> playerTeam = matchService.fetchFullHeroData(playerTeamIds);
        List<HeroData> aiTeam = matchService.fetchFullHeroData(aiTeamIds);

        Map<String, Object> battleResult = matchService.simulateBattle(playerTeam, aiTeam);

        // Extract victory from the nested result structure
        Map<String, Object> result = (Map<String, Object>) battleResult.get("result");
        boolean victory = (boolean) result.get("victory");

        // Create the match with the extracted victory value
        Match match = matchService.createMatch(user, playerTeam, aiTeam, victory);

        return ResponseEntity.ok(battleResult);
    }
}