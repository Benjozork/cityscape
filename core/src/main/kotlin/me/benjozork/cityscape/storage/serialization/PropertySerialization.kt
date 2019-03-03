package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.NotSerialized
import me.benjozork.cityscape.storage.model.SProp
import me.benjozork.cityscape.storage.model.Serializable
import me.benjozork.cityscape.storage.model.SerializationContext
import me.benjozork.cityscape.storage.model.SerializeReference

import okio.BufferedSource

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

/**
 * Provides the list of props that should be serialized for a given [Serializable] class.
 * Only mutable properties that are NOT private and are not annotated with [NotSerialized] will be returned.
 *
 * @receiver a [KClass] that extends [Serializable]
 *
 * @return a [Map] of the property hash (as an [Int]) to the property itself [SProp]
 */
@Suppress("UNCHECKED_CAST")
fun <OC : Serializable> KClass<out OC>.serializedProps(): Map<Int, SProp<OC>> { //@TODO takes a lot of time
    return (memberProperties
            .filter {
                it is KMutableProperty1 && it.visibility!! < KVisibility.PRIVATE
            } as Collection<SProp<OC>>)
            .filter { prop ->
                prop.annotations.none { it is NotSerialized }
            }
            .associate { it.name.getAdler32Int() to it }
}

/**
 * Serializes an object property
 *
 * @receiver [SProp]
 *
 * @param ctx    the [DeserializationContext] to use
 * @param target the object property to write
 *
 * @return the result of the serialization as a [ByteArray]
 */
fun <OC : Serializable> SProp<OC>.serialize(ctx: SerializationContext, target: OC): ByteArray {
    val targetPropValue = this.get(target)
    return when {
        this.isSerializedAsPrimitive()    ->  targetPropValue.serializeAsPrimitive()
        this.isAssetReferenceSerialized() ->  targetPropValue.serializeAsAssetReference(ctx)
        else -> (targetPropValue as Serializable).serialize(ctx)
    }
}

/**
 * Deserializes an object property
 *
 * @receiver [SProp]
 *
 * @param ctx    the [DeserializationContext] to use
 * @param buffer the [BufferedSource] to read from
 *
 * @return the result of the deserialization (as [Any])
 */
fun <OC : Serializable> SProp<OC>.deSerialize(ctx: DeserializationContext, buffer: BufferedSource): Any {
    val propTypeClass = this.returnType.classifier as KClass<*>
    return when {
        this.isSerializedAsPrimitive()    -> ctx.deSerializeNextPrimitive(propTypeClass, buffer)
        this.isAssetReferenceSerialized() -> ctx.deSerializeAssetReference(buffer)
        this.isSerialized()               -> ctx.deSerializeObject(buffer, 0)
        else -> error("can't dserialize property: it is neither of primitive type, referenceable or serializable")
    }
}

/**
 * Checks whether or not an object property should be serialized as a primitive
 *
 * @receiver Any?
 *
 * @return Boolean
 */
fun <OC : Serializable> SProp<OC>.isSerializedAsPrimitive(): Boolean {
    val kClass = this.returnType.classifier as KClass<*>
    return (kClass.isSubclassOf(Number::class) || kClass.isSubclassOf(String::class))
}

/**
 * Checks whether or not an object property should be serialized as an asset reference
 *
 * @receiver SProp<OC>
 *
 * @return the result of the check
 */
fun <OC : Serializable> SProp<OC>.isAssetReferenceSerialized(): Boolean {
    return (this.returnType.classifier as KClass<*>).isSubclassOf(ReferenceableAsset::class)
}

/**
 * Checks whether or not an object property should be serialized fully or by reference,
 * through the use of the [NotSerialized] annotation
 *
 * @receiver [SProp]
 *
 * @return the result of the check
 */
fun <OC : Serializable> SProp<OC>.isReferenceSerialized(): Boolean {
    return this.annotations.any { it is SerializeReference }
}

/**
 * Checks whether or not an object property should be serialized or not, through the use
 * of the [NotSerialized] annotation
 *
 * @receiver [SProp]
 *
 * @return the result of the check
 */
fun <OC : Serializable> SProp<OC>.isSerialized(): Boolean {
    return this.annotations.none { it is NotSerialized }
}