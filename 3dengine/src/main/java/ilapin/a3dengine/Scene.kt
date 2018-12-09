package ilapin.a3dengine

open class Scene {

    var rootObject: SceneObject? = null

    fun update() {
        rootObject?.update()
    }
}