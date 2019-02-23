package me.benjozork.cityscape.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera

import ktx.math.*

// Keymap constants

const val CAMERA_MOVE_UP_KEY    = Input.Keys.UP
const val CAMERA_MOVE_LEFT_KEY  = Input.Keys.LEFT
const val CAMERA_MOVE_DOWN_KEY  = Input.Keys.DOWN
const val CAMERA_MOVE_RIGHT_KEY = Input.Keys.RIGHT

// Zoom constants

const val CAMERA_ZOOM_TRANSITION_SPEED = 8f

      val CAMERA_ZOOM_FUNCTION = { pos: Float ->
          Math.pow(Math.max(0.toDouble(), pos.toDouble()), 2.0).toFloat()
      }

const val CAMERA_PAN_KEY = Input.Buttons.MIDDLE

      val CAMERA_DRAG_MOMENTUM_SCALAR_FUNCTION = { dist: Float ->
          if (dist < 5) 1f else Math.pow(dist.toDouble(), 1.1).toFloat()
      }

// Drag momentum constants

const val CAMERA_DRAG_MOMENTUM_LIMIT   = 600f
const val CAMERA_DRAG_MOMENTUM_FALLOFF = 700f

class CameraController(val camera: OrthographicCamera = RenderingContext.camera!!) {

    internal val dragMomentum = vec2()
    internal var virtualZoom = 0f

    internal val targetCamPosition = vec2()
    internal var targetZoomLevel   = 0f

    internal val inputProcessor = CameraControllerInputProcessor(this, camera)

    public fun update() {

        if (camera.zoom < targetZoomLevel) camera.zoom += (CAMERA_ZOOM_TRANSITION_SPEED * (Math.abs(camera.zoom - targetZoomLevel))) * Gdx.graphics.deltaTime
        if (camera.zoom > targetZoomLevel) camera.zoom -= (CAMERA_ZOOM_TRANSITION_SPEED * (Math.abs(camera.zoom - targetZoomLevel))) * Gdx.graphics.deltaTime

        camera.zoom = Math.max(0f, camera.zoom)

        dragMomentum.setLength(dragMomentum.len() - CAMERA_DRAG_MOMENTUM_FALLOFF * Gdx.graphics.deltaTime)

        if (dragMomentum.len() < 1f && dragMomentum.len() > -1f) dragMomentum.setLength(0f)

        if (Gdx.input.isButtonPressed(CAMERA_PAN_KEY)) camera.translate(dragMomentum.cpy().scl(Gdx.graphics.deltaTime))
        camera.update()
    }

}

class CameraControllerInputProcessor(private val controller: CameraController, private val camera: OrthographicCamera) : InputProcessor {

    private var xi = 0f
    private var yi = 0f

    private var xf = 0f
    private var yf = 0f

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        xi = screenX.toFloat()
        yi = screenY.toFloat()

        controller.dragMomentum.set(0f, 0f)

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        xf = screenX.toFloat()
        yf = screenY.toFloat()

        val x = xf - xi
        val y = yf - yi

        if (x != 0f && y != 0f) controller.dragMomentum.set(-x, y).scl(CAMERA_DRAG_MOMENTUM_SCALAR_FUNCTION(controller.dragMomentum.len())).setLength(Math.min(controller.dragMomentum.len(), CAMERA_DRAG_MOMENTUM_LIMIT))

        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        controller.virtualZoom += amount / 6f
        controller.targetZoomLevel = CAMERA_ZOOM_FUNCTION(controller.virtualZoom)
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {

        val x = -Gdx.input.deltaX * camera.zoom
        val y =  Gdx.input.deltaY * camera.zoom

        if (Gdx.input.isButtonPressed(CAMERA_PAN_KEY)) camera.translate(x, y)

        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

}