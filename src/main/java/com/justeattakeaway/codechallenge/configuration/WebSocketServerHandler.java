package com.justeattakeaway.codechallenge.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketServerHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Called when a new WebSocket connection is established
        System.out.println("New WebSocket connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Called when a WebSocket message is received from the client
        System.out.println("Received message: " + message.getPayload());
        // You can send a response back to the client if needed
        session.sendMessage(new TextMessage("Received your message: " + message.getPayload()));
    }
}
