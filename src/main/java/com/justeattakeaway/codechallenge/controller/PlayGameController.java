package com.justeattakeaway.codechallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justeattakeaway.codechallenge.model.PlayRequest;
import com.justeattakeaway.codechallenge.service.PlayGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("play")
public class PlayGameController {

    private final PlayGameService playGameService;

    public PlayGameController(PlayGameService playGameService) {
        this.playGameService = playGameService;
    }

    @PostMapping
    public ResponseEntity<String> playTurn(@RequestBody PlayRequest playRequest) throws JsonProcessingException {
        playGameService.playInManualMode(playRequest);
        return new ResponseEntity<>("Waiting for next players turn", HttpStatus.OK);
    }
}
