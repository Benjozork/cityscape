package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.storage.model.DeserializationContext

import okio.BufferedSource

/**
 *
 *
 * @receiver DeserializationContext
 *
 * @param buffer BufferedSource
 */
fun DeserializationContext.deSerializeAssetReference(buffer: BufferedSource): ReferenceableAsset {
    // Read the reference
    val assetReference = buffer.readInt()

    // Try to find the asset descriptor for the reference
    val descriptor = assetMap[assetReference] ?: error("unknown asset reference ${assetReference.getBytes().toList()}: nothing was found in the asset map")

    return Cityscape.assetManager[descriptor] as ReferenceableAsset
}