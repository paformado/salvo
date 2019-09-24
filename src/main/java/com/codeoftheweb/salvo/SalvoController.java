package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    private GamePlayerRepository gamePlayerRepository;
    @RequestMapping ("/games")
    public List<Long> getAllGames(){
        return gameRepository.findAll()
                             .stream()
                             .map(Game::getId)
                             .collect(Collectors.toList());
    }

}
