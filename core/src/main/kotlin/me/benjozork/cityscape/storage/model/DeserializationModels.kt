package me.benjozork.cityscape.storage.model

import com.badlogic.gdx.assets.AssetDescriptor
import me.benjozork.cityscape.assets.ReferenceableAsset

import kotlin.reflect.KClass

class DeserializationContext {

    var initialized = false

    var classMap     = mutableMapOf<Int, KClass<*>>()
        internal set

    var assetMap = mutableMapOf<Int, AssetDescriptor<ReferenceableAsset>>()
        internal set
}