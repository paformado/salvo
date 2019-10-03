package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

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
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

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

    public Set<Score> getScores() {
        return scores;
    }

    public Map<String, Object> makeGamePlayersDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpid", getId());
        dto.put("id", player.getId());
        dto.put("name", player.getUserName());
        return dto;
    }

    public Map<String, Object> makeGamePlayersDTO2() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.game.getId());
        dto.put("created", this.game.getCreationDate());
        dto.put("gamePlayers", this.game.getAllGamePlayers(game.getGamePlayers()));
        dto.put("ships", getAllShips(this.getShips()));
        dto.put("salvoes", game.getSalvoesDTO());
        return dto;
    }

    public List<Map<String, Object>> getAllShips(Set<Ship> ships){
        return ships.stream()
                .map(ship -> ship.makeShipsDTO())
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllScores(Set<Score> scores){
        return this.getScores()
                .stream()
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList());
    }


}
