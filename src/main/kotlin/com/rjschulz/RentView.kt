package com.rjschulz

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import java.util.*

/**
 * Classes to support the Rent View
 */

@Route(value = "/rent/:id?", layout = MainLayout::class)
@UIScope
class RentView(
    val gameRepository: GameRepository,
): Div(), BeforeEnterObserver {

    override fun beforeEnter(event: BeforeEnterEvent) {
        val optionalId: Optional<String> = event.routeParameters["id"]
            optionalId.ifPresentOrElse(
                { id -> displayGameDetails(id.toInt())}
        ) {
            add(Span("Sorry, could not find that game"))
        }
    }

    fun displayGameDetails(id: Int) {

        val div = Div()

        val game = gameRepository.getGame(id)

        game?.let {
            div.add(Div(Span("Have fun with your rented game!!")))
            div.add(Div(Span(game.name)))

            val gameImage = Image()
            gameImage.src = game.getScreenUrl()
            div.add(Div(gameImage))

            val goToSearchButton = Button("Back To Search")
            goToSearchButton.addClickListener {
                UI.getCurrent()
                    .page
                    .open("/search", "_self")
            }

            div.add(goToSearchButton)

        } ?: run {
            div.add(Span("Sorry, could not find game with id $id"))
        }

        add(div)
    }


}