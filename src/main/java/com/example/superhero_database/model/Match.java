package com.example.superhero_database.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name="matches")
@Data
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @ElementCollection
    @CollectionTable(
            name = "match_player_heroes",
            joinColumns = @JoinColumn(name = "match_id")
    )
    @Column(name = "hero_id")
    private List<String> playerHeroIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "match_opponent_heroes",
            joinColumns = @JoinColumn(name = "match_id")
    )
    @Column(name = "hero_id")
    private List<String> opponentHeroIds = new ArrayList<>();

    @Column(name = "match_date")
    private Date matchDate;

    @Column(name = "survival_time_player")
    private Double survivalTimePlayer;

    @Column(name = "survival_time_opponent")
    private Double survivalTimeOpponent;

    private boolean victory;
}
