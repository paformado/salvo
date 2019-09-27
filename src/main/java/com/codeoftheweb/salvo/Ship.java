package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import sun.awt.image.ImageWatched;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_ID")
    private GamePlayer gamePlayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game")
    private Game game;

    @ElementCollection
    @Column(name = "shipLocations")
    private Set<String> shipLocations = new LinkedHashSet<>();

    public Ship() {
    }

    public Ship(GamePlayer gamePlayer, String shipType, Set<String> shipLocations) {
        this.gamePlayer = gamePlayer;
        this.shipType = shipType;
        this.shipLocations = shipLocations;
    }

    public long getId() {
        return id;
    }

    public String getShipType() {
        return shipType;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Set<String> getShipLocations() {
        return shipLocations;
    }

    public Map<String, Object> makeShipsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getShipType());
        dto.put("shipLocations", this.getShipLocations());
        return dto;
    }
}
