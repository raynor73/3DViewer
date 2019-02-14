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
    private val uniformFillingVisitor = UniformFillingVisitor()
    private val meshRenderers = ArrayList<MeshRendererComponent>()

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

        controller.currentCamera = viewerScene.camera

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