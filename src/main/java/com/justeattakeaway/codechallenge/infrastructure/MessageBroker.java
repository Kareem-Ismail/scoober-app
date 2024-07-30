package com.justeattakeaway.codechallenge.infrastructure;

public interface MessageBroker<T extends String> {

    void sendMessage(String queueName, T object);

}
