package me.benjozork.cityscape.assets

import me.benjozork.cityscape.storage.model.Referenceable

/**
 * An asset that has a reference associated with it; that reference should *only* be used for referencing assets
 * inside [MapPackages][me.benjozork.cityscape.storage.MapPackage], and *never* elsewhere.
 *
 * To locate [ReferenceableAssets][ReferenceableAsset] inside the game files or extension files, use [AssetLocator].
 *
 * @author Benjozork
 */
open class ReferenceableAsset : Referenceable()