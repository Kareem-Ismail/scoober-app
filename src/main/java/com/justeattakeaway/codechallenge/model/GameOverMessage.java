package com.justeattakeaway.codechallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameOverMessage {
    private String winner;
    private int numberOfMoves;
}
