package me.benjozork.cityscape.game.`object`.model

import me.benjozork.cityscape.game.GameWorld
import me.benjozork.cityscape.game.model.Bounded
import me.benjozork.cityscape.game.model.Deleteable
import me.benjozork.cityscape.storage.model.Referenceable

abstract class Object : Referenceable(), Bounded, Deleteable {

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