package ilapin.a3dviewer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.a3dengine.*
import ilapin.a3dviewer.renderer.MeshRendererComponent
import ilapin.a3dviewer.renderer.Shader
import org.joml.Quaternionf
import org.joml.Vector3f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer : GLSurfaceView.Renderer {

    private val scene = Scene()
    private val meshRenderers = ArrayList<MeshRendererComponent>()
    private val camera = PerspectiveCameraComponent()

    private val vertexShaderCode = """
        attribute vec4 positionAttribute;
        uniform mat4 mvpMatrixUniform;
        void main() {
            gl_Position = mvpMatrixUniform * positionAttribute;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 colorUniform;
        void main() {
            gl_FragColor = colorUniform;
        }
    """.trimIndent()

    val controller = TouchScreenController()

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        controller.update()
        scene.update()
        meshRenderers.forEach { it.render() }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        camera.config = PerspectiveCameraComponent.Config(90f, width.toFloat() / height.toFloat())
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f)

        val rootObject = SceneObject()

        val cameraObject = SceneObject()
        cameraObject.addComponent(TransformationComponent(Vector3f(), Quaternionf(), Vector3f(1f, 1f, 1f)))
        cameraObject.addComponent(camera)
        rootObject.addChild(cameraObject)

        val triangleObject = SceneObject()
        triangleObject.addComponent(
            MeshComponent(
                listOf(
                    Vector3f(0f, 0.5f, 0f),
                    Vector3f(-0.5f, -0.5f, 0f),
                    Vector3f(0.5f, -0.5f, 0f)
                ),
                listOf(0, 1, 2)
            )
        )
        triangleObject.addComponent(TransformationComponent(Vector3f(0f, 0f, 1f), Quaternionf(), Vector3f(1f, 1f, 1f)))
        val meshRenderer = MeshRendererComponent(Shader(vertexShaderCode, fragmentShaderCode)) { camera }
        meshRenderers += meshRenderer
        triangleObject.addComponent(meshRenderer)
        triangleObject.addComponent(MaterialComponent(0x008000ff))
        rootObject.addChild(triangleObject)

        scene.rootObject = rootObject

        controller.currentCamera = cameraObject
    }
}