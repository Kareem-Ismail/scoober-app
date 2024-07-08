package com.justeattakeaway.codechallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ExampleRabbitMQ {

    private static final String QUEUE = "game-queue";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${PLAYER_NAME}")
    String playerName;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Queue gameQueue() {
        return new Queue(QUEUE, false);
    }

    public void startGame(StartGameRequest startGameRequest) throws JsonProcessingException {
        startGameRequest.setPlayerName(playerName);
        var s = objectMapper.writeValueAsString(startGameRequest);
        rabbitTemplate.convertAndSend(QUEUE, s);
    }

    @RabbitListener(queues = QUEUE)
    public void receiveMessage(Message message, Channel channel) throws Exception {
        try {
            // Process the message
            var startGameRequest = objectMapper.readValue(new String(message.getBody()), StartGameRequest.class);

            if (playerName.equals(startGameRequest.getPlayerName()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                System.out.println("Received message: " + startGameRequest);
            }

        } catch (Exception e) {
            // Handle the exception and decide whether to nack or requeue the message
            System.err.println("Error processing message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }


}
