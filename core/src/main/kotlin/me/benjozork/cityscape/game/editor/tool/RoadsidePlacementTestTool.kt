package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2

import ktx.math.vec2

import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.`object`.road.Road
import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor

import me.benjozork.cityscape.render.RenderingContext

import me.benjozork.cityscape.utils.unprojectedPos

class RoadsidePlacementTestTool : EditorTool() {

    private val circlePos: Vector2 = vec2()

    private var drawEnabled = true

    override fun draw() {
        if (drawEnabled) {
            RenderingContext.switchToShape()
            RenderingContext.shapeRenderer?.circle(circlePos.x, circlePos.y, 100f, 48)
        }
    }

    class RoadsidePlacementTestToolInputProcessor(private val parentTool: RoadsidePlacementTestTool) : ToolInputProcessor(parentTool) {

        override fun mouseMoved(screenX: Int, screenY: Int): Boolean {

            val unproj = Gdx.input.unprojectedPos(RenderingContext.camera!!)

            val nearestRoadLine = GameWorld.objects
                    // Only keep roads, return if there are not any
                    .filter    { it is Road }
                    .also      { if (it.isEmpty()) return false }
                    // Cast every element as Road, so that we can use their attachment lines
                    .map       { it as Road }
                    // Get the sidelines for each road and transform them to their vec2 pairs
                    .flatMap   { it.sideAttachmentLines }
                    // Create a map with the distance from it to the mouse position
                    .associate { it to Intersector.distanceSegmentPoint(it.first.x, it.first.y, it.second.x, it.second.y, unproj.x, unproj.y) }
                    // Find the nearest road
                    .minBy     { it.value }!!
                    // If that is too far, return null. If not, select only the key
                    .takeIf    { it.value < 100f }
                    ?.key

            if (nearestRoadLine != null) {
                parentTool.drawEnabled = true
                Intersector.nearestSegmentPoint(nearestRoadLine.first, nearestRoadLine.second, unproj, parentTool.circlePos)
            } else parentTool.drawEnabled = false

            return true
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return false
        }

    }

}