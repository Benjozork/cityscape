package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.Cityscape
import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.SerializationContext

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

/**
 *
 *
 * @receiver SProp<OC>
 *
 * @param ctx SerializationContext
 * @param target OC
 */
fun <E> E?.serializeAsAssetReference(ctx: SerializationContext): ByteArray {
    val targetPropValue = this

    (targetPropValue as ReferenceableAsset)

    val locatorSearchResult = ctx.assetMap.entries.find { it.value == targetPropValue.locator }

    val value = locatorSearchResult?.key
    if (value != null) {
        println(value.serializeAsPrimitive().toList() + " existingRef")
        return value.serializeAsPrimitive()
    }

    ctx.assetMap[targetPropValue.reference] = targetPropValue.locator

    println(targetPropValue.reference.serializeAsPrimitive().toList() + " newRef")
    return targetPropValue.reference.serializeAsPrimitive()
}