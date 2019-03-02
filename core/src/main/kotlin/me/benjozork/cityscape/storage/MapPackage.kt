package me.benjozork.cityscape.storage

import com.badlogic.gdx.assets.AssetDescriptor

import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.log.Logger

import me.benjozork.cityscape.assets.AssetLocator
import me.benjozork.cityscape.assets.ReferenceableAsset
import me.benjozork.cityscape.exception
import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.storage.model.DeserializationContext
import me.benjozork.cityscape.storage.model.SerializationContext
import me.benjozork.cityscape.storage.serialization.deSerializeObject
import me.benjozork.cityscape.storage.serialization.deSerializeMap
import me.benjozork.cityscape.storage.serialization.serialize

import okio.buffer
import okio.source

import java.io.File

private const val OBJECT_DIR_PATH = "/_objects/"
private const val OBJECT_FILE_EXT = "bin"

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
 * @constructor Creates a new [MapPackage] with a certain [root] folder as it's [filesRoot]
 *
 * @author Benjozork
 */
class MapPackage(root: File) {

    val filesRoot   = root
    val objectsRoot = File(filesRoot.path + OBJECT_DIR_PATH)

    val classMapPath = File(filesRoot.path + CLASSMAP_PATH)
    val assetMapPath = File(filesRoot.path + ASSETMAP_PATH)

    val deserializer = this.Deserializer()
    val serializer   = this.Serializer()

    /**
     * Deserializer compoennt of the loaded package
     *
     * @property dctx This is the [DeserializationContext] that is used for de-serializing *everything* in this package
     */
    inner class Deserializer internal constructor() {

        private lateinit var dctx: DeserializationContext

        /**
         * This initializes the [DeserializationContext], which is necessary before the package is read
         */
        fun init() {
            dctx = DeserializationContext()

            val tempSctx = SerializationContext()

            if (!classMapPath.exists()) {
                writeClassMap(emptyMap(), tempSctx)
                log.debug { "dctx-init: classmap didn't exist, created it" }
            }

            if (!assetMapPath.exists()) {
                writeAssetMap(emptyMap(), tempSctx)
                log.debug { "dctx-init: assetmap didn't exist, created it" }
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

            val safeDctxInitialized = if(this::dctx.isInitialized) dctx.initialized else false

            if (!this::dctx.isInitialized || !safeDctxInitialized) {
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
            val objFiles = objectsRoot.listFiles().filter { it.extension == OBJECT_FILE_EXT }
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
            return (dctx.deSerializeObject(src.source().buffer(), src.nameWithoutExtension.toInt()) as T)
        }

    }

    /**
     * Serializer compoennt of the loaded package
     *
     * @property sctx This is the [SerializationContext] that is used for serializing *everything* in this package
     */
    inner class Serializer {

        private lateinit var sctx: SerializationContext

        /**
         * This initializes the [SerializationContext], which is necessary before the package is written to
         */
        fun init() {
            sctx = SerializationContext()

            // Check if classmap/assetmap exist; attempt to create them if not

            if (!classMapPath.exists()) {
                writeClassMap(emptyMap(), sctx)
                log.debug { "sctx-init: classmap didn't exist, created it" }
            }

            if (!classMapPath.exists()) {
                writeAssetMap(emptyMap(), sctx)
                log.debug { "sctx-init: assetmap didn't exist, created it" }
            }

            sctx.initialized = true
        }

        /**
         * This terminates the serialization and de-initializes the [SerializationContext]. Must be called in order for the package to be saved correctly
         */
        fun close() {
            writeClassMap(sctx.classMap, sctx)
            writeAssetMap(sctx.assetMap, sctx)

            sctx.initialized = false
        }

        /**
         * Adds an object into the package.
         * @param obj the object to add
         */
        fun addObject(obj: Object) {

            // If sctx is not initialized
            if (!sctx.initialized) {
                log.error { "map package can't be updated: SeserializationContext is not initialized" }
                return
            }

            val targetObjFile = File("${objectsRoot.path}/${obj.reference}.$OBJECT_FILE_EXT")

            // If object is already stored
            if (targetObjFile.exists()) {
                log.error { "map package can't be updated: object ${obj.reference} can't be created, it already exists" }
                return
            }

            try {
                targetObjFile.createNewFile()                 // Write object file
                targetObjFile.writeBytes(obj.serialize(sctx)) // -----------------
            } catch (e: Exception) {
                log.exception("map package can't be updated: couldn't write object ${obj.reference}: error occured during writing", e)
                return
            }

        }

        /**
         * Adds objects into the package.
         * @param objs the objects to add
         */
        fun addObjects(vararg objs: Object) {
            objs.forEach { addObject(it) }
        }

        /**
         * Deletes an object from the package. THIS IS IRREVERSIBLE.
         * @param obj the object to delete
         */
        fun deleteObject(obj: Object) {

            // If sctx is not initialized
            if (!sctx.initialized) {
                log.error { "map package can't be updated: SeserializationContext is not initialized" }
                return
            }

            val targetObjFile = File("${objectsRoot.path}/${obj.reference}.$OBJECT_FILE_EXT")

            // If object is already non-existent
            if (!targetObjFile.exists()) {
                log.error { "map package can't be updated: object can't be deleted, it doesn't already exist" }
                return
            }

            try {
                targetObjFile.deleteOnExit()
            } catch (e: Exception) {
                log.exception("couldn't delete object ${obj.reference}: error occured during deletion", e)
            }

        }

        /**
         * Deletes objects from the package. THIS IS IRREVERSIBLE.
         * @param objs the objects to delete
         */
        fun deleteObjects(vararg objs: Object) {
            objs.forEach { deleteObject(it) }
        }

        /**
         * Updates an existing object in the package.
         * @param obj the object to add
         */
        fun updateObject(obj: Object) {
            // TODO for now we just re-write

            // If sctx is not initialized
            if (!sctx.initialized) {
                log.error { "map package can't be updated: SeserializationContext is not initialized" }
                return
            }

            val targetObjFile = File("${objectsRoot.path}/${obj.reference}.$OBJECT_FILE_EXT")

            // If object is non-existent
            if (!targetObjFile.exists()) {
                log.error { "map package can't be updated: object can't be updated, it doesn't exist" }
                return
            }

            targetObjFile.writeBytes(obj.serialize(sctx))

        }

        /**
         * Updates existing objects in the package.
         * @param objs the objects to add
         */
        fun updateObjects(vararg objs: Object) {
            objs.forEach { updateObject(it) }
        }

        /**
         * @return whether the object is already stored or not
         */
        fun isStored(obj: Object): Boolean {
            return objectsRoot.list().contains("${obj.reference}.$OBJECT_FILE_EXT")
        }

    }

    /**
     * Writes the provided classmap into classmap.bin
     */
    internal fun writeClassMap(map: Map<Int, String>, ctx: SerializationContext) {
        if (classMapPath.exists()) classMapPath.delete()
        classMapPath.createNewFile()
        classMapPath.writeBytes(map.serialize(ctx))
    }

    /**
     * Writes the provided assetmap into assetmap.bin
     */
    internal fun writeAssetMap(map: Map<Int, AssetLocator>, ctx: SerializationContext) {
        if (assetMapPath.exists()) assetMapPath.delete()
        assetMapPath.createNewFile()
        assetMapPath.writeBytes(map.serialize(ctx))
    }

    companion object {
        val log = Logger(tag = "map-loader")
    }

}