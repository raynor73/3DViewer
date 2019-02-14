package ilapin.a3dviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.a3dengine.MeshComponent
import ilapin.a3dengine.PerspectiveCameraComponent
import ilapin.a3dviewer.domain.viewer.ViewerScene
import ilapin.a3dviewer.renderer.*
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingQueue
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val viewerScene = ViewerScene()
    //private val scene = Scene()
    private val uniformFillingVisitor = UniformFillingVisitor()
    private val meshRenderers = ArrayList<MeshRendererComponent>()
    //private val directionalLights = ArrayList<DirectionalLightComponent>()
    //private val camera = PerspectiveCameraComponent()

    /*private val ambientColor = Vector3f()

    private var shader: Shader? = null

    private val vertexShaderCode = """
        attribute vec3 positionAttribute;
        attribute vec3 normalAttribute;

        uniform mat4 mvpMatrixUniform;

        varying vec3 normal;

        void main() {
            normal = normalAttribute;
            gl_Position = mvpMatrixUniform * vec4(positionAttribute, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 colorUniform;

        varying vec3 normal;

        void main() {
            gl_FragColor = colorUniform + vec4(normal, 1);
        }
    """.trimIndent()*/

    private var ambientShader: Shader? = null
    private var directionalLightShader: Shader? = null

    val controller = TouchScreenController()
    val exposedMeshQueue = LinkedBlockingQueue<MeshComponent>()

    override fun onDrawFrame(gl: GL10) {
        if (exposedMeshQueue.size != 0) {
            meshRenderers.clear()
            val meshRenderer = MeshRendererComponent(uniformFillingVisitor) {
                viewerScene.camera.getComponent(PerspectiveCameraComponent::class.java)
            }
            meshRenderers += meshRenderer
            viewerScene.exposeMesh(exposedMeshQueue.take(), meshRenderer)
            controller.currentExposedObject = viewerScene.getExposedObject()
        }

        controller.update()
        //scene.update()

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        meshRenderers.forEach {
            it.currentShader = ambientShader
            it.render()
        }

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glDepthMask(false)
        GLES20.glDepthFunc(GLES20.GL_EQUAL)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        for (directionalLight in viewerScene.directionalLights) {
            for (renderer in meshRenderers) {
                renderer.currentShader = directionalLightShader
                uniformFillingVisitor.currentDirectionalLight = directionalLight
                renderer.render()
            }
        }

        GLES20.glDepthMask(true)
        GLES20.glDepthFunc(GLES20.GL_LESS)
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        viewerScene
            .camera
            .getComponent(PerspectiveCameraComponent::class.java)
            ?.config = PerspectiveCameraComponent.Config(90f, width.toFloat() / height.toFloat())
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        GLES20.glFrontFace(GLES20.GL_CCW)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        /*ambientColor.set(0.1f, 0.1f, 0.1f)
        uniformFillingVisitor.currentAmbientColor = ambientColor*/

        /*val rootObject = SceneObject()

        val sun = SceneObject()
        val sunDirectionalLightComponent = DirectionalLightComponent(
            Vector3f(1f, 1f, 1f),
            Vector3f(1f, -1f, -1f).normalize()
        )
        sun.addComponent(sunDirectionalLightComponent)
        directionalLights += sunDirectionalLightComponent
        rootObject.addChild(sun)

        val sky = SceneObject()
        val skyDirectionalLightComponent = DirectionalLightComponent(
            Vector3f(0.5f, 0.5f, 0.5f),
            Vector3f(-1f, 0f, 0f)
        )
        sky.addComponent(skyDirectionalLightComponent)
        directionalLights += skyDirectionalLightComponent
        rootObject.addChild(sky)

        val cameraObject = SceneObject()
        cameraObject.addComponent(TransformationComponent(Vector3f(), Quaternionf(), Vector3f(1f, 1f, 1f)))
        cameraObject.addComponent(camera)
        rootObject.addChild(cameraObject)

        val exposedObject = SceneObject()*/
        /*exposedObject.addComponent(
            MeshComponent(
                listOf(
                    Vector3f(-0.5f, 0.5f, 0f),
                    Vector3f(-0.5f, -0.5f, 0f),
                    Vector3f(0.5f, 0f, 0f)
                ),
                listOf(
                    Vector3f(0f, 0f, 1f),
                    Vector3f(0f, 0f, 1f),
                    Vector3f(0f, 0f, 1f)
                ),
                listOf(0, 1, 2)
            )
        )
        exposedObject.addComponent(TransformationComponent(Vector3f(0f, 0f, -1f), Quaternionf(), Vector3f(1f, 1f, 1f)))
        val meshRenderer = MeshRendererComponent(uniformFillingVisitor) { camera }
        meshRenderers += meshRenderer
        exposedObject.addComponent(meshRenderer)
        exposedObject.addComponent(MaterialComponent(0xffffffff.toInt()))
        rootObject.addChild(exposedObject)*/
        /*exposedObject.addComponent(ObjReader.read(context.assets.open("monkey_head.obj")).toMesh())
        exposedObject.addComponent(TransformationComponent(Vector3f(0f, 0f, -1f), Quaternionf(), Vector3f(1f, 1f, 1f)))
        val meshRenderer = MeshRendererComponent(uniformFillingVisitor) { camera }
        meshRenderers += meshRenderer
        exposedObject.addComponent(meshRenderer)
        exposedObject.addComponent(MaterialComponent(0xffffffff.toInt()))
        rootObject.addChild(exposedObject)
        
        scene.rootObject = rootObject

        controller.currentCamera = cameraObject
        controller.currentExposedObject = exposedObject*/

        controller.currentCamera = viewerScene.camera

        //shader = SimpleShader(vertexShaderCode, fragmentShaderCode)

        ambientShader = AmbientShader(
            context.assets.open("vertexShader.glsl").readBytes().toString(Charset.defaultCharset()),
            context.assets.open("ambientFragmentShader.glsl").readBytes().toString(Charset.defaultCharset())
        )

        directionalLightShader = DirectionalLightShader(
            context.assets.open("vertexShader.glsl").readBytes().toString(Charset.defaultCharset()),
            context.assets.open("directionalFragmentShader.glsl").readBytes().toString(Charset.defaultCharset())
        )
    }
}