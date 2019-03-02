package me.benjozork.cityscape.game.`object`.road

import com.badlogic.gdx.graphics.g2d.Sprite

import kotlinx.serialization.Serializable

import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.assets.AssetLocator
import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.assets.ReferenceableTexture

class RoadType(manifest: Manifest, locator: AssetLocator) : ReferenceableAsset(locator) {

    val roadTexture = Cityscape.assetManager.get<ReferenceableTexture>(manifest.roadTexturePath)

    val roadSprite = Sprite(roadTexture.texture)

    @Serializable
    class Manifest {

        lateinit var roadTexturePath: String

    }

}