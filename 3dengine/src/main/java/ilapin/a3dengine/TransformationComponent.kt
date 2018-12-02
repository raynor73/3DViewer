package ilapin.a3dengine

import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f
import org.joml.Vector3fc

class TransformationComponent(
    private val position: Vector3f,
    private val rotation: Quaternionf,
    private val scale: Vector3f
) : SceneObjectComponent() {

    fun getPosition(): Vector3fc = position

    fun setPosition(position: Vector3fc) {
        this.position.set(position)
    }

    fun getRotation(): Quaternionfc = rotation

    fun getScale(): Vector3fc = scale
}