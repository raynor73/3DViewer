package ilapin.a3dengine

import org.joml.Vector3f
import org.joml.Vector3fc

class DirectionalLightComponent(
    private val color: Vector3f,
    private val direction: Vector3f
) : SceneObjectComponent() {

    fun getColor(): Vector3fc = color

    fun getDirection(): Vector3fc = direction
}