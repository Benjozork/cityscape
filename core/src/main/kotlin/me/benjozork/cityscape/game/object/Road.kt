package me.benjozork.cityscape.game.`object`

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon

import me.benjozork.cityscape.game.`object`.model.Object

import me.benjozork.cityscape.render.RenderingContext

import kotlin.math.pow

const val TEST_ROAD_WIDTH = 50f

val ROAD_TEXTURE = Texture(Gdx.files.internal("road.png"))

class Road (
        override var  x: Float,
        override var  y: Float,
                 var x2: Float,
                 var y2: Float
) : Object() {

    private val length = Math.sqrt(((this.x2 - this.x).pow(2) + (this.y2 - this.y).pow(2)).toDouble()).toFloat()

    private val angle  = MathUtils.atan2(this.y2 - this.y, this.x2 - this.x) * MathUtils.radDeg

    private var sprite = Sprite(ROAD_TEXTURE).apply {
        setPosition(this@Road.x, this@Road.y)
        setOrigin(0f, 0f)
        setRotation(angle)
        setScale(length / width, TEST_ROAD_WIDTH / height)
    }

    override val boundingBox = Polygon ( floatArrayOf (
            0f,     0f,
            length, 0f,
            length, TEST_ROAD_WIDTH,
            0f,     TEST_ROAD_WIDTH
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
    }

    override fun delete(): Boolean {
        super.delete()
        return true
    }

    companion object {
        val testRoad = Road(0f, 0f, 0f, 0f)
    }

}