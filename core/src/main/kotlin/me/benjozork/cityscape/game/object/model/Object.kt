package me.benjozork.cityscape.game.`object`.model

import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.model.Deletable

import me.benjozork.cityscape.storage.model.Referenceable

abstract class Object : Referenceable(), Deletable {

    abstract var y: Float
    abstract var x: Float

        open fun init() {}
    abstract fun update()
    abstract fun draw()

    override fun delete(): Boolean {
        GameWorld.unregisterObject(this)
        return false
    }

}