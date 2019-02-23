package me.benjozork.cityscape.storage

import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.Serializable

import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

/**
 * Serializes the receiver [Set] into a [ByteArray]
 *
 * @receiver Set<E>
 *
 * @return ByteArray
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified E: Any> Set<E>.serialize(): ByteArray {
    val finalBytes = mutableListOf<Byte>()

    // Create a KType object containing the Set class and the current type noted by E::class
    val setKType = Set::class.createType(listOf(KTypeProjection.invariant(E::class.createType())))

    // Add the collection type byte
    finalBytes.add(setKType.COLLECTION_TYPE_BYTE)

    // Add the data type byte(s)
    finalBytes.addAll (
            when {
                E::class.isSubclassOf(Serializable::class) -> {
                    val typeHash = (setKType.arguments[0].type?.classifier as KClass<Serializable>).TYPE_HASH

                    typeHash.toList()
                }

                else ->
                    byteArrayOf(E::class.TYPE_BYTE).toList()
            }
    )

    // Build payload
    val payload = listOf(*this.map {
        when {
            E::class.isSubclassOf(Serializable::class) -> {
                (it as Serializable).serialize()
            }

            else ->
                it.serializeAsPrimitive()
        }
    }.toTypedArray())

    // Add payload length
    finalBytes.addAll(payload.size.getBytes().toTypedArray())

    // Add payload
    payload.forEach { finalBytes.addAll(it.toTypedArray()) }

    return finalBytes.toByteArray()
}

/**
 * Serializes the receiver [List] into a [ByteArray]
 *
 * @receiver List<E>
 *
 * @return ByteArray
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified E: Any> List<E>.serialize(): ByteArray {
    val finalBytes = mutableListOf<Byte>()

    // Create a KType object containing the List class and the current type noted by E::class
    val listKType = List::class.createType(listOf(KTypeProjection.invariant(E::class.createType())))

    // Add the collection type byte
    finalBytes.add(listKType.COLLECTION_TYPE_BYTE)

    // Add the data type byte(s)
    finalBytes.addAll (
        when {
            E::class.isSubclassOf(Serializable::class) ->
                (listKType.arguments[0].type?.classifier as KClass<Serializable>).TYPE_HASH.toList()

            else ->
                byteArrayOf(E::class.TYPE_BYTE).toList()
        }
    )

    // Build payload
    val payload = listOf(*this.map {
        when {
            E::class.isSubclassOf(Serializable::class) ->
                (it as Serializable).serialize()

            else ->
                it.serializeAsPrimitive()
        }
    }.toTypedArray())

    // Add payload length
    finalBytes.addAll(payload.size.getBytes().toTypedArray())

    // Add payload
    payload.forEach { finalBytes.addAll(it.toTypedArray()) }

    return finalBytes.toByteArray()
}

/**
 * Serializes the receiver [Map] into a [ByteArray]
 *
 * @receiver Map<K, V>
 *
 * @return ByteArray
 */
internal inline fun <reified K : Any, reified V : Any> Map<K, V>.serialize(): ByteArray {
    val finalBytes = mutableListOf<Byte>()

    val mapKType = Map::class.createType(listOf(KTypeProjection.invariant(K::class.createType()), KTypeProjection.invariant(V::class.createType())))

    // Add the collection type byte
    finalBytes.add(mapKType.COLLECTION_TYPE_BYTE)

    // Create the two subsets
    val keySet   = this.keys.toList()
    val valueSet = this.values.toList()

    finalBytes.addAll((keySet.serialize() + valueSet.serialize()).toTypedArray())

    return finalBytes.toByteArray()
}

/**
 *
 *
 * @receiver DeserializationContext
 *
 * @return Set<E>
 */
fun <E> DeserializationContext.deSerializeNextSet(): Set<E> {
    // Read the collection type byte
    val collectionTypeByte = buffer.readByte()

    when (collectionTypeByte) {
        PRIM_SET_BYTE -> {
            // Read metadata

            val typeClass = buffer.readByte().TYPE_CLASS
            val numElems = buffer.readInt()

            val finalPrimSet = mutableSetOf<E>()

            @Suppress("UNCHECKED_CAST")
            for (i in 1..numElems) finalPrimSet.add(this.deSerializeNextPrimitive(typeClass) as E)

            return finalPrimSet
        }

        OBJ_SET_BYTE  -> {
            // Read metadata

            val numElems = buffer.readInt()

            val finalObjSet = mutableSetOf<E>()

            @Suppress("UNCHECKED_CAST")
            for (i in 1..numElems) finalObjSet.add(this.deSerializeNextObject() as E)

            return finalObjSet
        }

        else -> error("wrong or unsupported collection type \"$collectionTypeByte\"")
    }

}


/**
 *
 *
 * @receiver DeserializationContext
 *
 * @return List<E>
 */
fun <E : Any> DeserializationContext.deserializeNextList(): List<E> {
    // Read the collection type byte
    val collectionTypeByte = buffer.readByte()

    when (collectionTypeByte) {
        PRIM_LIST_BYTE -> {
            // Read metadata

            val typeClass = buffer.readByte().TYPE_CLASS
            val numElems = buffer.readInt()

            val finalPrimSet = mutableListOf<E>()

            @Suppress("UNCHECKED_CAST")
            for (i in 1..numElems) finalPrimSet.add(this.deSerializeNextPrimitive(typeClass) as E)

            return finalPrimSet
        }

        OBJ_LIST_BYTE  -> {
            // Read metadata

            val typeHash = buffer.readInt()
            //val objClass = classMap[typeHash] ?: error("unknown class hash ${typeHash}: nothing was found in the class map")

            val numElems = buffer.readInt()

            val finalObjSet = mutableListOf<E>()

            @Suppress("UNCHECKED_CAST")
            for (i in 1..numElems) finalObjSet.add(this.deSerializeNextObject() as E)

            return finalObjSet
        }

        else -> error("wrong or unsupported collection type \"$collectionTypeByte\"")
    }

}


/**
 *
 *
 * @receiver DeserializationContext
 *
 * @param keyTypeClass
 * @param valueTypeClass
 *
 * @return Map<K, V>
 */
fun <K : Any, V : Any> DeserializationContext.deserializeNextMap(): Map<K, V> {
    buffer.readByte()
    val keys   = deserializeNextList<K>()
    val values = deserializeNextList<V>()
    return keys.mapIndexed { i, k -> k to values[i] }.toMap()

}
