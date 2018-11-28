package ilapin.a3dengine

class Scene {

    var rootObject: SceneObject? = null

    fun update() {
        rootObject?.update()
    }
}