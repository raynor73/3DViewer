package ilapin.a3dviewer

import android.renderscript.Matrix4f
import ilapin.a3dengine.CameraComponent
import ilapin.a3dengine.SceneObject
import ilapin.a3dengine.TransformationComponent

class TouchScreenController(private val camera: SceneObject) {

    private val invertedViewProjectionMatrix = Matrix4f()

    fun onScroll(normalizedDistanceX: Float, normalizedDistanceY: Float) {
        val cameraTransformation = camera.getComponent(TransformationComponent::class.java) ?: return
        val viewProjectionMatrix = camera.getComponent(CameraComponent::class.java)?.getViewProjectionMatrix() ?: return

        //cameraTransformation.
    }
}