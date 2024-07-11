package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.model.GameEvent;
import com.justeattakeaway.codechallenge.model.GameState;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PlayGameService {

    private static final String GAME_QUEUE = "game-queue";
    private static final Logger log = LoggerFactory.getLogger(PlayGameService.class);

    private final ObjectMapper objectMapper;

    @Value("${game.player.name}")
    private String playerName;

    private final GameRepository gameRepository;

    public PlayGameService(ObjectMapper objectMapper, GameRepository gameRepository) {
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
    }

    public void currentMove(int operation) {
        Game game = gameRepository.findGameByGameState(GameState.IN_PROGRESS);
        int newNumber = game.getLastNumber() + operation;
        verifyNumberDivisibleByThree(newNumber);

        game.addNewEvent(new GameEvent(playerName, operation));
        game.setLastOnePlayed(playerName);

        gameRepository.save(game);

        log.info("Operation was successful, waiting for the other player");
    }

    private void verifyNumberDivisibleByThree(int newNumber) {
        if(newNumber % 3 != 0) {
            throw new IllegalStateException("New Number not divisible by three, please choose another operation");
        }
    }

    private int calculateMove(int number) {
        if ((number + 1) % 3 == 0) {
            return 1;
        } else if ((number - 1) % 3 == 0) {
            return -1;
        } else {
            return 0;
        }
    }


}
