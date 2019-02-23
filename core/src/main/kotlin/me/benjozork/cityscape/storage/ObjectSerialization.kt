package me.benjozork.cityscape.storage

import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.Referenceable
import me.benjozork.cityscape.storage.model.SProp
import me.benjozork.cityscape.storage.model.Serializable

import okio.Buffer

import kotlin.reflect.KClass

/**
 * Serializes the receiver [Serializable] into a [ByteArray]
 *
 * @receiver the object to serialize
 *
 * @return ByteArray
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified OC : Serializable> OC.serialize(): ByteArray {
    val props = this::class.serializedProps()

    val finalBytes = mutableListOf<Byte>()

    finalBytes.add(OBJ_BYTE)
    finalBytes.addAll(this::class.TYPE_HASH.toTypedArray())
    finalBytes.addAll(props.size.getBytes().toTypedArray())

    props.forEach { k, v ->
        finalBytes.addAll(k.getBytes().toTypedArray())
        finalBytes.addAll(v.serialize(this).toTypedArray())
    }

    return finalBytes.toByteArray()
}

/**
 * Serializes the receiver [Serializable] into a [ByteArray].
 * As opposed to [serialize], this does not use a receiver but rather an explicit
 * target and class.
 *
 * @param obj   the explicit receiver
 * @param klass the explicit class of the receiver
 *
 * @return ByteArray
 */
@Suppress("UNCHECKED_CAST")
internal fun serializeExplicit(obj: Serializable, klass: KClass<Serializable>): ByteArray {
    val props = klass.serializedProps().map { (h, p) -> h to p.serialize(obj) }.toMap()

    val finalBytes = mutableListOf<Byte>()

    finalBytes.add(OBJ_BYTE)
    finalBytes.addAll(klass.TYPE_HASH.toTypedArray())
    finalBytes.addAll(props.size.getBytes().toTypedArray())

    props.forEach { (k, v) ->
        finalBytes.addAll(k.getBytes().toTypedArray())
        finalBytes.addAll(v.toTypedArray())
    }

    return finalBytes.toByteArray()
}

/**
 * Reads the next [Serializable] object found in the receiver [Buffer]
 *
 * @receiver Buffer
 *
 * @return Serializable
 */
@Suppress("UNCHECKED_CAST")
fun DeserializationContext.deSerializeNextObject(): Serializable {

    // Prepare type info

    val typeByte = buffer.readByte()
    if (typeByte != OBJ_BYTE) error("expected object byte but found $typeByte instead")

    val objClassHash  = buffer.readInt()
    val objClass      = classMap[objClassHash] as KClass<Serializable>? ?: error("unknown class hash ${objClassHash.getBytes().toList()}: nothing was found in the class map")
    val objClassProps = objClass.serializedProps()

    classMap.putAll(objClassProps.map { (h, p) -> h to p.returnType.classifier as KClass<Serializable> })

    // Start reading object

    val deserializedProps = mutableMapOf<SProp<Serializable>, Any?>()

    val numPropsInPayload = buffer.readInt()

    val propList = objClassProps.values.toList()

    for (i in 0 until numPropsInPayload) {
        val propHash = buffer.readInt()
        val currentProp = objClassProps[propHash] ?: error("unknown property with hash \"$propHash\"")
        deserializedProps[currentProp] = propList[i].deserialize(this)
    }

    // Instantiate object and populate props
    return fabricateInstance(objClass, deserializedProps)
}

/**
 *
 * @receiver ByteArray
 *
 * @return Referenceable
 */
fun DeserializationContext.deSerializeNextReference(): Referenceable {
    val reference = buffer.readInt()
    val refClass = classMap[reference] ?: error("unknown class hash \"$reference\": nothing was found in the reference map")

    // @TODO

    return Referenceable()
}