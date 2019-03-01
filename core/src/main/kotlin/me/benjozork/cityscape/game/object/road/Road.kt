package me.benjozork.cityscape.game.`object`.road

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon

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

    private val length = Math.sqrt(((this.x2 - this.x).pow(2) + (this.y2 - this.y).pow(2)).toDouble()).toFloat()

    private val angle  = MathUtils.atan2(this.y2 - this.y, this.x2 - this.x) * MathUtils.radDeg

    private var sprite = type.roadSprite.apply {
        setPosition(this@Road.x, this@Road.y)
        setOrigin(0f, 0f)
        setRotation(angle)
        setScale(length / width, type.roadSprite.width / height)
    }

    override val boundingBox = Polygon ( floatArrayOf (
            0f,     0f,
            length, 0f,
            length, type.roadSprite.width,
            0f,     type.roadSprite.width
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

}