package com.justeattakeaway.codechallenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.configuration.WebSocketClientHandler;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class PlayGameService {

    private static final String GAME_QUEUE = "game-queue";

    private final ObjectMapper objectMapper;

    @Value("${game.player.name}")
    private String playerName;

    @Value("${server.port}")
    String portNumber;

    private final WebSocketClientHandler myWebSocketClient;

    private final GameRepository gameRepository;

    public PlayGameService(ObjectMapper objectMapper, WebSocketClientHandler myWebSocketClient, GameRepository gameRepository) {
        this.objectMapper = objectMapper;
        this.myWebSocketClient = myWebSocketClient;
        this.gameRepository = gameRepository;
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
                System.out.println("Received message: " + game);
                String url = String.format("ws://localhost:%s/game", game.getServerPortNumber());
                if (!Objects.equals(portNumber, game.getServerPortNumber())) {
                    initiateConnection(url);
                    if (myWebSocketClient.isConnected()) {
                        myWebSocketClient.sendMessage("Hello I'm connected");
                    } else {
                        System.out.println("Failed to establish WebSocket connection");
                    }
                }
                if (!game.getPlayerGamingMode().isAutomatic()) {
                    System.out.println("Automatic game");
                }
                game.setLastOnePlayed(playerName);
                gameRepository.save(game);
            }

        } catch (Exception e) {
            // Handle the exception and decide whether to nack or requeue the message
            System.err.println("Error processing message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    public void initiateConnection(String url) {
        try {
            myWebSocketClient.connectToWebSocketServer(url);
            if (myWebSocketClient.awaitConnection(5, TimeUnit.SECONDS)) {
                System.out.println("Successfully connected to WebSocket server");
            } else {
                System.out.println("Failed to connect to WebSocket server within timeout");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processMessage(WebSocketSession session, String message) {
//        // Parse the message
//        int number = Integer.parseInt(message);
//
//        // Calculate the next move
//        int move = calculateMove(number);
//        int result = (number + move) / 3;
//
//        // Broadcast the move and result
//        String response = String.format("Added: %d, Result: %d", move, result);
//        session.sendMessage(new TextMessage(response));
//
//        // If result is 1, end the game
//        if (result == 1) {
//            session.sendMessage(new TextMessage("Game over"));
//            session.close();
//        }
        System.out.println("I player " + playerName + " received a message:" + message);
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
