package me.benjozork.cityscape.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import com.strongjoshua.console.GUIConsole

import ktx.app.clearScreen
import ktx.graphics.use

import me.benjozork.cityscape.CityscapeCommandExecutor

import me.benjozork.cityscape.game.`object`.Road
import me.benjozork.cityscape.game.`object`.TestEntity
import me.benjozork.cityscape.game.`object`.model.Object
import me.benjozork.cityscape.game.editor.tool.RoadTool
import me.benjozork.cityscape.game.editor.tool.ToolManager
import me.benjozork.cityscape.game.editor.ui.EditorUIView
import me.benjozork.cityscape.game.input.GameInputController

import me.benjozork.cityscape.render.CameraController
import me.benjozork.cityscape.render.RenderingContext
import me.benjozork.cityscape.render.Screen

import me.benjozork.cityscape.storage.CSFFileReader
import me.benjozork.cityscape.storage.CSFFileWriter
import me.benjozork.cityscape.storage.deserializeNextList
import me.benjozork.cityscape.storage.serialize

import java.io.File

object GameScreen : Screen() {

    private val image = Texture("ktx-logo.png")
    private val batch = SpriteBatch()

    private val console = GUIConsole().apply { this.displayKeyID = Input.Keys.ESCAPE }

    private lateinit var cameraController: CameraController
    private lateinit var uiController: EditorUIView

    override fun show() {
        super.show()

        RenderingContext.intialize()

        this.cameraController = CameraController()
        this.uiController = EditorUIView()

        Gdx.input.inputProcessor = GameInputController
        GameInputController.addProcessor(cameraController.inputProcessor)

        ToolManager.switchTool(RoadTool::class)

        val ctx = CSFFileReader().readFrom(File("C:\\Users\\benjo\\Documents\\reddit\\test.dat"))
        GameWorld.registerObjects(ctx.deserializeNextList())
    }

    override fun render(delta: Float) {
        super.render(delta)

        clearScreen(0.8f, 0.8f, 0.8f)

        RenderingContext.update()
        cameraController.update()

        batch.projectionMatrix = RenderingContext.camera!!.combined
        batch.use {
            it.draw(image, 0f, 0f)
        }

        //

        GameWorld.update()
        GameWorld.draw()

        uiController.update()
        uiController.draw()

        ToolManager.currentTool?.draw()

        //

        console.setCommandExecutor(CityscapeCommandExecutor())
        console.draw()
    }

    override fun dispose() {
        super.dispose()

        val file = File("C:\\Users\\benjo\\Documents\\reddit\\test.dat")

        val csfFileWriter = CSFFileWriter()
        csfFileWriter.addClassToMap(Object::class, Road::class, TestEntity::class)
        csfFileWriter.writeTo(dest = file, data = GameWorld.objects.serialize())

        image.dispose()
        batch.dispose()
    }

}