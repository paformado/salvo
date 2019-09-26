package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

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

    public Date getJoinDate() {
        return joinDate;
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

    public Map<String, Object> makeGamePlayersDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gamePlayer_id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }

    public Map<String, Object> makeGamePlayersDTO2() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.game.getId());
        dto.put("creationDate", this.game.getCreationDate());
        dto.put("gamePlayers", this.game.getAllGamePlayers(game.getGamePlayers()));
        dto.put("ships", getAllShips(this.getShips()));//FALTA CREAR GETALLSHIPS
        return dto;
    }
}
