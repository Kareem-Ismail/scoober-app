package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.enums.MetaData;
import com.justeattakeaway.codechallenge.infrastructure.MessageBroker;
import com.justeattakeaway.codechallenge.model.*;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.justeattakeaway.codechallenge.enums.GameState.GAME_OVER;
import static com.justeattakeaway.codechallenge.enums.QueueName.GAME_OVER_QUEUE;
import static com.justeattakeaway.codechallenge.enums.QueueName.PLAY_GAME_QUEUE;

@Service
public class PlayGameService {


    private static final Logger log = LoggerFactory.getLogger(PlayGameService.class);

    private final MessageBroker<String> messageBroker;

    private final ObjectMapper objectMapper;

    private final GameRepository gameRepository;

    private final MetaData metaData;

    public PlayGameService(MessageBroker<String> messageBroker, ObjectMapper objectMapper, GameRepository gameRepository, MetaData metaData) {
        this.messageBroker = messageBroker;
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
        this.metaData = metaData;
    }

    public GameDTO getGame(String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        return new GameDTO(game.getId(), game.getLastOnePlayed(), game.getEvents(), game.getGameState());
    }

    public void playInManualMode(PlayRequest playRequest) throws JsonProcessingException {
        Game game = gameRepository.findById(playRequest.getGameId()).orElseThrow();
        currentMove(game, playRequest.getOperation());
    }

    public void playInAutomaticMode(String gameId) throws JsonProcessingException {
        Game game = gameRepository.findById(gameId).orElseThrow();
        if (game.getGameState().equals(GAME_OVER))
            throw new IllegalStateException("Game is over");
        int lastNumber = game.getLastNumber();
        int operation = calculateMove(lastNumber);
        log.info("Calculated move is {}", operation);
        currentMove(game, operation);
    }

    public void currentMove(Game game, int operation) throws JsonProcessingException {
        if (game.getLastOnePlayed().equals(metaData.getName()))
            throw new IllegalStateException("This is not your turn");
        int newNumber = game.getLastNumber() + operation;
        log.info("New number is {}", newNumber / 3);
        verifyNumberDivisibleByThree(newNumber);
        boolean gameOver = false;
        if (newNumber / 3 == 1) {
            game.setGameState(GAME_OVER);
            GameOverMessage gameOverMessage = new GameOverMessage(metaData.getName(), game.getEvents().size());
            log.info("Wohooo ! You won the game with a total number of {} moves, there is no game in progress now if you want to start a new one ;)", gameOverMessage.getNumberOfMoves());
            var gameOverMessageString = objectMapper.writeValueAsString(gameOverMessage);
            messageBroker.sendMessage(GAME_OVER_QUEUE, gameOverMessageString);
            gameOver = true;
        }
        game.addNewEvent(new GameEvent(metaData.getName(), operation));
        game.setLastOnePlayed(metaData.getName());
        gameRepository.save(game);
        if (!gameOver) {
            var s = objectMapper.writeValueAsString(game);
            messageBroker.sendMessage(PLAY_GAME_QUEUE, s);
            log.info("Operation was successful, waiting for the other player");
        }
    }

    private void verifyNumberDivisibleByThree(int newNumber) {
        if (newNumber % 3 != 0) {
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
