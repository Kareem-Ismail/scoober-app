package com.justeattakeaway.codechallenge.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
@QueryEntity
public class Game {

    @Id
    private String id;
    private String events;
    private int initialNumber;
    private GameState gameState;
    private PlayerGamingMode playerGamingMode;
    private String lastOnePlayed;
    private String serverPortNumber;

    public String toString() {
        return String.format("Documents data is %s and ID: %s and state is %s", events, id, gameState.toString());
    }
}
