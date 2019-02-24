package me.benjozork.cityscape.game.editor.tool.model

import com.badlogic.gdx.InputAdapter

import me.benjozork.cityscape.game.editor.tool.EditorTool

/**
 * @constructor the parent associated [EditorTool]
 */
open class ToolInputProcessor(val tool: EditorTool) : InputAdapter()