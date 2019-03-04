package me.benjozork.cityscape.utils

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.minus

/**
 * Represents a line using two vectors
 */
typealias Line = Pair<Vector2, Vector2>

/**
 * @return the length of the line
 */
fun Line.length(): Float {
    return Math.abs((first - second).len())
}

/**
 * @return the angle of the line in degrees
 */
fun Line.angle(): Double {
    return Math.atan2((this.second.y - this.first.y).toDouble(), (this.second.x - this.first.x).toDouble()) * MathUtils.radDeg
}

/**
 * @param other                   the other line to test against
 * @param returnIntersectionPoint only used for returning the intersection point
 *
 * @return whether the two lines intersect through the method return, and the position of the intersection if they do through
 * the [returnIntersectionPoint] parameter.
 */
fun Line.intersectsWith(other: Line, returnIntersectionPoint: Vector2? = null): Boolean {
    return Intersector.intersectLines(this.first, this.second, other.first, other.second, returnIntersectionPoint)
}