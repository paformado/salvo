package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;

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
                                        .map(game -> makeGamesDTO(game))
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

     @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication){
        if (isGuest(authentication)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        Game requestedGameId = gameRepository.findById(gameId).get();
        if(requestedGameId == null){
            return new ResponseEntity<>(makeMap("No such game", gameId), HttpStatus.FORBIDDEN);
        }
        if(requestedGameId.getGamePlayers().stream().count()>1){
            return new ResponseEntity<>(makeMap("Game is full", gameId), HttpStatus.FORBIDDEN);
        }
        Date newDate = new Date();
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newDate,requestedGameId,currentPlayer));
        long gamePlayerId = newGamePlayer.getId();
        return new ResponseEntity<>(makeMap("gpid", gamePlayerId), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable long gamePlayerId, Authentication authentication) {
        GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).get();
        if (authentication.getName()==gamePlayerRepository.findById(gamePlayerId).get().getPlayer().getUserName()){
            return ResponseEntity.ok().body(makeGameViewDTO(authentication, gp));
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RequestMapping(value="/games/players/{gamePlayerID}/salvoes", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setSalvos(@PathVariable long gamePlayerID, @RequestBody Salvo salvo, Authentication authentication){
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).orElse(null);
        Player loggedPlayer = playerRepository.findByUserName(authentication.getName());
        if (loggedPlayer == null){
            return new ResponseEntity<>(makeMap("no player logged in", gamePlayerID), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer == null){
            return new ResponseEntity<>(makeMap("no such gameplayer", gamePlayerID), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != loggedPlayer.getId()) {
            return new ResponseEntity<>(makeMap("wrong gameplayer", gamePlayerID), HttpStatus.UNAUTHORIZED);
        }
        Set<Salvo> salvoes = gamePlayer.getSalvos();
        for(Salvo salvoX: salvoes){
            if(salvo.getTurnNumber() == salvoX.getTurnNumber()){
                return new ResponseEntity<>(makeMap2("error", "The player already has submitted a salvo for the turn listed"), HttpStatus.FORBIDDEN);
            }
        }
        salvo.setGamePlayer(gamePlayer);
        salvoRepository.save(new Salvo(salvoes.size()+1, gamePlayer, salvo.getSalvoLocations()));
        return new ResponseEntity<>(makeMap2("addSalvoes","Salvoes saved"), HttpStatus.CREATED);
    }

    @RequestMapping(value="/games/players/{gpid}/ships", method=RequestMethod.POST)
    public ResponseEntity<Object> setShips(@PathVariable long gpid, @RequestBody Set<Ship> ships, Authentication authentication){
        GamePlayer gamePlayer = gamePlayerRepository.findById(gpid).orElse(null);
        Player loggedPlayer = playerRepository.findByUserName(authentication.getName());
        if (loggedPlayer == null){
            return new ResponseEntity<>(makeMap2("error", "no esta logueado"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer == null){
            return new ResponseEntity<>(makeMap2("error", "no existe el juego"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != loggedPlayer.getId()){
            return new ResponseEntity<>(makeMap2("error","juego incorrecto"), HttpStatus.UNAUTHORIZED);
        }else{
            if(gamePlayer.getShips().isEmpty()){
                ships.forEach(ship -> ship.setGamePlayer(gamePlayer));
                shipRepository.saveAll(ships);
                return new ResponseEntity<>(makeMap2("OK","Ships creados"), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(makeMap2("error","el jugador ya tiene ships"), HttpStatus.FORBIDDEN);
            }
        }
    }

    private Map<String, Object> makeMap(String key, long id) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, id);
        return map;
    }

    private Map<String, Object> makeMap2(String gp, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put(gp, id);
        return map;
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

    public GamePlayer getOpponent(GamePlayer gpSelf) {
        return gpSelf.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != gpSelf.getId()).findAny().orElse(null);
    }

    public Map<String, Object> getHitsDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        GamePlayer self = gamePlayer;
        GamePlayer opponent = getOpponent(gamePlayer);

        if (opponent != null){
            dto.put("self", getAllHits(getOpponent(gamePlayer)));
            dto.put("opponent", getAllHits(gamePlayer));
        }else{
            dto.put("self", new ArrayList<>());
            dto.put("opponent", new ArrayList<>());
        }

        return dto;
    }

    public List<Map<String, Object>> getAllHits(GamePlayer gamePlayer) {
        List<Map<String, Object>> listaDeDTO = new ArrayList<>();

        int carrierDMG = 0, battleshipDMG = 0, submarineDMG = 0, destroyerDMG = 0, patrolboatDMG = 0;
        for (Salvo salvo: gamePlayer.getSalvos()) {
            int carrierHIT = 0, battleshipHIT = 0, submarineHIT = 0, destroyerHIT = 0, patrolboatHIT = 0;
            List<String> hitLocations = new ArrayList<>();
            for (Ship ship : getOpponent(gamePlayer).getShips()) {
                List<String> hits = new ArrayList<>(salvo.getSalvoLocations());
                hits.retainAll(ship.getLocations());
                int shots = hits.size();
                if (shots != 0) {
                    hitLocations.addAll(hits);
                    switch (ship.getType()){
                        case "carrier":
                            carrierHIT+=shots;
                            carrierDMG+=shots;
                            break;
                        case "battleship":
                            battleshipHIT+=shots;
                            battleshipDMG+=shots;
                            break;
                        case "submarine":
                            submarineHIT+=shots;
                            submarineDMG+=shots;
                            break;
                        case "destroyer":
                            destroyerHIT+=shots;
                            destroyerDMG+=shots;
                            break;
                        case "patrolboat":
                            patrolboatHIT+=shots;
                            patrolboatDMG+=shots;
                            break;
                    }

                }}

                Map<String, Object> dto = new LinkedHashMap<String, Object>();
                dto.put("turn", salvo.getTurnNumber());
                dto.put("hitLocations", hitLocations);
                dto.put("damages", getDamageDTO(carrierHIT, carrierDMG, battleshipHIT, battleshipDMG,
                        submarineHIT, submarineDMG, destroyerHIT, destroyerDMG, patrolboatHIT, patrolboatDMG));
                dto.put("missed", salvo.getSalvoLocations().size() - hitLocations.size());
                listaDeDTO.add(dto);
        }
        return listaDeDTO;
    }

    public Map<String, Object> getDamageDTO(int carrierHIT, int carrierDMG, int battleshipHIT, int battleshipDMG,
                                             int submarineHIT, int submarineDMG, int destroyerHIT, int destroyerDMG,
                                             int patrolboatHIT, int patrolboatDMG) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("carrierHits", carrierHIT);
        dto.put("battleshipHits", battleshipHIT);
        dto.put("submarineHits", submarineHIT);
        dto.put("destroyerHits", destroyerHIT);
        dto.put("patrolboatHits", patrolboatHIT);
        dto.put("carrier", carrierDMG);
        dto.put("battleship",battleshipDMG);
        dto.put("submarine",submarineDMG);
        dto.put("destroyer", destroyerDMG);
        dto.put("patrolboat", patrolboatDMG);
        return dto;
    }

    public List<Map<String, Object>> getAllShips(Set<Ship> ships){
        return ships.stream()
                .map(ship -> makeShipsDTO(ship))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllGamePlayers(Set<GamePlayer> gamePlayers){
        return gamePlayers.stream()
                .map(gamePlayer -> makeGamePlayersDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    public List<Object> getSalvoesDTO(GamePlayer gamePlayer){
        return gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer.getSalvoes().stream())
                .map(salvo -> makeSalvoDTO(salvo))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllScores(Set<Score> scores){
        return scores.stream()
                .map(score -> makeScoreDTO(score))
                .collect(Collectors.toList());
    }

    public Map<String, Object> makeGamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", gamePlayer.getPlayer().makePlayerDTO());
        return dto;
    }

    //ESTO ES PARA /game_view/{gamePlayerId}
    public Map<String, Object> makeGameViewDTO(Authentication authentication, GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("gameState", getGameState(gamePlayer));
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", getAllGamePlayers(gamePlayer.getGame().getGamePlayers()));
        dto.put("ships", getAllShips(gamePlayer.getShips()));
        dto.put("salvoes", getAllSalvoes(gamePlayer.getSalvoes()));
        dto.put("hits", getHitsDTO(gamePlayer));
        return dto;
    }

    //ESTO ES PARA /games
    public Map<String, Object> makeGamesDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", getAllGamePlayers(game.getGamePlayers()));
        dto.put("scores", getAllScores(game.getScores()));
        return dto;
    }

    public Map<String, Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurnNumber());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getSalvoLocations());
        return dto;
    }

    public Map<String, Object> makeScoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", score.getPlayer().getId());
        dto.put("score", score.getScore());
        dto.put("finishDate", score.getFinishDate());
        return dto;
    }

    public Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    public List<Map<String, Object>> getAllSalvoes(Set<Salvo> salvoes){
        return salvoes.stream().map(salvo -> makeSalvoDTO(salvo)).collect(Collectors.toList());
    }

    private long getCurrentTurn(GamePlayer gamePlayer, GamePlayer opponent) {

        int selfGPSalvoes = gamePlayer.getSalvoes().size();
        int opponentGPSalvoes = opponent.getSalvoes().size();

        int totalSalvoes = selfGPSalvoes + opponentGPSalvoes;

        if (totalSalvoes % 2 == 0)
            return totalSalvoes / 2 + 1;

        return (int) (totalSalvoes / 2.0 + 0.5);
    }

    public String getGameState(GamePlayer gamePlayer) {
        if (gamePlayer.getShips().size() == 0) {
            return "PLACESHIPS";
        }
        if (getOpponent(gamePlayer) == null) {
            return "ESPERANDO OPONENTE";
        }

        if ((gamePlayer.getSalvos().size() == getOpponent(gamePlayer).getSalvos().size()) &&
                (gamePlayer.getShips().size() > 0 && getOpponent(gamePlayer).getShips().size() > 0)) {
            return "PLAY";
        } else if ((gamePlayer.getSalvos().size() > getOpponent(gamePlayer).getSalvos().size())) {
            return "WAITINGFOROPP";
        } else if (getOpponent(gamePlayer).getShips().size() == 0 || gamePlayer.getShips().size() == 0) {
            return "GAME OVER";
        }

        if (getOpponent(gamePlayer).getShips().size() == 0 && gamePlayer.getShips().size() > 0) {
            return "WON";
        } else if (getOpponent(gamePlayer).getShips().size() > 0 && gamePlayer.getShips().size() == 0) {
            return "LOST";
        } else {
            return "TIE";
        }
    }
}