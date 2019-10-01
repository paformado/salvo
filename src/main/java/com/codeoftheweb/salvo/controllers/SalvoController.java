package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public List<Map<String, Object>> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(game -> game.makeGamesDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderBoard() {
        return playerRepository.findAll()
                .stream()
                .map(player -> player.makePlayerScoreDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable long gamePlayerId) {
        return gamePlayerRepository.findById(gamePlayerId).get().makeGamePlayersDTO2();
    }
}