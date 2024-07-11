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

@Slf4j
@RestController
@RequestMapping("start")
public class StartGameController {

    @Autowired
    StartGameService startGameService;

    @PostMapping("/create-game")
    public ResponseEntity<String> startGame(@RequestBody StartGameRequest startGameRequest) throws JsonProcessingException {
        Boolean isAutomatic = startGameRequest.getIsAutomatic();
        log.info("Player chose {} mode", isAutomatic ? "Automatic" : "Manual");
        startGameService.startGame(startGameRequest);
        return new ResponseEntity<>("Game created successfully", HttpStatus.OK);
    }

    @PatchMapping("/set-player-mode")
    public ResponseEntity<String> setPlayerMode(@RequestBody PlayerModeRequest playerModeRequest) {
        Boolean isAutomatic = playerModeRequest.getIsAutomatic();
        log.info("Player chose {} mode", isAutomatic ? "Automatic" : "Manual");
        startGameService.setGameMode(isAutomatic);
        return new ResponseEntity<>("Mode set successfully to " + (isAutomatic ? "Automatic" : "Manual"), HttpStatus.OK);
    }

}
