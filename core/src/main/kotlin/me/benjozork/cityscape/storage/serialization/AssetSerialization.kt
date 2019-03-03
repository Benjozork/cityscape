package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.SerializationContext

import okio.BufferedSource

/**
 * Deserializes asset references
 *
 * @receiver the [SerializationContext] to use
 *
 * @param buffer the [BufferedSource] to read from
 */
fun DeserializationContext.deSerializeAssetReference(buffer: BufferedSource): ReferenceableAsset {
    // Read the reference
    val assetReference = buffer.readInt()

    // Try to find the asset descriptor for the reference
    val descriptor = assetMap[assetReference] ?: error("unknown asset reference ${assetReference.getBytes().toList()}: nothing was found in the asset map")

    return Cityscape.assetManager[descriptor] as ReferenceableAsset
}

/**
 * Serializes asset references
 *
 * @receiver [Any]
 *
 * @param ctx    the [SerializationContext] to use
 * @param E      the asset reference to write
 */
fun <E> E?.serializeAsAssetReference(ctx: SerializationContext): ByteArray {
    val targetPropValue = this

    (targetPropValue as ReferenceableAsset)

    // Look for an existing matching, reference in the assetmap so that we don't create a new one
    val locatorSearchResult = ctx.assetMap.entries.find { it.value() == targetPropValue.locator() }

    // If there IS an existing matching reference we write the existing one and not the instance currently has
    val value = locatorSearchResult?.key
    if (value != null) {
        return value.serializeAsPrimitive()
    }

    // If there IS NOT an existing matching reference we write the one this instance currently has
    ctx.assetMap[targetPropValue.reference] = targetPropValue.locator

    return targetPropValue.reference.serializeAsPrimitive()
}