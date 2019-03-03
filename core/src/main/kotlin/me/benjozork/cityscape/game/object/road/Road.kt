package me.benjozork.cityscape.game.`object`.road

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon

import ktx.collections.gdxArrayOf

import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.render.RenderingContext

import kotlin.math.pow

class Road (
                   type: RoadType,
        override var  x: Float,
        override var  y: Float,
                 var x2: Float,
                 var y2: Float
) : Object() {

    var type: RoadType = type

    val length = Math.sqrt(((this.x2 - this.x).pow(2) + (this.y2 - this.y).pow(2)).toDouble()).toFloat()
    val angle  = MathUtils.atan2(this.y2 - this.y, this.x2 - this.x) * MathUtils.radDeg

    /**
     * Represents the lines objects attached to this road are placed on
     */
    val sideAttachmentLines = gdxArrayOf("")

    private var sprite = Sprite(type.roadTexture()).apply {
        setPosition(this@Road.x, this@Road.y)
        setOrigin(0f, 0f)
        setRotation(angle)
        setScale(length / width, type.roadWidth / height)
    }

    override val boundingBox = Polygon ( floatArrayOf (
            0f,     0f,
            length, 0f,
            length, type.roadWidth,
            0f,     type.roadWidth
    )).apply {
        setPosition(this@Road.x, this@Road.y)
        setOrigin(0f, 0f)
        setRotation(angle)
    }

    override fun update() {
    }

    override fun draw() {
        RenderingContext.switchToSprite()
        sprite.draw(RenderingContext.spriteBatch)
        RenderingContext.switchToShape()
        RenderingContext.shapeRenderer?.circle(x, y, 100f)
    }

    override fun delete(): Boolean {
        super.delete()
        return true
    }

}