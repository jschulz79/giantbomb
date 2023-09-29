package com.rjschulz

import org.springframework.stereotype.Component

/**
 * This class represents the data store for the application.
 * In a real world scenario, this would be a persistent datastore.
 * For this exercise, just using an in memory Map.
 */

@Component
class GameRepository {

    val rentedGamesMap: MutableMap<Int, Game> = mutableMapOf()

    fun rentGame(game: Game) {
        rentedGamesMap[game.id] = game
    }

    fun returnGame(id: Int) {
        rentedGamesMap.remove(id)
    }

    fun getGame(id: Int): Game? {
        return rentedGamesMap[id]
    }

    fun isRented(id: Int): Boolean {
        rentedGamesMap[id]?.let {
            return true
        }
        return false
    }

}