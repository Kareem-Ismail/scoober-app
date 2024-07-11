package com.justeattakeaway.codechallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameOverMessage {
    private String winner;
    private int numberOfMoves;
}
