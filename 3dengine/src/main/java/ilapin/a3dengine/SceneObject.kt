package ilapin.a3dengine

class SceneObject {

    private var parent: SceneObject? = null
    private val children = HashSet<SceneObject>()
    private val components = HashSet<SceneObjectComponent>()

    fun addChild(child: SceneObject) {
        children += child
        child.parent = this
    }

    fun removeChild(child: SceneObject) {
        children -= child
        child.parent = null
    }

    fun addComponent(component: SceneObjectComponent) {
        components += component
        component.sceneObject = this
    }

    fun removeComponent(component: SceneObjectComponent) {
        components -= component
        component.sceneObject = null
    }

    fun update() {
        children.forEach { it.update() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : SceneObjectComponent> getComponent(clazz: Class<T>): T? {
        for (component in components) {
            if (component.javaClass == clazz) {
                return component as T
            }
        }

        return null
    }
}