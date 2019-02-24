package me.benjozork.cityscape

import com.kotcrab.vis.ui.VisUI

import com.strongjoshua.console.CommandExecutor

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.enableKtxCoroutines

import me.benjozork.cityscape.game.GameScreen


class Cityscape : KtxGame<KtxScreen>() {

    override fun create() {

        VisUI.load()

        enableKtxCoroutines(2)

        addScreen(GameScreen)
        setScreen<GameScreen>()
    }

}

class CityscapeCommandExecutor : CommandExecutor()