package com.justeattakeaway.codechallenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class ExampleApi {

    @Autowired
    ExampleH2DB exampleH2DB;
    @Autowired
    ExampleMongoDB exampleMongoDB;
    @Autowired
    ExampleKafka exampleKafka;
    @Autowired
    ExampleRabbitMQ exampleRabbitMQ;

    @GetMapping("hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @GetMapping("h2")
    public ResponseEntity<String> testH2DB() {
        exampleH2DB.testRepository();
        return new ResponseEntity<>("H2DB test done", HttpStatus.OK);
    }

    @GetMapping("mongo")
    public ResponseEntity<String> testMongoDB() {
        exampleMongoDB.testRepository();
        return new ResponseEntity<>("MongoDB test done", HttpStatus.OK);
    }

    @GetMapping("rabbit")
    public ResponseEntity<String> testRabbitMQ() {
        exampleRabbitMQ.produceMessage();
        return new ResponseEntity<>("RabbitMQ test done", HttpStatus.OK);
    }

    @GetMapping("kafka")
    public ResponseEntity<String> testKafka() {
        exampleKafka.produceMessage();
        return new ResponseEntity<>("Kafka test done", HttpStatus.OK);
    }

}
