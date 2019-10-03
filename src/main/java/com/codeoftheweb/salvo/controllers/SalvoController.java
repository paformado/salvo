package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sun.awt.image.ImageWatched;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if(isGuest(authentication)){
            dto.put("player", "Guest");
        }else{
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        }

        dto.put("games", gameRepository.findAll()
                                        .stream()
                                        .map(game -> game.makeGamesDTO())
                                        .collect(Collectors.toList()));
        return dto;
    }

    /*private Player getPlayerLogin(Authentication authentication){
        List<Player> listPlayerLogin = new ArrayList<>();
        if (!isGuest(authentication)){
            listPlayerLogin = playerRepository.findAll()
                                                .stream()
                                                .filter(player -> player.getUserName().equals(authentication.getName()))
                                                .collect(Collectors.toList());;
        }
        if(listPlayerLogin.isEmpty()){
            return null;
        }else{
            return listPlayerLogin.get(0);
        }
    }*/

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    /*public List<Map<String, Object>> getAllGames(Authentication authentication) {
        return gameRepository.findAll()
                .stream()
                .map(game -> game.makeGamesDTO())
                .collect(Collectors.toList());
    }*/

    @RequestMapping("/leaderBoard")
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}