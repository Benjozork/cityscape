package me.benjozork.cityscape.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Represents a line using two vectors
 */
typealias Line = Pair<Vector2, Vector2>

fun Line.angle(): Double {
    return Math.atan2((this.second.y - this.first.y).toDouble(), (this.second.x - this.first.x).toDouble()) * MathUtils.radDeg
}