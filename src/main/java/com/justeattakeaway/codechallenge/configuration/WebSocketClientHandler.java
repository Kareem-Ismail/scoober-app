package com.justeattakeaway.codechallenge.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketClientHandler extends TextWebSocketHandler {

    private final URI serverUri = URI.create("ws://scoober-app-p2:8081/websocket-endpoint");

    private WebSocketSession session;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        latch.countDown();
        System.out.println("Connected to WebSocket server");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received message from server: " + message.getPayload());
    }

    public void sendMessage(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            System.out.println("WebSocket session is not open");
        }
    }

    public void connectToWebSocketServer(String url) {
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(this, url);
    }

    public boolean awaitConnection(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }
}
