package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turnNumber;

    @ElementCollection
    @Column(name = "salvoLocations")
    private Set<String> salvoLocations = new LinkedHashSet<String>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_ID")
    private GamePlayer gamePlayer;

    public Salvo(){
    }

    public Salvo(int turnNumber, GamePlayer gamePlayer, Set<String> salvoLocations) {
        this.turnNumber = turnNumber;
        this.salvoLocations = salvoLocations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public long getTurnNumber() {
        return turnNumber;
    }

    public Set<String> getSalvoLocations() {
        return salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurnNumber());
        dto.put("player", this.getId());
        dto.put("salvoLocations", this.getSalvoLocations());
        return dto;
    }


}
