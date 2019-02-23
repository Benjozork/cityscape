package me.benjozork.cityscape.storage.model

import okio.Buffer
import okio.BufferedSource

import kotlin.reflect.KClass

class DeserializationContext (
        val buffer: BufferedSource = Buffer()
) {
    var referenceMap = mutableMapOf<List<Byte>, Serializable>()
        internal set

    var classMap     = mutableMapOf<Int, KClass<*>>()
        internal set
}