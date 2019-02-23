package me.benjozork.cityscape.utils

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle

fun Rectangle.toPolygon(): Polygon {
    return Polygon(floatArrayOf(x, y, x + width, y, x + width, y + height, x, y + height))
}