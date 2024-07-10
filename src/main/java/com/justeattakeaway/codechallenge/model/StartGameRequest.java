package com.justeattakeaway.codechallenge.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameRequest {

    private boolean isAutomatic;
    private int initialNumber;

}
