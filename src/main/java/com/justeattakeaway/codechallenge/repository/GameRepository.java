package com.justeattakeaway.codechallenge.repository;

import com.justeattakeaway.codechallenge.model.Game;
import com.justeattakeaway.codechallenge.enums.GameState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String>, QuerydslPredicateExecutor<Game> {

    Game findGameByGameState(GameState gameState);

    boolean existsByGameState(GameState gameState);

}

