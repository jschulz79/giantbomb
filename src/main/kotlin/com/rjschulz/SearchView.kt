package com.rjschulz


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