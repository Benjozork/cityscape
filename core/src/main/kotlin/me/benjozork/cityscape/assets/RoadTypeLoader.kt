package me.benjozork.cityscape.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array

import kotlinx.serialization.json.Json
import ktx.assets.assetDescriptor

import ktx.collections.gdxArrayOf

import me.benjozork.cityscape.game.`object`.road.RoadType

class RoadTypeLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<RoadType, RoadTypeLoader.RoadTypeParameters>(resolver) {

    private var manifest: RoadType.Manifest? = null

    override fun loadSync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: RoadTypeParameters?): RoadType {
        return RoadType(this.manifest!!, AssetLocator(fileName!!, RoadType::class))
    }

    override fun loadAsync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: RoadTypeParameters?) {
    }

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: RoadTypeParameters?): Array<AssetDescriptor<out Any>> {
        this.manifest = Json.nonstrict.parse(RoadType.Manifest.serializer(), file!!.file().readText())
        return gdxArrayOf(assetDescriptor<ReferenceableTexture>(manifest!!.roadTexturePath))
    }

    class RoadTypeParameters : AssetLoaderParameters<RoadType>()

}