package com.justeattakeaway.codechallenge

import com.fasterxml.jackson.databind.ObjectMapper
import com.justeattakeaway.codechallenge.model.Game
import com.justeattakeaway.codechallenge.model.GameEvent
import com.justeattakeaway.codechallenge.model.GameState
import com.justeattakeaway.codechallenge.model.PlayRequest
import com.justeattakeaway.codechallenge.repository.GameRepository
import com.justeattakeaway.codechallenge.service.PlayGameService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest
import spock.lang.Specification

@SpringRabbitTest
class PlayGameServiceSpec extends Specification {


    String playerName = "playerName"
    RabbitTemplate rabbitTemplate = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    GameRepository gameRepository = Mock()
    PlayGameService playGameService = new PlayGameService(rabbitTemplate, objectMapper, gameRepository)

    def "test playInManualMode with valid request"() {
        given:
        PlayRequest playRequest = new PlayRequest(1, "game-id")
        Game game = Game.builder().id("game-id").lastOnePlayed("other-player").initialNumber(5).build()
        game.addNewEvent(new GameEvent("other-player", 1))
        gameRepository.findById("game-id") >> Optional.of(game)

        when:
        playGameService.playInManualMode(playRequest)

        then:
        1 * gameRepository.save(_)
        1 * rabbitTemplate.convertAndSend(_, _)
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
        1 * rabbitTemplate.convertAndSend(_, _)
        notThrown(IllegalStateException)
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
}
