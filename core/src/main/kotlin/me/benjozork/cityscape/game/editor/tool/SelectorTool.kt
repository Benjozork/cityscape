package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Polygon
import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.game.GameScreen

import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor

import me.benjozork.cityscape.render.RenderingContext

import me.benjozork.cityscape.utils.unprojectedPos

const val KEY_DELETE_OBJ = Input.Keys.FORWARD_DEL

class SelectorTool : EditorTool() {

    val currentlySelected = mutableSetOf<Object>()

    private var rot = 0f

    override fun draw() {
        currentlySelected.forEach {
            RenderingContext.switchToShape()
            Polygon(it.boundingBox.transformedVertices).apply {

                val minX = vertices.toList().filterIndexed { i, _ -> i % 2 == 0 }.min()!!
                val maxX = vertices.toList().filterIndexed { i, _ -> i % 2 == 0 }.max()!!

                val minY = vertices.toList().filterIndexed { i, _ -> i % 2 != 0 }.min()!!
                val maxY = vertices.toList().filterIndexed { i, _ -> i % 2 != 0 }.max()!!

                val cX = minX + (maxX - minX) / 2
                val cY = minY + (maxY - minY) / 2

                setOrigin(cX, cY)
                scale(0.2f)

                RenderingContext.shapeRenderer?.color = Color.RED
                RenderingContext.shapeRenderer?.polygon(transformedVertices)

            }.transformedVertices
            RenderingContext.shapeRenderer?.color = Color.WHITE
        }
    }

    class SelectorToolInputProcessor(private val parentTool: SelectorTool) : ToolInputProcessor(parentTool) {

        override fun keyDown(keycode: Int): Boolean {
            // This handles element deletion-
            return if (keycode == KEY_DELETE_OBJ) {
                (Cityscape.shownScreen as GameScreen).ui.deleteWithConfirmDialog(parentTool.currentlySelected)
                true
            } else false
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            // Find item to select
            val selectedBox = GameWorld.objects.firstOrNull { it.boundingBox.contains(Gdx.input.unprojectedPos(RenderingContext.camera!!)) } ?: return false

            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                parentTool.currentlySelected += selectedBox
            else
                parentTool.currentlySelected.also { it.clear() } += selectedBox

            return true
        }

    }

}