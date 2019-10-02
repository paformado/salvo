package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;

    public Game() {
    }

    public Game(Date creationDate){
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate(){
        return creationDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public Map<String, Object> makeGamesDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", getAllGamePlayers(this.getGamePlayers()));
        dto.put("scores", this.getAllScoresFromGamePlayers());
        return dto;
    }

    public List<Map<String, Object>> getAllGamePlayers(Set<GamePlayer> gamePlayers){
        return gamePlayers.stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayersDTO())
                .collect(Collectors.toList());
    }

    public List<Object> getSalvoesDTO(){
        return this.getGamePlayers()
                .stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvoes().stream())
                .map(salvo -> salvo.makeSalvoDTO())
                .collect(Collectors.toList());
    }

   public List<Map<String, Object>> getAllScores(Set<Score> scores){
        return this.getScores()
                .stream()
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllScoresFromGamePlayers(){
        if(!scores.isEmpty()) {
            return this.scores.stream()
                    .map(score -> score.makeScoreDTO())
                    .collect(Collectors.toList());
        }else{
            return null;
        }
    }
}
