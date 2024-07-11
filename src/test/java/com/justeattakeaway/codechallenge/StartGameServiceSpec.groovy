package com.justeattakeaway.codechallenge

import com.fasterxml.jackson.databind.ObjectMapper
import com.justeattakeaway.codechallenge.model.Game
import com.justeattakeaway.codechallenge.model.GameState
import com.justeattakeaway.codechallenge.model.StartGameRequest
import com.justeattakeaway.codechallenge.repository.GameRepository
import com.justeattakeaway.codechallenge.service.PlayGameService
import com.justeattakeaway.codechallenge.service.StartGameService
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest
import spock.lang.Specification

@SpringRabbitTest
class StartGameServiceSpec extends Specification {

    private static final String CREATE_NEW_GAME_QUEUE = "create-new-game-queue"
    private RabbitTemplate rabbitTemplate = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    private GameRepository gameRepository = Mock()
    private PlayGameService playGameService = Mock()

    private StartGameService startGameService = new StartGameService(rabbitTemplate, objectMapper, gameRepository, playGameService)

    def "test startGame method with game already in progress"() {
        given:
        StartGameRequest startGameRequest = new StartGameRequest(true)
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(5).gameState(GameState.IN_PROGRESS).build()

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
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(5).gameState(GameState.IN_PROGRESS).build()

        gameRepository.existsByGameState(GameState.IN_PROGRESS) >> false
        gameRepository.findGameByGameState(GameState.IN_PROGRESS) >> game
        gameRepository.save(_ as Game) >> game

        when:
        startGameService.startGame(startGameRequest)

        then:
        1 * gameRepository.save(_)
        1 * rabbitTemplate.convertAndSend(CREATE_NEW_GAME_QUEUE, _)
    }

    def "test set gaming mode for a player"() {
        given:
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(5)
                .gameState(GameState.IN_PROGRESS).gamingMode(new HashMap<String, Boolean>()).build()

        gameRepository.existsByGameState(GameState.IN_PROGRESS) >> false
        gameRepository.findGameByGameState(GameState.IN_PROGRESS) >> game
        gameRepository.save(_ as Game) >> game

        when:
        startGameService.setGameMode(true)

        then:
        1 * gameRepository.save(_)
        1 * playGameService.playInAutomaticMode(_)
    }

    def "test listener methods behavior with a message that should be IGNORED"() {
        given:
        String jsonMessage = "{\"lastOnePlayed\":\"playerName\"}"
        def channel = Mock(Channel.class)
        when:
        startGameService.receiveCreateNewGameMessage(new Message(jsonMessage.getBytes()), channel)
        then:
        1 * channel.basicNack(_, _, _)

    }

    def "test listener methods with a message that should be ACKNOWLEDGED"() {
        given:
        String jsonMessage = "{\"lastOnePlayed\":\"otherPlayer\"}"
        def channel = Mock(Channel.class)
        when:
        startGameService.receiveCreateNewGameMessage(new Message(jsonMessage.getBytes()), channel)
        then:
        1 * channel.basicAck(_, _)

    }

}

