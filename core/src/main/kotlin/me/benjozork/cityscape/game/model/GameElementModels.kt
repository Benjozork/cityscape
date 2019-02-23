package me.benjozork.cityscape.game.model

import com.badlogic.gdx.math.Polygon

interface Deleteable {

    /**
     * @return whether this object needs confirmation in order to be deleted
     */
    fun delete(): Boolean

}

interface Bounded {

    val boundingBox: Polygon

}

interface Selectable : Bounded