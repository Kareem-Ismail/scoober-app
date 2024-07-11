package com.justeattakeaway.codechallenge.controller;

import com.justeattakeaway.codechallenge.service.PlayGameService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayGameController {
    private final PlayGameService playGameService;

    public PlayGameController(PlayGameService playGameService) {
        this.playGameService = playGameService;
    }
}
