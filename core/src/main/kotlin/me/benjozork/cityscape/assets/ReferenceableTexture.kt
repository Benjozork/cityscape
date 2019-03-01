package me.benjozork.cityscape.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array

import ktx.collections.gdxArrayOf

/**
 * This class represents a libGDX [Texture] object inside a [ReferenceableAsset] container, allowing
 * the game to reference it inside [MapPackages][me.benjozork.cityscape.storage.MapPackage]
 *
 * @property texture the contained [Texture].
 * @constructor
 */
class ReferenceableTexture(val texture: Texture) : ReferenceableAsset()

/**
 *
 * @constructor
 */
class ReferenceableTextureLoader(resolver: FileHandleResolver) : SynchronousAssetLoader<ReferenceableTexture, ReferenceableTextureLoader.Unused>(resolver) {

    override fun load(assetManager: AssetManager?, fileName: String?, file: FileHandle?, parameter: Unused?): ReferenceableTexture {
        return ReferenceableTexture(Texture(file))
    }

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: Unused?): Array<AssetDescriptor<Any>> {
        return gdxArrayOf()
    }

    class Unused : AssetLoaderParameters<ReferenceableTexture>()

}