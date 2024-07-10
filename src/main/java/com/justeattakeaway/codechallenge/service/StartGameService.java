package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.model.GameState;
import com.justeattakeaway.codechallenge.model.PlayerGamingMode;
import com.justeattakeaway.codechallenge.model.StartGameRequest;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class StartGameService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${game.player.name}")
    String playerName;

    @Value("${server.port}")
    String portNumber;

    private static final String GAME_QUEUE = "game-queue";

    private final ObjectMapper objectMapper;

    private final GameRepository gameRepository;

    public StartGameService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, GameRepository gameRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
    }

    @Bean
    public Queue gameQueue() {
        return new Queue(GAME_QUEUE, false);
    }

    public void startGame(StartGameRequest startGameRequest) throws JsonProcessingException {

        if (gameRepository.existsByGameState(GameState.IN_PROGRESS)) {
            Game gameByGameState = gameRepository.findGameByGameState(GameState.IN_PROGRESS);
            boolean isCurrentPlayerTurn = !gameByGameState.getLastOnePlayed().equals(playerName);
            throw new IllegalStateException(String.format("There is a game already in progress with ID: %s and its %s turn", gameByGameState.getId(), isCurrentPlayerTurn ? "your" : "the other player's"));
        }

        Game game = Game.builder().gameState(GameState.IN_PROGRESS).
                playerGamingMode(new PlayerGamingMode(playerName, startGameRequest.isAutomatic()))
                .initialNumber(generateRandomNumber())
                .lastOnePlayed(playerName)
                .serverPortNumber(portNumber).build();

        gameRepository.save(game);
        var s = objectMapper.writeValueAsString(game);
        rabbitTemplate.convertAndSend(GAME_QUEUE, s);
    }

    private int generateRandomNumber() {
        int randomNumber = (int) (Math.random() * 100);
        while (randomNumber == 1)
            randomNumber = (int) (Math.random() * 100);
        return randomNumber;
    }

}
