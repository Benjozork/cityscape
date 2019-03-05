package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.SProp
import me.benjozork.cityscape.storage.model.Serializable
import me.benjozork.cityscape.storage.model.SerializationContext

import okio.Buffer
import okio.BufferedSource

import kotlin.reflect.KClass

/**
 * Serializes the receiver [Serializable] into a [ByteArray]
 *
 * @receiver the object to serialize
 *
 * @return ByteArray
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified OC : Serializable> OC.serializeObject(ctx: SerializationContext): ByteArray {
    val props = this::class.serializedProps()

    val finalBytes = mutableListOf<Byte>()

    finalBytes.add(OBJ_BYTE)

    val classHash = this::class.TYPE_HASH
    finalBytes.addAll(classHash.toTypedArray())

    if (!ctx.classMap.mapKeys { it.key.getBytes() }.containsKey(classHash)) {
        ctx.classMap[this::class.simpleName!!.getAdler32Int()] = this::class.qualifiedName!!
    }

    finalBytes.addAll(props.size.getBytes().toTypedArray())

    props.forEach { k, v ->
        finalBytes.addAll(k.getBytes().toTypedArray())
        finalBytes.addAll(v.serialize(ctx, this).toTypedArray())
    }
    return finalBytes.toByteArray()
}

/**
 * Serializes the receiver [Serializable] into a [ByteArray] of it's reference
 *
 * @receiver the object to serialize
 *
 * @return ByteArray
 */
internal fun <E : Serializable> E.serializeAsObjectReference(ctx: SerializationContext): ByteArray {

    val ref = this.reference

    ctx.serializeObjByRef(this)

    return this.reference.getBytes()
}

/**
 * Reads the next [Serializable] object found in the receiver [Buffer]
 *
 * @receiver Buffer
 *
 * @return Serializable
 */
@Suppress("UNCHECKED_CAST")
fun DeserializationContext.deSerializeObject(buffer: BufferedSource, targetReference: Int): Serializable {

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
        deserializedProps[currentProp] = propList[i].deSerialize(this, buffer)
    }

    // Instantiate object and populate props
    val inst = fabricateInstance(objClass, deserializedProps)

    // Set reference
    inst.reference = targetReference

    return inst
}

fun DeserializationContext.deSerializeObjectReference(buffer: BufferedSource): Serializable {

    // Read reference
    val reference = buffer.readInt()

    return this.getDeserializedObjByRef(reference)
}