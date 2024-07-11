package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.model.*;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class PlayGameService {

    private static final String PLAY_GAME_QUEUE = "play-game-queue";
    private static final String GAME_OVER_QUEUE = "game-over-queue";
    private static final Logger log = LoggerFactory.getLogger(PlayGameService.class);
    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    @Value("${game.player.name}")
    private String playerName = "playerName";

    private final GameRepository gameRepository;

    public PlayGameService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, GameRepository gameRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
    }

    @Bean
    public Queue playGameQueue() {
        return new Queue(PLAY_GAME_QUEUE, false);
    }

    @Bean
    public Queue gameOverQueue() {
        return new Queue(GAME_OVER_QUEUE, false);
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
        if (game.getGameState().equals(GameState.GAME_OVER))
            throw new IllegalStateException("Game is over");
        int lastNumber = game.getLastNumber();
        int operation = calculateMove(lastNumber);
        log.info("Calculated move is {}", operation);
        currentMove(game, operation);
    }

    public void currentMove(Game game, int operation) throws JsonProcessingException {
        if (game.getLastOnePlayed().equals(playerName))
            throw new IllegalStateException("This is not your turn");
        int newNumber = game.getLastNumber() + operation;
        log.info("New number is {}", newNumber / 3);
        verifyNumberDivisibleByThree(newNumber);
        boolean gameOver = false;
        if (newNumber / 3 == 1) {
            game.setGameState(GameState.GAME_OVER);
            GameOverMessage gameOverMessage = new GameOverMessage(playerName, game.getEvents().size());
            log.info("Wohooo ! You won the game with a total number of {} moves, there is no game in progress now if you want to start a new one ;)", gameOverMessage.getNumberOfMoves());
            var gameOverMessageString = objectMapper.writeValueAsString(gameOverMessage);
            rabbitTemplate.convertAndSend(GAME_OVER_QUEUE, gameOverMessageString);
            gameOver = true;
        }
        game.addNewEvent(new GameEvent(playerName, operation));
        game.setLastOnePlayed(playerName);
        gameRepository.save(game);
        if (!gameOver) {
            var s = objectMapper.writeValueAsString(game);
            rabbitTemplate.convertAndSend(PLAY_GAME_QUEUE, s);
            log.info("Operation was successful, waiting for the other player");
        }
    }

    @RabbitListener(queues = PLAY_GAME_QUEUE)
    public void receiveMessage(Message message, Channel channel) throws Exception {
        try {
            var gameDTO = objectMapper.readValue(new String(message.getBody()), GameDTO.class);
            Game game = gameRepository.findById(gameDTO.getId()).orElseThrow();
            if (playerName.equals(gameDTO.getLastOnePlayed()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                if (game.getGamingMode().get(playerName))
                    playInAutomaticMode(gameDTO.getId());
                else
                    log.info("Its your turn and the current number is {}", game.getLastNumber());
                log.info("A gameDTO started with {} \n" +
                        "Please select your gaming mode either Automatic or Manual or else it will be Automatic and choose the next operation whether +1, -1 or 0", gameDTO);
            }

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(queues = GAME_OVER_QUEUE)
    public void receiveGameOverMessage(Message message, Channel channel) throws Exception {
        try {
            var gameOverMessage = objectMapper.readValue(new String(message.getBody()), GameOverMessage.class);
            if (playerName.equals(gameOverMessage.getWinner()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("Sorry you lost the game, good luck next time\n" +
                        "Player {} won with a total number of {} moves and there is no game in progress if you want to start a new one ;)", gameOverMessage.getWinner(), gameOverMessage.getNumberOfMoves());
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
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
