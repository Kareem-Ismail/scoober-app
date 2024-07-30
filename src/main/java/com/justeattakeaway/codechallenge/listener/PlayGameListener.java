package com.justeattakeaway.codechallenge.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justeattakeaway.codechallenge.enums.MetaData;
import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.model.GameDTO;
import com.justeattakeaway.codechallenge.repository.GameRepository;
import com.justeattakeaway.codechallenge.service.PlayGameService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.justeattakeaway.codechallenge.enums.QueueName.PLAY_GAME_QUEUE;

@Slf4j
@Component
public class PlayGameListener {

    private final PlayGameService playGameService;
    private final ObjectMapper objectMapper;
    private final GameRepository gameRepository;
    private final MetaData metaData;

    public PlayGameListener(PlayGameService playGameService, ObjectMapper objectMapper, GameRepository gameRepository, MetaData metaData) {
        this.playGameService = playGameService;
        this.objectMapper = objectMapper;
        this.gameRepository = gameRepository;
        this.metaData = metaData;
    }

    @RabbitListener(queues = PLAY_GAME_QUEUE)
    public void receivePlayGameMessage(Message message, Channel channel) throws Exception {
        try {
            var gameDTO = objectMapper.readValue(new String(message.getBody()), GameDTO.class);
            Game game = gameRepository.findById(gameDTO.getId()).orElseThrow();
            if (metaData.getName().equals(gameDTO.getLastOnePlayed()))
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                if (game.getGamingMode().get(metaData.getName()))
                    playGameService.playInAutomaticMode(gameDTO.getId());
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

}
