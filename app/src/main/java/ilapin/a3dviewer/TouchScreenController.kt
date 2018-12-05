package ilapin.a3dviewer

import android.renderscript.Matrix4f
import android.util.Log
import ilapin.a3dengine.PerspectiveCameraComponent
import ilapin.a3dengine.SceneObject
import ilapin.a3dengine.TransformationComponent
import org.joml.Vector3f
import java.util.concurrent.LinkedBlockingQueue

class TouchScreenController {

    val queue = LinkedBlockingQueue<TouchEvent>()

    var currentCamera: SceneObject? = null
    var currentExposedObject: SceneObject? = null

    private val invertedViewProjectionMatrix = Matrix4f()

    private val position = Vector3f()
    private val scale = Vector3f()

    private var scaleFactor = 1f

    fun update() {
        while (queue.size != 0) {
            val event = queue.take()
            when (event) {
                is TouchEvent.ScrollEvent -> onScroll(event.normalizedDistanceX, event.normalizedDistanceY)
                is TouchEvent.ScaleEvent -> onScale(event.scaleFactor)
            }
        }
    }

    private fun onScale(newScaleFactor: Float) {
        scaleFactor *= newScaleFactor
        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f))
        Log.d("!@#", "Scale : $scaleFactor")

        val exposedObject = currentExposedObject ?: return
        val exposedObjectTransformation = exposedObject.getComponent(TransformationComponent::class.java) ?: return

        scale.set(scaleFactor, scaleFactor, scaleFactor)
        exposedObjectTransformation.setScale(scale)
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

    sealed class TouchEvent {
        class ScrollEvent(val normalizedDistanceX: Float, val normalizedDistanceY: Float) : TouchEvent()
        class ScaleEvent(val scaleFactor: Float) : TouchEvent()
    }
}