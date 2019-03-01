package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

import ktx.assets.assetDescriptor
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.`object`.road.Road
import me.benjozork.cityscape.game.`object`.road.RoadType
import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor
import me.benjozork.cityscape.game.input.AutoSnapper

import me.benjozork.cityscape.render.RenderingContext

import me.benjozork.cityscape.utils.unprojectedPos

import kotlin.math.pow


const val KEY_ANGLE_SNAP      = Input.Keys.CONTROL_LEFT

const val BUTTON_DRAW         = Input.Buttons.LEFT
const val BUTTON_CANCEL_DRAW  = Input.Buttons.RIGHT

const val ANGLE_SNAP_STEP = 45

/**
 * [EditorTool] that places roads.
 */
class RoadTool : EditorTool() {

    private var currentlyDrawing = false

    private var angleSnappingEnabled = false

    private lateinit var currentType: RoadType

    private var currentX1 = 0f
    private var currentY1 = 0f
    private var currentX2 = 0f
    private var currentY2 = 0f

    @Suppress("UsePropertyAccessSyntax")
    override fun draw() {

        // We don't want to do *anything* if currentType is not initialized
        if (!this::currentType.isInitialized) return

        if (currentlyDrawing) {
            RenderingContext.switchToSprite()
            currentType.roadSprite.apply {

                setPosition(currentX1, currentY1)
                setOrigin(0f, 0f)
                setRotation(MathUtils.atan2(currentY2 - currentY1, currentX2 - currentX1) * MathUtils.radDeg)

                val cathX = currentX2 - currentX1
                val cathY = currentY2 - currentY1
                val hypo = Math.sqrt((cathX.pow(2) + cathY.pow(2)).toDouble()).toFloat()
                setScale(hypo / width, currentType.roadSprite.width / height)
            }
            currentType.roadSprite.draw(RenderingContext.spriteBatch)
        }
    }

    class RoadToolInputProcessor(private val parentTool: RoadTool) : ToolInputProcessor(parentTool) {

        override fun keyDown(keycode: Int): Boolean {
            return if (keycode == KEY_ANGLE_SNAP) {
                parentTool.angleSnappingEnabled = false
                true
            } else false
        }

        override fun keyUp(keycode: Int): Boolean {
            return if (keycode == KEY_ANGLE_SNAP) {
                parentTool.angleSnappingEnabled = false
                true
            } else false
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (button == BUTTON_DRAW) {
                parentTool.currentlyDrawing = true

                parentTool.currentType = Cityscape.assetManager.get(assetDescriptor("road.json"))

                val unprojected = Gdx.input.unprojectedPos(RenderingContext.camera!!)
                parentTool.currentX1 = unprojected.x
                parentTool.currentY1 = unprojected.y  // This makes sure that there is no temporary line going to the previous
                parentTool.currentX2 = unprojected.x  // x2 and y2 values from the previous draw
                parentTool.currentY2 = unprojected.y
                return true
            } else if (button == BUTTON_CANCEL_DRAW) {
                parentTool.currentlyDrawing = false  // Cancel the drawing
                return true
            }
            return false
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            if (parentTool.currentlyDrawing) {
                val actualVec = Gdx.input.unprojectedPos(RenderingContext.camera!!)
                val actualDist = actualVec.sub(parentTool.currentX1, parentTool.currentY1)

                if (parentTool.angleSnappingEnabled) {

                    val computedAngle = AutoSnapper.findNearestSnapAngle(actualDist.angle().toInt(), base = ANGLE_SNAP_STEP).toFloat()
                    val computedLength = actualDist.len()

                    AngleSnapTracking.computedVector.set(1f, 1f).nor()
                    AngleSnapTracking.computedVector.setLength(computedLength) // This creates an appropriate
                    AngleSnapTracking.computedVector.setAngle(computedAngle)   // "actual" angle vector that fits the snapped angle

                    val (nx2, ny2) = AngleSnapTracking.computedVector
                    parentTool.currentX2 = parentTool.currentX1 + nx2
                    parentTool.currentY2 = parentTool.currentY1 + ny2

                } else {
                    parentTool.currentX2 = parentTool.currentX1 + actualDist.x // In this case, place the road without
                    parentTool.currentY2 = parentTool.currentY1 + actualDist.y // angle snapping at all
                }

                return true
            }
            else return false

        } private object AngleSnapTracking {
            val computedVector: Vector2 = vec2(1f, 1f).nor()
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return if (parentTool.currentlyDrawing) {
                parentTool.currentlyDrawing = false // here, we actually register the new road since we are done drawing
                GameWorld.registerObject(Road(parentTool.currentType.copy(), parentTool.currentX1, parentTool.currentY1, parentTool.currentX2, parentTool.currentY2))
                true
            } else false
        }

    }

}