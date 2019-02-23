package me.benjozork.cityscape.storage

import me.benjozork.cityscape.storage.model.DeserializationContext

import java.nio.ByteBuffer
import java.util.*
import java.util.zip.Adler32

import kotlin.reflect.KClass

private const val BUFFER_SIZE = 4_096

private var tempBuffer = ByteBuffer.allocate(BUFFER_SIZE)

/**
 *
 *
 * @receiver Any?
 *
 * @return ByteArray
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
internal fun <E> E?.serializeAsPrimitive(): ByteArray {
    return when {
        this is Number -> this.getBytes()
        this is String -> java.lang.String(this + '\u0000').getBytes(Charsets.UTF_8) // CHANGE THIS !
        else -> ByteArray(0)
    }
}

/**
 *
 *
 * @receiver Number
 *
 * @return ByteArray
 */
internal fun Number.getBytes(): ByteArray {
    checkCleanup()
    when {
        this is Int -> {
            tempBuffer.putInt(this)
            return returnByteResult(Int.SIZE_BYTES)
        }
        this is Long -> {
            tempBuffer.putLong(this)
            return returnByteResult(Long.SIZE_BYTES)
        }
        this is Float -> {
            tempBuffer.putFloat(this)
            return returnByteResult(java.lang.Float.BYTES)
        }
        this is Double -> {
            tempBuffer.putDouble(this)
            return returnByteResult(java.lang.Double.BYTES)
        }
        this is Short -> {
            tempBuffer.putShort(this)
            return returnByteResult(java.lang.Short.BYTES)
        }
        else -> error("unsupported collection type \"${this::class.simpleName}\"")
    }
}


/**
 *
 *
 * @receiver Number
 *
 * @return ByteArray
 */
internal fun DeserializationContext.deSerializeNextPrimitive(typeClass: KClass<*>): Any {
    if (typeClass == String::class) {
        var stringBytes = byteArrayOf(buffer.readByte())
        var nextByte = 0xFF.toByte()
        while (nextByte != 0x00.toByte()) {
            nextByte = buffer.readByte()
            stringBytes += nextByte
        }
        return java.lang.String(stringBytes.dropLast(1).toByteArray(), Charsets.UTF_8)
    }
    checkCleanup()
    return when (typeClass) {
        Int::class -> {
            tempBuffer.put(buffer.readByteArray(Int.SIZE_BYTES.toLong()))
            tempBuffer.getInt(tempBuffer.position() - Int.SIZE_BYTES)
        }
        Long::class -> {
            tempBuffer.put(buffer.readByteArray(Long.SIZE_BYTES.toLong()))
            tempBuffer.getLong(tempBuffer.position() - java.lang.Float.BYTES)
        }
        Float::class -> {
            tempBuffer.put(buffer.readByteArray(java.lang.Float.BYTES.toLong()))
            tempBuffer.getFloat(tempBuffer.position() - java.lang.Float.BYTES)
        }
        Double::class -> {
            tempBuffer.put(buffer.readByteArray(java.lang.Double.BYTES.toLong()))
            tempBuffer.getDouble(tempBuffer.position() - java.lang.Double.BYTES)
        }
        Short::class -> {
            tempBuffer.put(buffer.readByteArray(java.lang.Short.BYTES.toLong()))
            tempBuffer.getShort(tempBuffer.position() - java.lang.Short.BYTES)
        }
        else -> error("unsupported number type \"${typeClass.simpleName}\"")
    }
}

private object Adler {
    internal val adler = Adler32()
}

/**
 * Returns this [String]'s [Adler32] hash in byte format
 *
 * @receiver String
 *
 * @return ByteArray
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun String.getAdler32(): ByteArray {
    Adler.adler.reset()
    Adler.adler.update((this as java.lang.String).getBytes(Charsets.UTF_8))
    return Adler.adler.value.getBytes().sliceArray(4..7)
}

/**
 * Returns this [String]'s [Adler32] hash in byte format
 *
 * @receiver String
 *
 * @return ByteArray
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun String.getAdler32Int(): Int {
    Adler.adler.reset()
    Adler.adler.update((this as java.lang.String).getBytes(Charsets.UTF_8))
    return Adler.adler.value.toInt()
}

private fun checkCleanup() {
    if (tempBuffer.position() >= tempBuffer.limit() * .75) {
        tempBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    }
}

private fun returnByteResult(numBytes: Int): ByteArray {
    return Arrays.copyOfRange(tempBuffer.array(), tempBuffer.position() - numBytes, tempBuffer.position())
}