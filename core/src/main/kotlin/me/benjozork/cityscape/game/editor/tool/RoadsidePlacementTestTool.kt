package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

import ktx.collections.toGdxArray
import ktx.math.vec2

import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.`object`.TestEntity
import me.benjozork.cityscape.game.`object`.road.Road
import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor
import me.benjozork.cityscape.game.input.AutoSnapper

import me.benjozork.cityscape.render.RenderingContext

import me.benjozork.cityscape.utils.unprojectedPos

class RoadsidePlacementTestTool : EditorTool() {

    private val circlePos: Vector2 = vec2()

    private var currentlyDrawing = true

    override fun draw() {
        if (currentlyDrawing) {
            RenderingContext.switchToShape()
            RenderingContext.shapeRenderer?.circle(circlePos.x, circlePos.y, 100f, 48)
        }
    }

    class RoadsidePlacementTestToolInputProcessor(private val parentTool: RoadsidePlacementTestTool) : ToolInputProcessor(parentTool) {

        override fun mouseMoved(screenX: Int, screenY: Int): Boolean {

            val unproj = Gdx.input.unprojectedPos(RenderingContext.camera!!)

            // Use the result of snapPointOnLines to decide whether or not we draw the circle or not
            if (AutoSnapper.snapPointOnLines (
                        unproj,
                        GameWorld.objects.filter { it is Road }.flatMap { (it as Road).sideAttachmentLines }.toGdxArray(),
                        100f
            )) {
                this.parentTool.circlePos.set(unproj); this.parentTool.currentlyDrawing = true
            } else this.parentTool.currentlyDrawing = false

            return true
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (parentTool.currentlyDrawing) {
                val unproj = Gdx.input.unprojectedPos(RenderingContext.camera!!)
                GameWorld.registerObject(TestEntity(unproj.x, unproj.y))
                return true
            } else return false
        }

    }

}