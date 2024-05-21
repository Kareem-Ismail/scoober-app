package com.justeattakeaway.codechallenge;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ExampleRabbitMQ {

    @Bean
    public Queue myQueue() {
        return new Queue(QUEUE, false);
    }

    private static final String QUEUE = "rabbitMQ-Queue";

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void produceMessage() {
        rabbitTemplate.convertAndSend(QUEUE, "message-" + Instant.now());
    }

    @RabbitListener(queues = QUEUE)
    public void consumeMensage(String content) {
        System.out.println("Received from RabbitMQ: " + content);
    }


}
