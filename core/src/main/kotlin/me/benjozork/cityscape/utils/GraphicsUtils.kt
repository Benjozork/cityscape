package me.benjozork.cityscape.utils

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

inline fun <B : ShapeRenderer> B.use(action: (B) -> Unit) {
    begin()
    action(this)
    end()
}