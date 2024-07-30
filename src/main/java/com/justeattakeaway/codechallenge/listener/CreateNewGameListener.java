package com.justeattakeaway.codechallenge.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.enums.MetaData;
import com.justeattakeaway.codechallenge.model.Game;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.justeattakeaway.codechallenge.enums.QueueName.CREATE_NEW_GAME_QUEUE;

@Slf4j
@Component
public class CreateNewGameListener {

    private final ObjectMapper objectMapper;
    private final MetaData metaData;

    public CreateNewGameListener(ObjectMapper objectMapper, MetaData metaData) {
        this.objectMapper = objectMapper;
        this.metaData = metaData;
    }


    @RabbitListener(queues = CREATE_NEW_GAME_QUEUE)
    public void receiveCreateNewGameMessage(Message message, Channel channel) throws Exception {
        try {
            var game = objectMapper.readValue(new String(message.getBody()), Game.class);

            if (metaData.getName().equals(game.getLastOnePlayed()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("A game started with {} \n" +
                        "Please select your gaming mode either Automatic or Manual or else it will be Automatic and choose the next operation whether +1, -1 or 0", game);
            }

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

}
