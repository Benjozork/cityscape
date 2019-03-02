package me.benjozork.cityscape

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver

import com.kotcrab.vis.ui.VisUI

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.enableKtxCoroutines

import me.benjozork.cityscape.assets.ReferenceableTexture
import me.benjozork.cityscape.assets.ReferenceableTextureLoader

import me.benjozork.cityscape.assets.RoadTypeLoader
import me.benjozork.cityscape.game.`object`.road.RoadType
import me.benjozork.cityscape.game.screen.LoadingScreen
import me.benjozork.cityscape.render.RenderingContext
import me.benjozork.cityscape.storage.MapPackage

import java.io.File

object Cityscape : KtxGame<KtxScreen>() {

    val assetManager = AssetManager()

    override fun create() {

        Gdx.app.logLevel = Application.LOG_DEBUG

        assetManager.setLoader(ReferenceableTexture::class.java, ReferenceableTextureLoader(InternalFileHandleResolver()))
        assetManager.setLoader(RoadType::class.java, RoadTypeLoader(InternalFileHandleResolver()))

        RenderingContext.intialize()
        VisUI.load()

        val mapPackage = MapPackage(File("C:\\users\\benjo\\Documents\\reddit\\world"))

        addScreen(LoadingScreen(mapPackage))
        setScreen<LoadingScreen>()

        enableKtxCoroutines(2)
    }

}