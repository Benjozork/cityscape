package me.benjozork.cityscape.game.input

/**
 * Provides utilities for snapping to lines, angles, etc.
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

}