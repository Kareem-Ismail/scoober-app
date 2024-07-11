package com.justeattakeaway.codechallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayRequest {

    private int operation;
    private String gameId;

}
