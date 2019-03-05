package me.benjozork.cityscape.storage.model

import com.badlogic.gdx.assets.AssetDescriptor

import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.storage.MapPackage
import me.benjozork.cityscape.storage.OBJECT_FILE_EXT

import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

import kotlin.reflect.KClass

/**
 * Represents the context of an ongoing deserialization
 *
 * @property initialized whether this [DeserializationContext] is ready to be used or not
 * @property classMap    a map of class hashes (as [ints][Int])
 * @property assetMap    a map of asset refrences (as [ints][Int])
 */
class DeserializationContext(private val deserializer: MapPackage.Deserializer) {

    var initialized = false

    var classMap     = mutableMapOf<Int, KClass<*>>()
        internal set

    var assetMap     = mutableMapOf<Int, AssetDescriptor<ReferenceableAsset>>()
        internal set

    private val deSerializedObjects = mutableMapOf<Int, Object>()

    fun getDeserializedObjByRef(reference: Int): Object {
        // TODO will change
        return deSerializedObjects.getOrPut(reference) {
            return try {
                deserializer.readObjectFromFile(File(deserializer.parentPackage.objectsRoot.path + "\\$reference.$OBJECT_FILE_EXT"))
            } catch (e: FileNotFoundException) {
                error("object $reference, which the current object was dependent on, was not found")
            } catch (e : Exception) {
                error("${e::class.simpleName} while reading object $reference, which the current object was dependent on")
            }
        }
    }

}