package ilapin.a3dengine

class SceneObject {

    private val children = HashSet<SceneObject>()
    private val components = HashSet<SceneObjectComponent>()

    fun addChild(child: SceneObject) {
        children += child
    }

    fun removeChild(child: SceneObject) {
        children -= child
    }

    fun addComponent(component: SceneObjectComponent) {
        components += component
    }

    fun removeComponent(component: SceneObjectComponent) {
        components -= component
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