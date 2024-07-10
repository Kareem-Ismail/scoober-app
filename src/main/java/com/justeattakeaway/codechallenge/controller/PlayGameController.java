package com.justeattakeaway.codechallenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayGameController {
    @GetMapping("/game/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("Game info endpoint");
    }
}
