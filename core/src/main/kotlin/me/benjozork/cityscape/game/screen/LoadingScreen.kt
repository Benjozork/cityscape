package me.benjozork.cityscape.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture

import com.kotcrab.vis.ui.widget.VisProgressBar
import com.kotcrab.vis.ui.widget.VisTable

import ktx.app.KtxScreen
import ktx.vis.table

import me.benjozork.cityscape.Cityscape

import me.benjozork.cityscape.game.GameScreen
import me.benjozork.cityscape.storage.MapPackage

import me.benjozork.cityscape.ui.UIView

class LoadingScreen(val mapPackage: MapPackage) : KtxScreen {

    lateinit var pBar: VisProgressBar

    val view = object : UIView() {

        override fun layout(): VisTable {
            return table {
                x = 0f
                y = 0f
                width  = Gdx.graphics.width.toFloat()
                height = Gdx.graphics.height.toFloat()
                center()

                label("Loading assets...")
                row()
                progressBar {
                    pBar = this
                }
            }
        }

    }

    override fun show() {
        mapPackage.readAssetsToLoad().forEach { Cityscape.assetManager.load(it) }
    }

    override fun render(delta: Float) {

        val manager = Cityscape.assetManager

        if (!manager.isFinished) {
            manager.update()
            pBar.value = manager.progress * 100
        } else {
            Cityscape.addScreen(GameScreen(mapPackage))
            Cityscape.setScreen<GameScreen>()
        }

        view.update()
        view.draw()
    }
}