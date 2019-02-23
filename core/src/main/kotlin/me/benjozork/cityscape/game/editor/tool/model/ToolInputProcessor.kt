package me.benjozork.cityscape.game.editor.tool.model

import com.badlogic.gdx.InputProcessor

import me.benjozork.cityscape.game.editor.tool.EditorTool
import me.benjozork.cityscape.game.input.AbstractInputProcessor

/**
 * A special implementation of [InputProcessor] that isn't an interface, therefore
 * not requiring to implement all methods, including ones we don't use.
 *
 * @constructor the parent associated [EditorTool]
 */
open class ToolInputProcessor(val tool: EditorTool) : AbstractInputProcessor()