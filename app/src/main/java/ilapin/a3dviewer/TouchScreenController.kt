package ilapin.a3dviewer

import android.renderscript.Matrix4f
import ilapin.a3dengine.CameraComponent
import ilapin.a3dengine.SceneObject
import ilapin.a3dengine.TransformationComponent
import org.joml.Vector3f

class TouchScreenController(private val camera: SceneObject) {

    private val invertedViewProjectionMatrix = Matrix4f()
    private val position = Vector3f()

    fun onScroll(normalizedDistanceX: Float, normalizedDistanceY: Float) {
        val cameraTransformation = camera.getComponent(TransformationComponent::class.java) ?: return
        val viewProjectionMatrix = camera.getComponent(CameraComponent::class.java)?.getViewProjectionMatrix() ?: return

        position.set(cameraTransformation.getPosition())
        position.x -= normalizedDistanceX
        cameraTransformation.setPosition(position)
    }
}