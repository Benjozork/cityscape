package me.benjozork.cityscape.game

import me.benjozork.cityscape.game.`object`.model.Object

object GameWorld {

    private val objectsData = mutableListOf<Object>()

    val objects
        get() = this.objectsData.toList()

    fun registerObject(obj: Object): Boolean {
        return this.objectsData.add(obj)
    }

    fun registerObjects(objs: Collection<Object>): Boolean {
        return this.objectsData.addAll(objs)
    }

    fun unregisterObject(obj: Object): Boolean {
        return this.objectsData.remove(obj)
    }

    fun unregisterObjects(objs: Collection<Object>): Boolean {
        return this.objectsData.removeAll(objs)
    }

    fun update() = this.objectsData.forEach { it.update() }

    fun draw() = this.objectsData.forEach { it.draw() }

}