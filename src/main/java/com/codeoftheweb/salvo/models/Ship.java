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
    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_ID")
    private GamePlayer gamePlayer;

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

    public Set<String> getShipLocations() {
        return shipLocations;
    }

    public Map<String, Object> makeShipsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getShipType());
        dto.put("locations", this.getShipLocations());
        return dto;
    }
}
