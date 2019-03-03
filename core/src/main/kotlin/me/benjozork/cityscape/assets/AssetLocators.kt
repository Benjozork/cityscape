package me.benjozork.cityscape.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle

import me.benjozork.cityscape.storage.model.Serializable

import kotlin.reflect.KClass

private const val NAMESPACE_DELIM = ":"
private const val TYPE_DELIM = "$"

/**
 * An [AssetLocator] can locate a [ReferenceableAsset] inside the game or in another module.
 *
 * @property namespace Represents which "module" contains this asset. The default namespace is `cityscape`
 * @property path      The path, inside the [namespace] root, which is used to find the located asset
 * @property type      The type of the located asset
 *
 * @constructor Creates an [AssetLocator] from a string of the following format: `namespace:path$type`
 *
 * @author Benjozork
 */
 class AssetLocator(private val stringForm: String) : Serializable() {

    constructor(namespaceAndPath: String, type: KClass<out ReferenceableAsset>)
            : this("$namespaceAndPath\$${type.qualifiedName}")

    val namespace = stringForm.substringBefore(NAMESPACE_DELIM)

    val path = stringForm.substringAfter(NAMESPACE_DELIM).substringBefore(TYPE_DELIM)

    val type = stringForm.substringAfter(TYPE_DELIM)

    val typeLessString = "$namespace:$path"

    operator fun invoke(): String {
        return this.toString()
    }

    /**
     * Produces a representation of this [AssetLocator] in this format: `namespace:path$type`
     */
    override fun toString(): String {
        return namespace + NAMESPACE_DELIM + path + TYPE_DELIM + type
    }

}

/**
 * Provides a [FileHandle] for [AssetLocators][AssetLocator]
 */
class AssetLocatorFileHandleResolver : FileHandleResolver {

    override fun resolve(fileName: String?): FileHandle {
        val locator = AssetLocator(stringForm = fileName!!)

        if (locator.namespace == "cityscape") {
            return Gdx.files.internal(locator.path)
        }

        //TODO handle other namespaces
        return Gdx.files.absolute("null")
    }

}