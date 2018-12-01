package ilapin.a3dengine

import org.joml.Matrix4fc

abstract class CameraComponent : SceneObjectComponent() {

    abstract fun getViewProjectionMatrix(): Matrix4fc?
}