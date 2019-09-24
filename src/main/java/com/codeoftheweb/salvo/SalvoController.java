package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    private GamePlayerRepository gamePlayerRepository;
    @RequestMapping ("/games")
    public List<Map<String, Object>> getAllGames(){
        return gameRepository.findAll()
                             .stream()
                             .map(game -> makePlayerDTO(pl))
                             .collect(Collectors.toList());
    }

    public Map<String,Object> makePlayerDTO(Player player){
        Map<String,Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id:", player.getId());
        dto.put("email:", player.getUserName());
        return dto;
    }

    public Map<String,Object> makeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String,Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        return dto;
    }

    }

