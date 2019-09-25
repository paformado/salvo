package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData (PlayerRepository playerRepository,
									   GameRepository gameRepository,
									   GamePlayerRepository gamePlayerRepository,
									   ShipRepository shipRepository){
		return (args) ->{
			//guarda jugadores de prueba
			Player p1 = new Player("j.bauer@ctu.gov");
			Player p2 = new Player("c.obrian@ctu.gov");
			Player p3 = new Player("kim_bauer@gmail.com");
			Player p4 = new Player("t.almeida@ctu.gov");
			playerRepository.saveAll(Arrays.asList(p1,p2,p3,p4));

			Date date = new Date();

			Game g1 = new Game(date);
			Game g2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game g3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g4 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g5 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g6 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g7 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			Game g8 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			gameRepository.saveAll(Arrays.asList(g1,g2,g3,g4,g5,g6,g7,g8));


			GamePlayer gp1 = new GamePlayer(date, g1, p1);
			GamePlayer gp2 = new GamePlayer(date, g1, p2);
			GamePlayer gp3 = new GamePlayer(date, g2, p1);
			GamePlayer gp4 = new GamePlayer(date, g2, p2);
			GamePlayer gp5 = new GamePlayer(date, g3, p2);
			GamePlayer gp6 = new GamePlayer(date, g3, p4);
			GamePlayer gp7 = new GamePlayer(date, g4, p2);
			GamePlayer gp8 = new GamePlayer(date, g4, p1);
			GamePlayer gp9 = new GamePlayer(date, g5, p4);
			GamePlayer gp10 = new GamePlayer(date, g5, p1);
			GamePlayer gp11 = new GamePlayer(date, g6, p3);
			GamePlayer gp12 = new GamePlayer(date, g7, p4);
			GamePlayer gp13 = new GamePlayer(date, g8, p3);
			GamePlayer gp14 = new GamePlayer(date, g8, p4);
			gamePlayerRepository.saveAll(Arrays.asList(gp1,gp2,gp3,gp4,gp5,gp6,gp7,gp8,gp9,gp10,gp11,gp12,gp13,gp14));

			Ship ship1 = new Ship(gp1,"Destroyer", Arrays.asList("H2","H3","H4"));
			Ship ship2 = new Ship(gp1,"Submarine", Arrays.asList("E1","F1","G1"));
			Ship ship3 = new Ship(gp1,"Patrol Boat", Arrays.asList("B5","B4"));
			Ship ship4 = new Ship(gp2,"Destroyer", Arrays.asList("B5","C5","D5"));
			Ship ship5 = new Ship(gp2,"Patrol Boat", Arrays.asList("F1","F2"));
			shipRepository.saveAll(Arrays.asList(ship1,ship2,ship3,ship4,ship5));
		};

        }
	}

