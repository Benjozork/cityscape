package me.benjozork.cityscape.render

import ktx.app.KtxScreen

import me.benjozork.cityscape.ui.UIView

open class Screen : KtxScreen {

    var uiView: UIView? = null

    override fun resize(width: Int, height: Int) {
        uiView?.resize(width, height)

        if (RenderingContext.initialized) {

            RenderingContext.camera?.apply {
                viewportWidth = width.toFloat()
                viewportHeight = height.toFloat()

                update()
            }
        }
    }

    override fun render(delta: Float) {
        uiView?.update()
        uiView?.draw()
    }

}