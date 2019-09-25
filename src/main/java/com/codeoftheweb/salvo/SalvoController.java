package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    private GamePlayerRepository gamePlayerRepository;
    private PlayerRepository playerRepository;
    @RequestMapping ("/games")
    public List<Map<String, Object>> getAllGames(){
        return gameRepository.findAll()
                .stream()
                .map(game -> makeGamesDTO(game))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAllGamePlayers(Set<GamePlayer> gamePlayers){
        return gamePlayers.stream()
                .map(gamePlayer -> makeGamePlayersDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAllPlayers(){
        return playerRepository.findAll()
                .stream()
                .map(player -> makePlayerDTO(player))
                .collect(Collectors.toList());
    }


    public Map<String, Object> makeGamesDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("creationDate", game.getCreationDate());
        dto.put("gamePlayers", getAllGamePlayers(game.getGamePlayers()));
        return dto;
    }
    public Map<String, Object> makeGamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gamePlayer id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    public Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    }