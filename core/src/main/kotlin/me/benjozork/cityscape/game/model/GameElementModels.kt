package me.benjozork.cityscape.game.model

import com.badlogic.gdx.math.Polygon

interface Bounded {

    /**
     * The bounding box of this element
     */
    val boundingBox: Polygon

}

interface Selectable : Bounded

interface Deletable : Selectable {

    /**
     * @return whether this object needs confirmation in order to be deleted
     */
    fun delete(): Boolean

}

interface Rotatable {

    /**
     * The rotation in degrees
     */
    var rotation: Float

}