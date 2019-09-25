package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @Column(name = "shipLocations")
    private List<String> shipLocations = new ArrayList<>();

    public Ship() {
    }

    public Ship(GamePlayer gamePlayer, String shipType, List<String> shipLocations) {
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

    public List getLocations() {
        return shipLocations;
    }
}
