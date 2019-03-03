package me.benjozork.cityscape.game.input

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2

import ktx.collections.GdxArray
import me.benjozork.cityscape.utils.Line

/**
 * Provides utilities for snapping to lines, angles, etc.
 *
 * @author Benjozork
 */
object AutoSnapper {

    /**
     * Returns the nearest multiple of [base] to [providedAngle]
     *
     * This only deals with [Int]s because of the massive (and intentional) loss of precision that occurs
     * whenever we snap angles.
     *
     * Example:
     * ```
     * val angle = 56
     * val base  = 45
     * println(AutoSnapper.findNearestSnapAngle(angle, base))
     * ```
     *
     * ```
     * >>> 45
     * ```
     *
     * @param providedAngle the angle we wish to "snap"
     * @param base          the base for the angle multiples we wish to snap to
     *
     * @return the nearest base of [base]
     */
    fun findNearestSnapAngle(providedAngle: Int, base: Int): Int {

        return AngleSnapData.retrieve(base).minBy { Math.abs(it - providedAngle) }!!

    } private object AngleSnapData {
        private val mulAngles = mutableMapOf<Int, Array<Int>>()

        internal fun retrieve(base: Int): Array<Int> {

            fun generate(): Array<Int> {
                val res = mutableSetOf<Int>()
                for (i in 0..360 step base) res += i
                return res.toTypedArray()
            }

            return mulAngles.getOrPut(base) { generate() }
        }
    }

    /**
     * Finds the nearest position on any line from [lines] from [providedPosition]
     *
     * @param providedPosition the base position to use, which will be changed to contain the result of this operation (also a return parameter)
     * @param lines            the lines we can snap to
     * @param tolerance        the maximum distance to a line for it to be used. -1 = no max
     *
     * @return whether or not the point was snapped or not, and the corresponding line if such
     */
    fun snapPointOnLines (
            providedPosition: Vector2,
                       lines: GdxArray<Line>,
                   tolerance: Float = -1f
    ): Boolean {
        val nearestLine = lines
                // Create a map with the distance from it to the mouse position
                .associate { it to Intersector.distanceSegmentPoint(it.first.x, it.first.y, it.second.x, it.second.y, providedPosition.x, providedPosition.y) }
                // Find the nearest road
                .minBy     { it.value }!!
                // If that is too far, return null. If not, select only the key
                .takeIf    { it.value < if (tolerance == -1f) Float.MAX_VALUE else tolerance }
                ?.key ?: return false

        Intersector.nearestSegmentPoint(nearestLine.first, nearestLine.second, providedPosition, providedPosition)
        return true
    }

}