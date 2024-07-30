package com.justeattakeaway.codechallenge.model;

import com.justeattakeaway.codechallenge.enums.GameState;
import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private String id;
    @Setter(AccessLevel.NONE)
    private List<GameEvent> events;
    private int initialNumber;
    private GameState gameState;
    @Setter(AccessLevel.NONE)
    private Map<String, Boolean> gamingMode;
    private String lastOnePlayed;

    public String toString() {
        return String.format("Game ID: %s and initial number: %d", id, initialNumber);
    }

    public void addPlayerGamingMode(String playerName, Boolean isAutomatic) {
        gamingMode.put(playerName, isAutomatic);
    }

    public int getLastNumber() {
        int currentNumber = initialNumber;
        if (events != null) {
            List<Integer> operations = events.stream().map(GameEvent::getOperation).toList();
            for (Integer operation : operations) {
                currentNumber = (currentNumber + operation) / 3;
            }
        }
        return currentNumber;
    }

    public void addNewEvent(GameEvent gameEvent) {
        if(events == null)
            events = new ArrayList<>();
        events.add(gameEvent);
    }
}
