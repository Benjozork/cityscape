package me.benjozork.cityscape.game.editor.tool

import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor
import me.benjozork.cityscape.game.input.GameInputController

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

object ToolManager {

    private val tools = mutableMapOf<KClass<out EditorTool>, EditorTool>()

    private var currentToolInputProcessor: ToolInputProcessor? = null

    var currentTool: EditorTool? = null
        private set(value) {
            val inputProcessorClass = value!!::class.nestedClasses.find { it.isSubclassOf(ToolInputProcessor::class) } ?: error("provided tool's class doesn't contain a tool input processor")
            val ipInstance: ToolInputProcessor

            try {
                ipInstance = inputProcessorClass.constructors.first().call(value) as ToolInputProcessor
            } catch (e: Exception) {
                error("invalid constructor for tool class ${inputProcessorClass.simpleName}")
            }

            GameInputController.removeProcessor(this.currentToolInputProcessor)
            GameInputController.addProcessor(ipInstance)

            this.currentToolInputProcessor = ipInstance

            field?.onUnload()
            field = value
            field?.onLoad()
        }


    fun <E : EditorTool> switchTool(toolClass: KClass<E>) {
        currentTool = tools.getOrPut(toolClass) {
            toolClass.createInstance()
        }
    }

}