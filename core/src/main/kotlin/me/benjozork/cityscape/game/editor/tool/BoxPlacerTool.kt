package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

import ktx.math.component1
import ktx.math.component2

import me.benjozork.cityscape.game.`object`.TestEntity
import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor

import me.benjozork.cityscape.render.RenderingContext

import me.benjozork.cityscape.utils.unprojectedPos

class BoxPlacerTool : EditorTool() {

    fun place(x: Float, y: Float) {
        GameWorld.registerObject(TestEntity(x, y))
    }

    class BoxPlacerToolInput(private val parentTool: BoxPlacerTool) : ToolInputProcessor(parentTool) {

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return if (button == Input.Buttons.LEFT) {
                val (x, y) = Gdx.input.unprojectedPos(RenderingContext.camera!!)
                parentTool.place(x, y)
                true
            } else false
        }

    }

}