package com.rjschulz

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * Classes to support the Search View
 */

@Route(value = "/search", layout = MainLayout::class)
@UIScope
class SearchView(
    private val gameService: GameService,
) : Div() {

    override fun onAttach(attachEvent: AttachEvent) {

        val searchField = TextField()
        val searchButton = Button("Search")

        val grid: Grid<Game> = Grid(Game::class.java, false)
        grid.setHeight(700f, Unit.PIXELS)

        grid.addColumn(Game::name).setHeader("Title")

        grid.addColumn(
            ComponentRenderer({ Image() },
                { image: Image, game: Game ->
                    image.setSrc(game.getThumbUrl())
                    image.setHeight(160f, Unit.PIXELS)
                    image.setAlt(game.name)
                })
        ).setHeader("Thumbnail")

        grid.addColumn(
            ComponentRenderer({ Button("Rent") },
                { rentButton: Button, game: Game ->
                    if (gameService.isGameRented(game.id)) {
                        rentButton.isEnabled = false
                    }
                    rentButton.addClickListener {
                        gameService.rentGame(game)
                        UI.getCurrent()
                            .page
                            .open("/rent/" + game.id, "_self")
                    }
                })
        ).setHeader("Rent")

        searchButton.addClickListener {
            grid.setItems(
                gameService.searchGames(searchField.value)
            )
        }

        val mainLayout = Div()
        mainLayout.setSizeFull()
        mainLayout.add(searchField, searchButton, grid)
        add(mainLayout)
    }

}

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