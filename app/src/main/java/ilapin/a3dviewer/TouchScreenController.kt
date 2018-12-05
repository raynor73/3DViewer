package ilapin.a3dviewer

import android.renderscript.Matrix4f
import ilapin.a3dengine.PerspectiveCameraComponent
import ilapin.a3dengine.SceneObject
import ilapin.a3dengine.TransformationComponent
import org.joml.Vector3f
import java.util.concurrent.LinkedBlockingQueue

class TouchScreenController {

    val queue = LinkedBlockingQueue<ScrollEvent>()

    var currentCamera: SceneObject? = null

    private val invertedViewProjectionMatrix = Matrix4f()
    private val position = Vector3f()

    fun update() {
        while (queue.size != 0) {
            val event = queue.take()
            onScroll(event.normalizedDistanceX, event.normalizedDistanceY)
        }
    }

    private fun onScroll(normalizedDistanceX: Float, normalizedDistanceY: Float) {
        val camera = currentCamera ?: return
        val cameraTransformation = camera.getComponent(TransformationComponent::class.java) ?: return
        val viewProjectionMatrix = camera.getComponent(PerspectiveCameraComponent::class.java)?.getViewProjectionMatrix() ?: return

        position.set(cameraTransformation.getPosition())
        position.x -= normalizedDistanceX
        position.y -= normalizedDistanceY
        cameraTransformation.setPosition(position)
    }

    class ScrollEvent(val normalizedDistanceX: Float, val normalizedDistanceY: Float)
}