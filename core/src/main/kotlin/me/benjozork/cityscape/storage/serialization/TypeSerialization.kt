package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.storage.model.Referenceable
import me.benjozork.cityscape.storage.model.Serializable

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

const val OBJ_BYTE = 0xA0.toByte()
const val REF_BYTE = 0xA0.toByte()

const val INT_BYTE    = 0xB0.toByte()
const val LONG_BYTE   = 0xB1.toByte()
const val FLOAT_BYTE  = 0xB2.toByte()
const val DOUBLE_BYTE = 0xB3.toByte()
const val SHORT_BYTE  = 0xB4.toByte()
const val CHAR_BYTE   = 0xB5.toByte()
const val STRING_BYTE = 0xB6.toByte()

const val PRIM_LIST_BYTE = 0xC0.toByte()
const val PRIM_SET_BYTE  = 0xC1.toByte()

const val OBJ_LIST_BYTE = 0xD0.toByte()
const val OBJ_SET_BYTE  = 0xD1.toByte()

const val MAP_BYTE = 0xE0.toByte()

val typeByteMap = mapOf<KClass<*>, Byte>(
        Int::class    to INT_BYTE,
        Long::class   to LONG_BYTE,
        Float::class  to FLOAT_BYTE,
        Double::class to DOUBLE_BYTE,
        Short::class  to SHORT_BYTE,
        Char::class   to CHAR_BYTE,
        String::class to STRING_BYTE
)

val classMap = typeByteMap.entries.associate { (k, v) -> v to k }

/**
 * Represents this [KClass]'s HDBS type byte. Only works for [String], [Referenceable] types and primitives.
 */
inline val <reified E : Any> KClass<E>.TYPE_BYTE: Byte
    get() {
        return typeByteMap[E::class] ?: when {
            this == String::class -> STRING_BYTE
            this is Referenceable -> REF_BYTE
            else                  -> OBJ_BYTE
        }
    }

/**
 * Represents this collection [KType]'s HDBS type byte. Only works for [List], [Set] and [Map].
 */
val KType.COLLECTION_TYPE_BYTE: Byte
    get() {
        val typeClass = this.classifier as KClass<*>
        return when {
            (this.arguments[0].type!!.classifier as KClass<*>).isSubclassOf(Serializable::class) ->
                when (typeClass) {
                    List::class -> OBJ_LIST_BYTE
                    Set::class  -> OBJ_SET_BYTE
                    Map::class  -> MAP_BYTE
                    else -> error("unsupported collection type \"${this::class.simpleName}\"")
                }
            else ->
                when (typeClass) {
                    List::class      -> PRIM_LIST_BYTE
                    Set::class       -> PRIM_SET_BYTE
                    Map::class       -> MAP_BYTE
                    else -> error("unsupported collection type \"${this::class.simpleName}\"")
                }
        }
    }

/**
 * Represents this [KClass]'s type hash. Only works for objects that inherit [Serializable].
 */
@Suppress("unused")
inline val <reified S : Serializable> KClass<out S>.TYPE_HASH: ByteArray
    get() = simpleName!!.getAdler32()

/**
 *
 */
val Byte.TYPE_CLASS: KClass<*>
    get() {
        return classMap[this] ?: when {
            this == OBJ_BYTE -> Serializable::class
            this == REF_BYTE -> Referenceable::class
            else -> error("unsupported type byte \"$this\"")
        }
    }