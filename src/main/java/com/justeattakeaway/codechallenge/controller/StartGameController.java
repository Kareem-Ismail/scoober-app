package com.justeattakeaway.codechallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justeattakeaway.codechallenge.model.PlayerModeRequest;
import com.justeattakeaway.codechallenge.model.StartGameRequest;
import com.justeattakeaway.codechallenge.service.StartGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("start")
public class StartGameController {

    @Autowired
    StartGameService startGameService;

    @PostMapping("/create-game")
    public ResponseEntity<URI> startGame(@RequestBody StartGameRequest startGameRequest, UriComponentsBuilder uriComponentsBuilder) throws JsonProcessingException {
        Boolean isAutomatic = startGameRequest.getIsAutomatic();
        log.info("Player chose {} mode", isAutomatic ? "Automatic" : "Manual");
        String s = startGameService.startGame(startGameRequest);
        URI location = uriComponentsBuilder
                .path("/play/{id}")
                .buildAndExpand(s)
                .toUri();
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }

    @PatchMapping("/set-player-mode")
    public ResponseEntity<String> setPlayerMode(@RequestBody PlayerModeRequest playerModeRequest) throws JsonProcessingException {
        Boolean isAutomatic = playerModeRequest.getIsAutomatic();
        log.info("Player chose {} mode", isAutomatic ? "Automatic" : "Manual");
        startGameService.setGameMode(isAutomatic);
        return new ResponseEntity<>("Mode set successfully to " + (isAutomatic ? "Automatic" : "Manual"), HttpStatus.OK);
    }

}
