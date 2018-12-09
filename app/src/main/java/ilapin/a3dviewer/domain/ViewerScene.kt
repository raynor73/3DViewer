package ilapin.a3dviewer.domain

import ilapin.a3dengine.*
import ilapin.a3dviewer.renderer.MeshRendererComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector3fc

class ViewerScene : Scene() {

    private val _meshRenderers = ArrayList<MeshRendererComponent>()
    private val _directionalLights = ArrayList<DirectionalLightComponent>()
    private val _ambientColor = Vector3f()

    val camera = PerspectiveCameraComponent()
    var exposedObject: SceneObject? = null

    val meshRendsrers: List<MeshRendererComponent> = _meshRenderers
    val directionalLights: List<DirectionalLightComponent> = _directionalLights
    val ambientColor: Vector3fc = _ambientColor

    init {
        _ambientColor.set(0.1f, 0.1f, 0.1f)

        val rootObject = SceneObject()

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

        val cameraObject = SceneObject()
        cameraObject.addComponent(TransformationComponent(Vector3f(), Quaternionf(), Vector3f(1f, 1f, 1f)))
        cameraObject.addComponent(camera)
        rootObject.addChild(cameraObject)

        this.rootObject = rootObject
    }
}