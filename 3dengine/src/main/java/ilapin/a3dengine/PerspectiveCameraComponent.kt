package ilapin.a3dengine

import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f

class PerspectiveCameraComponent: CameraComponent() {

    private val projectionMatrix = Matrix4f()
    private val viewProjectionMatrix = Matrix4f()
    private val lookAtDirection = Vector3f(0f, 0f, -1f)
    private val up = Vector3f(0f, 1f, 0f)

    var config: Config? = null
        set(value) {
            if (value != null) {
                projectionMatrix.identity().perspective(
                    Math.toRadians(value.fieldOfView.toDouble()).toFloat(),
                    value.aspect,
                    0.1f,
                    1000f
                )
            } else {
                projectionMatrix.identity()
            }
            field = value
        }

    init {
        projectionMatrix.identity()
    }

    override fun getViewProjectionMatrix(): Matrix4fc? {
        return if (config != null) {
            val position = sceneObject?.getComponent(TransformationComponent::class.java)?.getPosition() ?: return null
            projectionMatrix.lookAt(
                position.x(), position.y(), position.z(),
                position.x() + lookAtDirection.x, position.y() + lookAtDirection.y, position.z() + lookAtDirection.z,
                up.x, up.y, up.z,
                viewProjectionMatrix
            )
        } else {
            null
        }
    }

    class Config(val fieldOfView: Float, val aspect: Float)
}