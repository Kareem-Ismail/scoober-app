package com.justeattakeaway.codechallenge.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.enums.MetaData;
import com.justeattakeaway.codechallenge.model.GameOverMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.justeattakeaway.codechallenge.enums.QueueName.GAME_OVER_QUEUE;

@Slf4j
@Component
public class GameOverListener {

    private final ObjectMapper objectMapper;
    private final MetaData metaData;

    public GameOverListener(ObjectMapper objectMapper, MetaData metaData) {
        this.objectMapper = objectMapper;
        this.metaData = metaData;
    }

    @RabbitListener(queues = GAME_OVER_QUEUE)
    public void receiveGameOverMessage(Message message, Channel channel) throws Exception {
        try {
            var gameOverMessage = objectMapper.readValue(new String(message.getBody()), GameOverMessage.class);
            if (metaData.getName().equals(gameOverMessage.getWinner()))
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

}
