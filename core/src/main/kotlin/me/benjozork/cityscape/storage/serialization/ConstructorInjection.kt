package me.benjozork.cityscape.storage.serialization

import me.benjozork.cityscape.storage.model.SProp
import me.benjozork.cityscape.storage.model.Serializable

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Fabricates an instance of the given class with the given property/value map
 *
 * @param target KClass<Serializable>
 * @param propMap Map<SProp<Serializable>, Any?>
 *
 * @return Serializable
 */
internal fun fabricateInstance(target: KClass<Serializable>, propMap: Map<SProp<Serializable>, Any?>): Serializable {
    val ctor = findAdequateConstructor(target, propMap) ?: error("no adequate constructor found for class \"${target.simpleName}\"")

    val alignedCtorCallPayload = propMap.entries
            .associate { (prop, value) ->
                ctor.parameters.indexOfFirst {
                    it.name == prop.name && it.type == prop.returnType
                } to (prop to value)
            }
            .filter { it.key != -1 }
            .toSortedMap()
            .map { it.value.second }
            .toTypedArray()

    return ctor.call(*alignedCtorCallPayload)
}

@Suppress("UNCHECKED_CAST")
private fun findAdequateConstructor(target: KClass<Serializable>, propMap: Map<SProp<Serializable>, Any?>): KFunction<Serializable>? {
    val targetCtors = target.constructors

    return targetCtors.firstOrNull { ctor ->
        ctor.parameters.all { param ->
            propMap.any { prop -> prop.key.name == param.name && prop.key.returnType == param.type }
        }
    }.also { res ->
        constCache[target] = res
    }
} private val constCache = mutableMapOf<KClass<Serializable>, KFunction<Serializable>?>()
    get() { return if (field.size > 25) { field.clear(); field } else field }