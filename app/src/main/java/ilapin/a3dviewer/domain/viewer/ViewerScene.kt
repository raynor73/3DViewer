package ilapin.a3dviewer.domain.viewer

import ilapin.a3dengine.*
import org.joml.Quaternionf
import org.joml.Vector3f

class ViewerScene {

    private val _directionalLights = ArrayList<DirectionalLightComponent>()
    private val ambientColor = Vector3f()

    private var currentExposedObject: SceneObject? = null

    private val rootObject: SceneObject
    val camera: SceneObject

    val directionalLights: List<DirectionalLightComponent> = _directionalLights

    init {
        ambientColor.set(0.1f, 0.1f, 0.1f)

        rootObject = SceneObject()

        val sun = SceneObject()
        val sunDirectionalLightComponent = DirectionalLightComponent(
            Vector3f(1f, 1f, 1f),
            Vector3f(1f, -1f, -1f).normalize()
        )
        sun.addComponent(sunDirectionalLightComponent)
        _directionalLights += sunDirectionalLightComponent
        rootObject.addChild(sun)

        val sky = SceneObject()
        val skyDirectionalLightComponent = DirectionalLightComponent(
            Vector3f(0.5f, 0.5f, 0.5f),
            Vector3f(-1f, 0f, 0f)
        )
        sky.addComponent(skyDirectionalLightComponent)
        _directionalLights += skyDirectionalLightComponent
        rootObject.addChild(sky)

        camera = SceneObject()
        camera.addComponent(TransformationComponent(Vector3f(), Quaternionf(), Vector3f(1f, 1f, 1f)))
        camera.addComponent(PerspectiveCameraComponent())
        rootObject.addChild(camera)
    }

    fun exposeMesh(mesh: MeshComponent, rendererComponent: SceneObjectComponent) {
        currentExposedObject?.let { rootObject.removeChild(it) }

        val exposedObject = SceneObject()
        exposedObject.addComponent(rendererComponent)
        exposedObject.addComponent(mesh)
        exposedObject.addComponent(TransformationComponent(Vector3f(0f, 0f, -1f), Quaternionf(), Vector3f(1f, 1f, 1f)))
        exposedObject.addComponent(MaterialComponent(0xffffffff.toInt()))
        rootObject.addChild(exposedObject)
        currentExposedObject = exposedObject
    }

    fun getExposedObject() = currentExposedObject
}