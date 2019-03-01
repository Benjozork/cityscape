package me.benjozork.cityscape.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import ktx.app.clearScreen
import ktx.graphics.use

import me.benjozork.cityscape.game.editor.tool.RoadTool
import me.benjozork.cityscape.game.editor.tool.ToolManager
import me.benjozork.cityscape.game.editor.ui.EditorUIView
import me.benjozork.cityscape.game.input.GameInputController

import me.benjozork.cityscape.render.CameraController
import me.benjozork.cityscape.render.RenderingContext
import me.benjozork.cityscape.render.Screen
import me.benjozork.cityscape.storage.MapPackage

import me.benjozork.cityscape.ui.UIView

import java.io.File

class GameScreen(val mapPackage: MapPackage) : Screen() {

    private val image = Texture("ktx-logo.png")
    private val batch = SpriteBatch()

    /**
     * Provides an [UIView] casted to [EditorUIView] for convenience
     */
    val ui get() = (uiView as EditorUIView)

    private lateinit var cameraController: CameraController

    override fun show() {
        super.show()

        this.cameraController = CameraController()
        this.uiView = EditorUIView()

        Gdx.input.inputProcessor = GameInputController
        GameInputController.addProcessor(cameraController.inputProcessor)

        GameWorld.registerObjects(mapPackage.readObjects())

        ToolManager.switchTool(RoadTool::class)

        //val ctx = CSFFileReader().readFrom(File("C:\\Users\\benjo\\Documents\\reddit\\test.dat"))
        //GameWorld.registerObjects(ctx.deserializeNextList())
    }

    override fun render(delta: Float) {
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

        ToolManager.currentTool?.draw()

        super.render(delta)
    }

    override fun dispose() {
        super.dispose()

        /*val file = File("C:\\Users\\benjo\\Documents\\reddit\\test.dat")

        val csfFileWriter = CSFFileWriter()
        csfFileWriter.addClassToMap(Object::class, Road::class, TestEntity::class)
        csfFileWriter.writeTo(dest = file, data = GameWorld.objects.serialize())*/

        image.dispose()
        batch.dispose()
    }

}