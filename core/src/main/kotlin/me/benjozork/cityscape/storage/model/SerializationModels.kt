package me.benjozork.cityscape.storage.model

import me.benjozork.cityscape.assets.AssetLocator
import me.benjozork.cityscape.storage.MapPackage

import kotlin.random.Random
import kotlin.reflect.KMutableProperty1

/**
 * Represents a member property of a certain class.
 */
typealias SProp<O> = KMutableProperty1<in O, Any?>

/**
 * Marks a property that must *not* be serialized.
 */
    @Target(AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
annotation class NotSerialized

/**
 * Marks a property that is to be serialized using its reference. It's type must inherit [Serializable].
 */
    @Target(AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
annotation class SerializeReference

/**
 * Marks a function (or constructor) that is to be called when an object of it's declaring class is serialized.
 */
    @Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
annotation class CallOnSerialize

/**
 * A object of [Referenceable] type is serialized only using it's [reference], not it's full contents.
 *
 * @property reference the reference used to refer to the object
 */
open class Referenceable {

    @NotSerialized
    var reference: Int

    constructor() {
        this.reference = generateReference()
    }

    companion object {

        private fun generateReference(): Int {
            return Random.nextInt(Int.MAX_VALUE)
        }

    }

}

/**
 * A subclass of [Serializable] can be serialized into HDBS format.
 */
open class Serializable : Referenceable()

class SerializationContext(private val serializer: MapPackage.Serializer) {

    var initialized = false

    var classMap     = mutableMapOf<Int, String>()
        internal set

    var assetMap = mutableMapOf<Int, AssetLocator>()
        internal set

    fun serializeObjByRef(obj: Serializable) {
        if (!this.serializer.isStored(obj)) this.serializer.addObject(obj)
    }

}