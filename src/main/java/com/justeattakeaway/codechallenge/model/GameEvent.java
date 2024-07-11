package com.justeattakeaway.codechallenge.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameEvent {

    String player;
    int operation;

}
