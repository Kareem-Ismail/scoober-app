package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.model.GameState;
import com.justeattakeaway.codechallenge.model.StartGameRequest;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class StartGameService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${game.player.name}")
    String playerName;

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

        Game game = Game.builder().gameState(GameState.IN_PROGRESS)
                .gamingMode(Map.of(playerName, startGameRequest.getIsAutomatic()))
                .initialNumber(generateRandomNumber())
                .lastOnePlayed(playerName).build();

        gameRepository.save(game);
        var s = objectMapper.writeValueAsString(game);
        rabbitTemplate.convertAndSend(GAME_QUEUE, s);
    }

    public void setGameMode(boolean isAutomatic) {
        Game game = gameRepository.findGameByGameState(GameState.IN_PROGRESS);
        game.addPlayerGamingMode(playerName, isAutomatic);
        gameRepository.save(game);
    }

    @RabbitListener(queues = GAME_QUEUE)
    public void receiveMessage(Message message, Channel channel) throws Exception {
        try {
            // Process the message
            var game = objectMapper.readValue(new String(message.getBody()), Game.class);

            if (playerName.equals(game.getLastOnePlayed()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("A game started with {} \n" +
                        "Please select your gaming mode either Automatic or Manual or else it will be Automatic and choose the next operation whether +1, -1 or 0", game);
            }

        } catch (Exception e) {
            // Handle the exception and decide whether to nack or requeue the message
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    private int generateRandomNumber() {
        int randomNumber = (int) (Math.random() * 100);
        while (randomNumber == 1)
            randomNumber = (int) (Math.random() * 100);
        return randomNumber;
    }

}
