package com.justeattakeaway.codechallenge

import com.fasterxml.jackson.databind.ObjectMapper
import com.justeattakeaway.codechallenge.enums.GameState
import com.justeattakeaway.codechallenge.enums.MetaData
import com.justeattakeaway.codechallenge.infrastructure.MessageBroker
import com.justeattakeaway.codechallenge.model.Game
import com.justeattakeaway.codechallenge.model.GameEvent
import com.justeattakeaway.codechallenge.model.PlayRequest
import com.justeattakeaway.codechallenge.repository.GameRepository
import com.justeattakeaway.codechallenge.service.PlayGameService
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest
import spock.lang.Specification

@SpringRabbitTest
class PlayGameServiceSpec extends Specification {


    String playerName = "playerName"
    private static final String GAME_OVER_QUEUE = "game-over-queue"
    MessageBroker messageBroker = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    GameRepository gameRepository = Mock()
    MetaData metaData = Mock()
    PlayGameService playGameService = new PlayGameService(messageBroker, objectMapper, gameRepository, metaData)

    def setup() {
        metaData.getName() >> "playerName"
    }

    def "test playInManualMode with valid request"() {
        given:
        PlayRequest playRequest = new PlayRequest(1, "game-id")
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(26).build()
        game.addNewEvent(new GameEvent("other-player", 0))
        gameRepository.findById("game-id") >> Optional.of(game)

        when:
        playGameService.playInManualMode(playRequest)

        then:
        1 * gameRepository.save(_)
        1 * messageBroker.sendMessage(_, _)
        notThrown(IllegalStateException)
    }

    def "test playInManualMode with invalid turn"() {
        given:
        PlayRequest playRequest = new PlayRequest(1, "game-id")
        Game game = Game.builder().id("game-id").lastOnePlayed(playerName).initialNumber(5).build()
        game.setLastOnePlayed(playerName)
        gameRepository.findById("game-id") >> Optional.of(game)

        when:
        playGameService.playInManualMode(playRequest)

        then:
        thrown(IllegalStateException)
    }

    def "test playInAutomaticMode with valid game"() {
        given:
        String gameId = "game-id"
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").build()
        game.setId(gameId)
        game.setGameState(GameState.IN_PROGRESS)
        gameRepository.findById(gameId) >> Optional.of(game)

        when:
        playGameService.playInAutomaticMode(gameId)

        then:
        1 * gameRepository.save(_)
        1 * messageBroker.sendMessage(_, _)
        notThrown(IllegalStateException)
    }

    def "test currentMove a game over"() {
        given:
        String gameId = "game-id"
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(6)
                .gameState(GameState.IN_PROGRESS).build()
        game.addNewEvent(new GameEvent("other-player", 0))
        game.setGameState(GameState.IN_PROGRESS)
        gameRepository.findById(gameId) >> Optional.of(game)

        when:
        playGameService.currentMove(game, 1)

        then:
        1 * gameRepository.save(_)
        1 * messageBroker.sendMessage(GAME_OVER_QUEUE, _)
        notThrown(IllegalStateException)
    }

    def "test currentMove with an invalid operation"() {
        given:
        String gameId = "game-id"
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(6)
                .gameState(GameState.IN_PROGRESS).build()
        game.addNewEvent(new GameEvent("other-player", 0))
        game.setGameState(GameState.IN_PROGRESS)
        gameRepository.findById(gameId) >> Optional.of(game)

        when:
        playGameService.currentMove(game, 0)

        then:
        thrown(IllegalStateException)
    }

    def "test playInAutomaticMode with game over state"() {
        given:
        String gameId = "game-id"
        Game game = Game.builder().id("game-id").lastOnePlayed(playerName).gameState(GameState.GAME_OVER).build()
        gameRepository.findById(gameId) >> Optional.of(game)

        when:
        playGameService.playInAutomaticMode(gameId)

        then:
        thrown(IllegalStateException)
    }

//    def "test listener methods behavior with a message that should be ACKNOWLEDGED"() {
//        given:
//        String jsonMessage = "{\"winner\":\"player-1\",\"numberOfMoves\":3}"
//        def channel = Mock(Channel.class)
//        when:
//        playGameService.receiveGameOverMessage(new Message(jsonMessage.getBytes()), channel)
//        then:
//        0 * gameRepository.save(_)
//        0 * messageBroker.sendMessage(_, _)
//        1 * channel.basicAck(_, _)
//
//    }

//    def "test listener methods behavior with a message that should be IGNORED"() {
//        given:
//        String jsonMessage = "{\"winner\":\"PlayerName.getName()\",\"numberOfMoves\":3}"
//        def channel = Mock(Channel.class)
//        when:
//        playGameService.receiveGameOverMessage(new Message(jsonMessage.getBytes()), channel)
//        then:
//        0 * gameRepository.save(_)
//        0 * messageBroker.sendMessage(_, _)
//        1 * channel.basicNack(_, _, _)
//
//    }
}
