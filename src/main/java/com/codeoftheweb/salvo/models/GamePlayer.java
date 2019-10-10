package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name ="native", strategy = "native")
    private long id;
    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvos = new HashSet<>();

    /*@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();*/

    public GamePlayer() {
    }

    public GamePlayer(Date joinDate, Game game, Player player){
        this.joinDate = joinDate;
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvos;
    }

    /*public Set<Score> getScores() {
        return scores;
    }*/

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    /*public Map<String, Object> makeGamePlayersDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }

    public Map<String, Object> makeGameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.game.getId());
        dto.put("created", this.game.getCreationDate());
        dto.put("gamePlayers", this.game.getAllGamePlayers(game.getGamePlayers()));
        dto.put("ships", getAllShips(this.getShips()));
        dto.put("salvoes", game.getSalvoesDTO());
        //dto.put("hits", makeHitsDTO());
        return dto;
    }

    public List<Map<String, Object>> getAllShips(Set<Ship> ships){
        return ships.stream()
                .map(ship -> ship.makeShipsDTO())
                .collect(Collectors.toList());
    }*/

    /*public List<Map<String, Object>> getAllScores(Set<Score> scores){
        return this.getScores()
                .stream()
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList());
    }*/


    /*public List<String> getHitLocations(GamePlayer gamePlayer){
        return gamePlayer.getSalvos().stream().filter(salvo -> salvo.getSalvoLocations() == getOpponent(gamePlayer).getAllShips(ships).get(shi)).collect(Collectors.toList());
    }*/

}
