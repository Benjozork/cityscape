package me.benjozork.cityscape.storage.model

import com.badlogic.gdx.assets.AssetDescriptor
import me.benjozork.cityscape.assets.ReferenceableAsset

import kotlin.reflect.KClass

/**
 * Represents the context of an ongoing deserialization
 *
 * @property initialized whether this [DeserializationContext] is ready to be used or not
 * @property classMap    a map of class hashes (as [ints][Int])
 * @property assetMap    a map of asset refrences (as [ints][Int])
 */
class DeserializationContext {

    var initialized = false

    var classMap     = mutableMapOf<Int, KClass<*>>()
        internal set

    var assetMap = mutableMapOf<Int, AssetDescriptor<ReferenceableAsset>>()
        internal set
}