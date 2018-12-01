package ilapin.a3dviewer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.a3dengine.MaterialComponent
import ilapin.a3dengine.MeshComponent
import ilapin.a3dengine.Scene
import ilapin.a3dengine.SceneObject
import ilapin.a3dviewer.renderer.MeshRendererComponent
import ilapin.a3dviewer.renderer.Shader
import org.joml.Vector3f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer : GLSurfaceView.Renderer {

    private val scene = Scene()
    private val meshRenderers = ArrayList<MeshRendererComponent>()

    private val vertexShaderCode = """
        attribute vec4 positionAttribute;
        void main() {
            gl_Position = positionAttribute;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 colorUniform;
        void main() {
            gl_FragColor = colorUniform;
        }
    """.trimIndent()

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        scene.update()
        meshRenderers.forEach { it.render() }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val triangle = SceneObject()
        triangle.addComponent(
            MeshComponent(
                listOf(
                    Vector3f(0f, 0.5f, 0f),
                    Vector3f(-0.5f, -0.5f, 0f),
                    Vector3f(0.5f, -0.5f, 0f)
                ),
                listOf(0, 1, 2)
            )
        )
        val meshRenderer = MeshRendererComponent(Shader(vertexShaderCode, fragmentShaderCode))
        meshRenderers += meshRenderer
        triangle.addComponent(meshRenderer)
        triangle.addComponent(MaterialComponent(0x008000ff))
        scene.rootObject = triangle
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f)
    }
}