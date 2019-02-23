package me.benjozork.cityscape.utils

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2

import ktx.math.vec2
import ktx.math.vec3

private val tempUnprojectionVector = vec3(0f, 0f, 0f)
private val tempUnprojectionReturnVector = vec2(0f, 0f)

fun Input.unprojectedPos(cam: OrthographicCamera): Vector2 {
    tempUnprojectionVector.set(this.x.toFloat(), this.y.toFloat(), 0f)
    tempUnprojectionVector.set(cam.unproject(tempUnprojectionVector))
    return tempUnprojectionReturnVector.set(tempUnprojectionVector.x, tempUnprojectionVector.y)
}