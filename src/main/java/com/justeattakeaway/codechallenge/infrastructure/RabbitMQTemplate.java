package com.justeattakeaway.codechallenge.infrastructure;

import com.justeattakeaway.codechallenge.enums.QueueName;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.justeattakeaway.codechallenge.enums.QueueName.CREATE_NEW_GAME_QUEUE;

@Component
public class RabbitMQTemplate implements MessageBroker<String> {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public Queue playGameQueue() {
        return new Queue(QueueName.PLAY_GAME_QUEUE, false);
    }

    @Bean
    public Queue gameOverQueue() {
        return new Queue(QueueName.GAME_OVER_QUEUE, false);
    }

    @Bean
    public Queue createNewGameQueue() {
        return new Queue(CREATE_NEW_GAME_QUEUE, false);
    }

    @Override
    public void sendMessage(String queueName, String object) {
        rabbitTemplate.convertAndSend(queueName, object);
    }
}
