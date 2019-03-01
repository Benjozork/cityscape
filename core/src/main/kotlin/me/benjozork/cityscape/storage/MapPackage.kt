@file:Suppress("MemberVisibilityCanBePrivate")

package me.benjozork.cityscape.storage

import com.badlogic.gdx.assets.AssetDescriptor

import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.log.Logger

import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.exception
import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.serialization.deSerializeObject
import me.benjozork.cityscape.storage.serialization.deSerializeMap

import okio.buffer
import okio.source

import java.io.File

private const val OBJECT_DIR_PATH = "/_objects/"

private const val CLASSMAP_PATH = "/classmap.bin"
private const val ASSETMAP_PATH = "/assetmap.bin"

/**
 * This class represents a decompressed, extracted map package located within a single folder.
 *
 * @property filesRoot    This is the root of the whole package
 * @property objectsRoot  This is where objects are localted, which is always `./_objects/` relative to [filesRoot]
 * @property classMapPath This is where the classmap is located, which is always `./classmap.bin` relative to [filesRoot]
 * @property assetMapPath This is where the assetmap is located, which is always `./assetmap.bin` relative to [filesRoot]
 *
 * @property dctx This is the [DeserializationContext] that is used for de-serializing *everything* in this package
 *
 * @constructor Creates a new [MapPackage] with a certain [root] folder as it's [filesRoot]
 *
 * @author Benjozork
 */
class MapPackage(root: File) {

    val filesRoot   = root
    val objectsRoot = File(filesRoot.path + OBJECT_DIR_PATH)

    val classMapPath = File(filesRoot.path + CLASSMAP_PATH)
    val assetMapPath = File(filesRoot.path + ASSETMAP_PATH)

    lateinit var dctx: DeserializationContext

    /**
     * This initializes the [DeserializationContext], which is necessary before the package is read
     */
    fun initDserializer() {
        dctx = DeserializationContext()

        if (!classMapPath.exists()) {
            log.error { "dctx can't be initialized: couldn't find \"${CLASSMAP_PATH.drop(1)}\"" }
            return
        }

        if (!classMapPath.exists()) {
            log.error { "dctx can't be initialized: couldn't find \"${ASSETMAP_PATH.drop(1)}\"" }
            return
        }

        // Load classmap
        try {
            dctx.classMap = dctx.deSerializeMap<Int, String>(classMapPath.source().buffer())
                    .mapValues { Class.forName(it.value).kotlin }
                    .toMutableMap()
        } catch (e: Exception) {
            log.exception("dctx can't be initialized: error occured while reading classmap", e)
            return
        }

        log.debug { "found ${dctx.classMap.size} classes in the classmap" }

        // Load assetmap
        try {
            dctx.assetMap = dctx.deSerializeMap<Int, String>(assetMapPath.source().buffer())
                    .mapValues {
                        @Suppress("UNCHECKED_CAST")
                        AssetDescriptor(it.value.substringBefore('$'), Class.forName(it.value.substringAfter('$')) as Class<ReferenceableAsset>)
                    }
                    .toMutableMap()
        } catch (e: Exception) {
            log.exception("dctx can't be initialized: error occured while reading assetmap", e)
            return
        }

        log.debug { "found ${dctx.assetMap.size} assets in the assetmap" }

        dctx.initialized = true
    }

    fun readAssetsToLoad(): GdxArray<AssetDescriptor<ReferenceableAsset>> {

        val safeDctxInitialized = if(this::dctx.isInitialized) !dctx.initialized else true

        if (!this::dctx.isInitialized || safeDctxInitialized) {
            log.error { "map package can't be loaded: DeserializationContext is not initialized" }
            return gdxArrayOf()
        }

        return dctx.assetMap.values.toGdxArray()
    }

    /**+
     * This reads and returns *all* objects in "_objects/"
     * @return Collection<Object>
     */
    fun readObjects(): Collection<Object> {

        val safeDctxInitialized = if(this::dctx.isInitialized) !dctx.initialized else true

        if (!this::dctx.isInitialized || safeDctxInitialized) {
            log.error { "map package can't be loaded: DeserializationContext is not initialized" }
            return emptyList()
        }

        // Get all object files
        val objFiles = objectsRoot.listFiles().filter { it.extension == "blob" && it.nameWithoutExtension.length == 8 }
        log.debug { "found ${objFiles.size} object files" }

        val parsedObjects = mutableListOf<Object>()

        objFiles.forEach {
            if (it.length() == 0L) {
                log.debug { "couldn't load object ${it.nameWithoutExtension}: file is empty" }
            } else {
                try {
                    parsedObjects.add(readObjectFromFile(it))
                } catch (e: Exception) {
                    log.exception("couldn't load object ${it.nameWithoutExtension}: error occured during reading", e)
                }
            }
        }

        return parsedObjects.toList()

    }

    /**
     * This reads a single [Object] from a certain file using the default [DeserializationContext]
     *
     * @param src the [File] to read the [Object] from
     *
     * @return T
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Object> readObjectFromFile(src: File): T {
        return (dctx.deSerializeObject(src.source().buffer()) as T)
    }

    companion object {
        val log = Logger(tag = "map-loader")
    }

}