package com.justeattakeaway.codechallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {

    private String id;
    private String lastOnePlayed;
    private List<GameEvent> movesTillNow;
    private GameState gameState;

}
