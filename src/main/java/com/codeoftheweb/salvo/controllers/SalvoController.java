package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
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

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> getNewGame(Authentication authentication){
        if (isGuest(authentication)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else {
            Player creatorPlayer = playerRepository.findByUserName(authentication.getName());
            Date newDate = new Date();
            Game newGame = gameRepository.save(new Game());
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newDate,newGame,creatorPlayer));
            long gamePlayerId = newGamePlayer.getId();
            return new ResponseEntity<>(makeMap("gpid", gamePlayerId), HttpStatus.CREATED);
        }
    }



    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> getLeaderBoard() {
        return playerRepository.findAll()
                .stream()
                .map(player -> player.makePlayerScoreDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable long gamePlayerId, Authentication authentication) {
        GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).get();
        if (authentication.getName()==gamePlayerRepository.findById(gamePlayerId).get().getPlayer().getUserName()){
            return ResponseEntity.ok(gp.makeGamePlayersDTO2());
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private Map<String, Object> makeMap(String key, long id) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, id);
        return map;
    }


    /* @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable long gamePlayerId) {
        return gamePlayerRepository.findById(gamePlayerId).get().makeGamePlayersDTO2();
    }*/

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

    /*@RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(@RequestParam String email) {
        if (email.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "You must log in"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(email);
        if (email != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Game newGame = gameRepository.save(new Game());
        return new ResponseEntity<>(makeMap("id", newGame.getId()), HttpStatus.CREATED);
    }
    }*/
}