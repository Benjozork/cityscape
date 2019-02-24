package me.benjozork.cityscape.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport

import com.kotcrab.vis.ui.widget.VisTable

import me.benjozork.cityscape.game.input.GameInputController
import me.benjozork.cityscape.render.RenderingContext

abstract class UIView {

    val stage = Stage(ScreenViewport(RenderingContext.uiCamera!!))
            .apply {
                addActor(layout())
                GameInputController.addProcessor(this)
            }

    abstract fun layout(): VisTable

    fun update() {
        stage.act()
    }

    fun draw() {
        RenderingContext.switchToSprite()
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

}