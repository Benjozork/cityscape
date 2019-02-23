@file:JvmName("DesktopLauncher")

package me.benjozork.cityscape.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

import me.benjozork.cityscape.Cityscape

/** Launches the desktop (LWJGL) application. */
fun main(args: Array<String>) {
    LwjglApplication(Cityscape(), LwjglApplicationConfiguration().apply {

        title = "cityscape"
        width = 640
        height = 480
        resizable = true

        foregroundFPS = 145
    })
}
