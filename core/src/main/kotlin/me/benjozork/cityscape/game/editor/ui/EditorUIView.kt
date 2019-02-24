package me.benjozork.cityscape.game.editor.ui

import com.badlogic.gdx.utils.Align

import com.kotcrab.vis.ui.widget.VisTable

import ktx.actors.onClick
import ktx.actors.onClickEvent
import ktx.vis.table
import ktx.vis.window

import me.benjozork.cityscape.game.`object`.model.Object

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

            width = 600f

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

    /**
     * Opens an object deletion dialog
     *
     * @param items the [MutableCollection] containing the items to delete. this collection will be emptied.
     */
    fun deleteWithConfirmDialog(items: MutableCollection<Object>) {
        window("Delete ${items.size} item(s) ?") {

            isModal = true

            addCloseButton()
            closeOnEscape()
            setCenterOnAdd(true)

            label("Are you sure that you want to delete ${items.size} item(s) ?")
                    .cell(align = Align.center, padBottom = 15f)

            row()

            textButton("Yes").cell(align = Align.center, minWidth = 85f).onClick {
                items.forEach { it.delete() }
                items.clear()
                this.fadeOut()
            }

            width = 350f

            this@EditorUIView.stage.addActor(this)
        }
    }

}