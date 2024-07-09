package com.justeattakeaway.codechallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("start")
public class ExampleApi {

    @Autowired
    ExampleH2DB exampleH2DB;
//    @Autowired
//    ExampleMongoDB exampleMongoDB;
    @Autowired
    ExampleRabbitMQ exampleRabbitMQ;


    @GetMapping("h2")
    public ResponseEntity<String> testH2DB() {
        exampleH2DB.testRepository();
        return new ResponseEntity<>("H2DB test done", HttpStatus.OK);
    }

//    @GetMapping("mongo")
//    public ResponseEntity<String> testMongoDB() {
//        exampleMongoDB.testRepository();
//        return new ResponseEntity<>("MongoDB test done", HttpStatus.OK);
//    }

    @PostMapping("/")
    public ResponseEntity<String> testRabbitMQ(@RequestBody StartGameRequest startGameRequest) throws JsonProcessingException {
        exampleRabbitMQ.startGame(startGameRequest);
        return new ResponseEntity<>("RabbitMQ test done", HttpStatus.OK);
    }

}
