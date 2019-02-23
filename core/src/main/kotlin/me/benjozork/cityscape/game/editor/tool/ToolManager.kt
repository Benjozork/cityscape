package me.benjozork.cityscape.game.editor.tool

import com.badlogic.gdx.Input

import me.benjozork.cityscape.game.editor.tool.model.ToolInputProcessor
import me.benjozork.cityscape.game.input.AbstractInputProcessor
import me.benjozork.cityscape.game.input.GameInputController

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

object ToolManager {

    init {
        GameInputController.addProcessor(object : AbstractInputProcessor() {

            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.R) {
                    switchTool(RoadTool::class)
                } else if (keycode == Input.Keys.B) {
                    switchTool(BoxPlacerTool::class)
                }
                return false
            }

        })
    }

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