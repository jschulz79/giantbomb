package com.rjschulz

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * Classes related to the Game domain
 */

@Component
class GameService(
    val gameRepository: GameRepository,
    val httpClient: OkHttpClient,
) {

    fun searchGames(searchTerm: String): List<Game> {
        val call = httpClient.newCall(
            Request.Builder()
                .url(
                    "http://www.giantbomb.com/api/search/".toHttpUrl().newBuilder()
                        .addQueryParameter("query", searchTerm)
                        .addQueryParameter("format", "json")
                        .addQueryParameter("field_list", "id,name,image")
                        .addQueryParameter("resources", "game")
                        .build()
                ).build()
        )

        val response = call.execute()

        if (response.isSuccessful) {
            try {
                val resultsData = jacksonObjectMapper().readValue(response.body?.string(), Results::class.java)
                return resultsData.results
            } catch (ex: IOException) {
                println("there was error parsing the JSON response")
            }
        }
        return listOf()
    }

    fun rentGame(game: Game) {
        gameRepository.rentGame(game)
    }

    fun isGameRented(id: Int): Boolean {
        return gameRepository.isRented(id)
    }

}

/**
 * This class represents the data store for the application.
 * In a real world scenario, this would be a persistent data store.
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



/**
 * Data classes for the deserialization of responses from API
 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class Results(
    val error: String,
    val limit: Int,
    val offset: Int,
    val number_of_page_results: Int,
    val number_of_total_results: Int,
    val status_code: Int,
    val results: List<Game>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Game(
    val id: Int,
    val image: ImageUrls,
    val name: String
) {
    fun getThumbUrl(): String {
        return image.thumb_url
    }

    fun getScreenUrl(): String {
        return image.screen_url
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageUrls(
    val icon_url: String,
    val medium_url: String,
    val screen_url: String,
    val screen_large_url: String,
    val small_url: String,
    val super_url: String,
    val thumb_url: String,
)