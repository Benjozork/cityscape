package me.benjozork.cityscape.game.editor.ui

import com.kotcrab.vis.ui.widget.VisTable

import ktx.actors.onClickEvent
import ktx.vis.table

import me.benjozork.cityscape.game.editor.tool.BoxPlacerTool
import me.benjozork.cityscape.game.editor.tool.RoadTool
import me.benjozork.cityscape.game.editor.tool.RoadsidePlacementTestTool
import me.benjozork.cityscape.game.editor.tool.SelectorTool
import me.benjozork.cityscape.game.editor.tool.ToolManager

import me.benjozork.cityscape.ui.UIView

class EditorUIView : UIView() {

    override fun layout(): VisTable {
        return table {
            setFillParent(false)

            width = 300f

            left(); bottom()

            label("Tools ->")

            textButton("road") {
                width = 50f
                height = 15f

                cell(growX = true, spaceRight = 10f)

                onClickEvent { e, a ->
                    ToolManager.switchTool(RoadTool::class)
                }

            }

            textButton("box") {
                width = 50f
                height = 15f

                cell(growX = true, spaceRight = 10f)

                onClickEvent { e, a ->
                    ToolManager.switchTool(BoxPlacerTool::class)
                }

            }

            textButton("selector") {
                width = 50f
                height = 15f

                cell(growX = true, spaceRight = 10f)

                onClickEvent { e, a ->
                    ToolManager.switchTool(SelectorTool::class)
                }

            }

            textButton("roadside") {
                width = 50f
                height = 15f

                cell(growX = true, spaceRight = 10f)

                onClickEvent { e, a ->
                    ToolManager.switchTool(RoadsidePlacementTestTool::class)
                }

            }

        }
    }

}