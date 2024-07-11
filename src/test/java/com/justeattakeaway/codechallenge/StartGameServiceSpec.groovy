package com.justeattakeaway.codechallenge

import com.fasterxml.jackson.databind.ObjectMapper
import com.justeattakeaway.codechallenge.model.Game
import com.justeattakeaway.codechallenge.model.GameState
import com.justeattakeaway.codechallenge.model.StartGameRequest
import com.justeattakeaway.codechallenge.repository.GameRepository
import com.justeattakeaway.codechallenge.service.PlayGameService
import com.justeattakeaway.codechallenge.service.StartGameService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest
import spock.lang.Specification

@SpringRabbitTest
class StartGameServiceSpec extends Specification {

    private static final String CREATE_NEW_GAME_QUEUE = "create-new-game-queue"
    private RabbitTemplate rabbitTemplate = Mock()
    private ObjectMapper objectMapper = Mock()
    private GameRepository gameRepository = Mock()
    private PlayGameService playGameService = Mock()

    private StartGameService startGameService = new StartGameService(rabbitTemplate, objectMapper, gameRepository, playGameService)

    def "test startGame method with game already in progress"() {
        given:
        StartGameRequest startGameRequest = new StartGameRequest(true)
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(5).build()
        game.setGameState(GameState.IN_PROGRESS)
        game.setLastOnePlayed("otherPlayer")

        gameRepository.existsByGameState(GameState.IN_PROGRESS) >> true
        gameRepository.findGameByGameState(GameState.IN_PROGRESS) >> game

        when:
        startGameService.startGame(startGameRequest)

        then:
        thrown(IllegalStateException)
    }

    def "test startGame method with no games in progress"() {
        given:
        StartGameRequest startGameRequest = new StartGameRequest(true)
        Game game = Game.builder().lastOnePlayed("other-player").initialNumber(5).build()
        game.setGameState(GameState.IN_PROGRESS)
        game.setLastOnePlayed("otherPlayer")

        gameRepository.existsByGameState(GameState.IN_PROGRESS) >> false
        gameRepository.findGameByGameState(GameState.IN_PROGRESS) >> game

        when:
        startGameService.startGame(startGameRequest)

        then:
        1 * gameRepository.save(_)
        1 * rabbitTemplate.convertAndSend(CREATE_NEW_GAME_QUEUE, _)
    }

}

