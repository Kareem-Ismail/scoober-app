package com.justeattakeaway.codechallenge.enums;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetaData {

    @Value("${game.player.name}")
    private String playerName = "playerName";

    public String getName() {
        return playerName;
    }

}
