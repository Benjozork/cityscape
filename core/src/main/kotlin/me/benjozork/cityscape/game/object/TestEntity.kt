package me.benjozork.cityscape.game.`object`

import com.badlogic.gdx.math.Rectangle
import me.benjozork.cityscape.Cityscape

import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.game.`object`.road.Road
import me.benjozork.cityscape.game.model.Rotatable

import me.benjozork.cityscape.render.RenderingContext
import me.benjozork.cityscape.utils.toPolygon

class TestEntity (
        override var x: Float,
        override var y: Float,
        override var rotation: Float = 2f
) : Object(), Rotatable {

    var test = Road(Cityscape.assetManager["cityscape:road.json"], 10f, 10f, 10f, 10f)

    override val boundingBox = Rectangle(0f, 0f, 10f, 10f).toPolygon().apply {
            setPosition(this@TestEntity.x, this@TestEntity.y)
            setOrigin(5f, 5f)
            setRotation(this@TestEntity.rotation)
    }

    override fun update() {

    }

    override fun draw() {
        RenderingContext.switchToShape()
        val shapeRenderer = RenderingContext.shapeRenderer!!
        shapeRenderer.rect(x, y, 5f, 5f, 10f, 10f, 1f, 1f, rotation)
    }

}