package me.benjozork.cityscape.storage

import me.benjozork.cityscape.storage.model.DeserializationContext

import okio.buffer
import okio.source

import java.io.File

import kotlin.reflect.KClass

val FILE_HEADER = byteArrayOf(0x6a, 0x75, 0x6e, 0x69, 0x65)
val CSF_VERSION = 0x01.toByte()

class CSFFileWriter {

    private val classMap = mutableMapOf<Int, String>()

    fun addClassToMap(klass: KClass<*>) {
        this.classMap[klass.simpleName!!.getAdler32Int()] = klass.qualifiedName!!
    }

    fun addClassToMap(vararg klasses: KClass<*>) {
        klasses.forEach { klass -> this.classMap[klass.simpleName!!.getAdler32Int()] = klass.qualifiedName!! }
    }

    fun writeTo(dest: File, data: ByteArray) {

        // Write file signature and version
        dest.writeBytes(FILE_HEADER)
        dest.appendBytes(byteArrayOf(CSF_VERSION))

        // Write classMap

        dest.appendBytes(classMap.serialize())
        dest.appendBytes(data)
    }
}

class CSFFileReader {

    fun readFrom(src: File): DeserializationContext {

        val ctx = DeserializationContext(src.source().buffer())

        val fileHeaderCheck = ctx.buffer.readByteArray(FILE_HEADER.size.toLong())
        if (!fileHeaderCheck.contentEquals(FILE_HEADER)) error("invalid CSF signature")

        val fileVersionCheck = ctx.buffer.readByte()
        if (fileVersionCheck < CSF_VERSION) error("unprocessable file version \"$fileVersionCheck\"")

        val classMap = ctx.deserializeNextMap<Int, String>().mapValues { v -> Class.forName(v.value).kotlin }
        ctx.classMap = classMap.toMutableMap()

        return ctx
    }

}