package me.benjozork.cityscape.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport

import com.kotcrab.vis.ui.widget.VisTable

import me.benjozork.cityscape.game.input.GameInputController
import me.benjozork.cityscape.render.RenderingContext

abstract class UIView {

    val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), RenderingContext.uiCamera!!))
            .apply {
                addActor(layout())
                GameInputController.addProcessor(this)
            }

    abstract fun layout(): VisTable

    fun update() {
        stage.batch.projectionMatrix = RenderingContext.uiCamera!!.combined
        stage.viewport.setScreenSize(RenderingContext.uiCamera!!.viewportWidth.toInt(), RenderingContext.uiCamera!!.viewportHeight.toInt())
        stage.act()
    }

    fun draw() {
        RenderingContext.switchToSprite()
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        stage.batch.projectionMatrix = RenderingContext.uiCamera!!.combined
        stage.viewport.setScreenSize(width, height)
    }

}