package me.benjozork.cityscape.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

object RenderingContext {

    var initialized = false

    var shapeRenderer: ShapeRenderer? = null
        get() = if (field != null) field else error("rendering context must be initialized before accessing shaperenderer")
        private set

    var spriteBatch: SpriteBatch? = null
        get() = if (field != null) field else error("rendering context must be initialized before accessing spritebatch")
        private set

    var camera: OrthographicCamera? = null
        get() = if (field != null) field else error("rendering context must be initialized before accessing camera")
        private set

    var uiCamera: OrthographicCamera? = null
        get() = if (field != null) field else error("rendering context must be initialized before accessing camera")
        private set

    fun intialize() {
        this.initialized = true

        this.camera        = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        this.uiCamera      = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        this.shapeRenderer = ShapeRenderer().apply { this.setAutoShapeType(true) }
        this.spriteBatch   = SpriteBatch()
    }

    fun update() {
        this.shapeRenderer?.projectionMatrix = this.camera!!.combined
        this.spriteBatch?.projectionMatrix = this.camera!!.combined
    }

    fun switchToShape() {
        if (spriteBatch!!.isDrawing) spriteBatch!!.end()
        if (! shapeRenderer!!.isDrawing) {
            shapeRenderer!!.begin()
        } else {
            shapeRenderer!!.end()
            shapeRenderer!!.begin()
        }
    }

    fun switchToSprite() {
        if (shapeRenderer!!.isDrawing) shapeRenderer!!.end()
        if (! spriteBatch!!.isDrawing) {
            spriteBatch!!.begin()
        } else {
            spriteBatch!!.end()
            spriteBatch!!.begin()
        }
    }

}