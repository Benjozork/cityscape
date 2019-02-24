package me.benjozork.cityscape.render

import ktx.app.KtxScreen

import me.benjozork.cityscape.ui.UIView

open class Screen : KtxScreen {

    val uiView: UIView? = null

    override fun resize(width: Int, height: Int) {
        uiView?.resize(width, height)

        if (RenderingContext.initialized) {
            RenderingContext.camera?.viewportWidth  = width.toFloat()
            RenderingContext.camera?.viewportHeight = height.toFloat()

            RenderingContext.uiCamera?.viewportWidth  = width.toFloat()
            RenderingContext.uiCamera?.viewportHeight = height.toFloat()
        }
    }

    override fun render(delta: Float) {
        uiView?.update()
        uiView?.draw()
    }

}