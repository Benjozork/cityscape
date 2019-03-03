package me.benjozork.cityscape.game.`object`

import com.badlogic.gdx.math.Rectangle

import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.game.model.Rotatable

import me.benjozork.cityscape.render.RenderingContext
import me.benjozork.cityscape.utils.toPolygon

class TestEntity (
        override var x: Float,
        override var y: Float,
        override var rotation: Float = 0f
) : Object(), Rotatable {

    override val boundingBox = Rectangle(x, y, 100f, 100f).toPolygon()

    override fun update() {

    }

    override fun draw() {
        RenderingContext.switchToShape()
        val shapeRenderer = RenderingContext.shapeRenderer!!
        shapeRenderer.rect(x, y, 5f, 5f, 10f, 10f, 1f, 1f, rotation)
    }

}