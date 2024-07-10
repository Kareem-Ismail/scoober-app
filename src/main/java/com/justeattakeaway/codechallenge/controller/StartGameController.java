package com.justeattakeaway.codechallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justeattakeaway.codechallenge.service.StartGameService;
import com.justeattakeaway.codechallenge.model.StartGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("start")
public class StartGameController {

    @Autowired
    StartGameService startGameService;

    @PostMapping("/")
    public ResponseEntity<String> startGame(@RequestBody StartGameRequest startGameRequest) throws JsonProcessingException {
        startGameService.startGame(startGameRequest);
        return new ResponseEntity<>("RabbitMQ test done", HttpStatus.OK);
    }


}
