package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.enums.MetaData;
import com.justeattakeaway.codechallenge.infrastructure.MessageBroker;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.enums.GameState;
import com.justeattakeaway.codechallenge.model.StartGameRequest;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.justeattakeaway.codechallenge.enums.QueueName.CREATE_NEW_GAME_QUEUE;

@Slf4j
@Service
public class StartGameService {

    private final MessageBroker<String> messageBroker;

    private final ObjectMapper objectMapper;

    private final GameRepository gameRepository;

    private final PlayGameService playGameService;

    private final MetaData metaData;

    public StartGameService(MessageBroker<String> messageBroker, ObjectMapper objectMapper, GameRepository gameRepository, PlayGameService playGameService, MetaData metaData) {
        this.messageBroker = messageBroker;
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
        this.playGameService = playGameService;
        this.metaData = metaData;
    }

    public Game startGame(StartGameRequest startGameRequest) throws JsonProcessingException {

        if (gameRepository.existsByGameState(GameState.IN_PROGRESS)) {
            Game gameByGameState = gameRepository.findGameByGameState(GameState.IN_PROGRESS);
            boolean isCurrentPlayerTurn = !gameByGameState.getLastOnePlayed().equals(metaData.getName());
            throw new IllegalStateException(String.format("There is a game already in progress with ID: %s and its %s turn", gameByGameState.getId(), isCurrentPlayerTurn ? "your" : "the other player's"));
        }

        Game game = Game.builder().gameState(GameState.IN_PROGRESS)
                .gamingMode(Map.of(metaData.getName(), startGameRequest.getIsAutomatic()))
                .initialNumber(generateRandomNumber())
                .lastOnePlayed(metaData.getName()).build();

        Game newGame = gameRepository.save(game);
        var s = objectMapper.writeValueAsString(newGame);
        messageBroker.sendMessage(CREATE_NEW_GAME_QUEUE, s);

        return newGame;
    }

    public void setGameMode(boolean isAutomatic) throws JsonProcessingException {
        Game game = gameRepository.findGameByGameState(GameState.IN_PROGRESS);
        game.addPlayerGamingMode(metaData.getName(), isAutomatic);
        gameRepository.save(game);
        if (isAutomatic)
            playGameService.playInAutomaticMode(game.getId());
    }

    private int generateRandomNumber() {
        int randomNumber = (int) (Math.random() * 100) + 1;
        while (randomNumber == 1)
            randomNumber = (int) (Math.random() * 100) + 1;
        return randomNumber;
    }

}
