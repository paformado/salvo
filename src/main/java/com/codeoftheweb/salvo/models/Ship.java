package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_ID")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "shipLocations")
    private Set<String> locations = new LinkedHashSet<>();

    public Ship() {
    }

    public Ship(GamePlayer gamePlayer, String shipType, Set<String> shipLocations) {
        this.gamePlayer = gamePlayer;
        this.type = shipType;
        this.locations = shipLocations;
    }

    public long getId() {
        return id;
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }


    /*public Map<String, Object> makeShipsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getShipType());
        dto.put("locations", this.getShipLocations());
        return dto;
    }*/
}
