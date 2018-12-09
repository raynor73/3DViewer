package ilapin.a3dviewer

import ilapin.a3dengine.SceneObject
import ilapin.a3dengine.TransformationComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.concurrent.LinkedBlockingQueue

class TouchScreenController {

    val queue = LinkedBlockingQueue<TouchEvent>()

    var currentCamera: SceneObject? = null
    var currentExposedObject: SceneObject? = null

    //private val invertedViewProjectionMatrix = Matrix4f()

    private val position = Vector3f()
    private val scale = Vector3f()
    private val rotation = Quaternionf()

    private var scaleFactor = 1f
    private var hasScrollingMode = false

    fun update() {
        while (queue.size != 0) {
            val event = queue.take()
            when (event) {
                is TouchEvent.ScrollEvent -> onScroll(event.normalizedDistanceX, event.normalizedDistanceY)
                is TouchEvent.ScaleEvent -> onScale(event.scaleFactor)
                is TouchEvent.LongPressEvent -> hasScrollingMode = true
                is TouchEvent.TerminalEvent -> hasScrollingMode = false
            }
        }
    }

    private fun onScale(newScaleFactor: Float) {
        val exposedObject = currentExposedObject ?: return
        val exposedObjectTransformation = exposedObject.getComponent(TransformationComponent::class.java) ?: return

        scaleFactor *= newScaleFactor
        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f))

        scale.set(scaleFactor, scaleFactor, scaleFactor)
        exposedObjectTransformation.setScale(scale)
    }

    private fun onScroll(normalizedDistanceX: Float, normalizedDistanceY: Float) {
        if (!hasScrollingMode) {
            val exposedObject = currentExposedObject ?: return
            val exposedObjectTransformation = exposedObject.getComponent(TransformationComponent::class.java) ?: return

            rotation.identity()
            val angleY = (normalizedDistanceX * Math.PI / 2).toFloat()
            val angleX = (-normalizedDistanceY * Math.PI / 2).toFloat()
            rotation.rotateX(angleX)
            rotation.rotateY(angleY)
            rotation.mul(exposedObjectTransformation.getRotation())
            exposedObjectTransformation.setRotation(rotation)
        } else {
            val camera = currentCamera ?: return
            val cameraTransformation = camera.getComponent(TransformationComponent::class.java) ?: return
            //val viewProjectionMatrix = camera.getComponent(PerspectiveCameraComponent::class.java)?.getViewProjectionMatrix() ?: return

            position.set(cameraTransformation.getPosition())
            position.x -= normalizedDistanceX
            position.y -= normalizedDistanceY
            cameraTransformation.setPosition(position)
        }
    }

    sealed class TouchEvent {
        class ScrollEvent(val normalizedDistanceX: Float, val normalizedDistanceY: Float) : TouchEvent()
        class ScaleEvent(val scaleFactor: Float) : TouchEvent()
        object TerminalEvent : TouchEvent()
        object LongPressEvent : TouchEvent()
    }
}