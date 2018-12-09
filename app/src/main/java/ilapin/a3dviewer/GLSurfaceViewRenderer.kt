package ilapin.a3dviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.owens.oobjloader.parser.BuilderInterface
import com.owens.oobjloader.parser.Parse
import ilapin.a3dengine.*
import ilapin.a3dviewer.renderer.*
import org.joml.Quaternionf
import org.joml.Vector3f
import java.nio.charset.Charset
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val scene = Scene()
    private val uniformFillingVisitor = UniformFillingVisitor()
    private val meshRenderers = ArrayList<MeshRendererComponent>()
    private val directionalLights = ArrayList<DirectionalLightComponent>()
    private val camera = PerspectiveCameraComponent()

    private val ambientColor = Vector3f()

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
    """.trimIndent()

    private var ambientShader: Shader? = null
    private var directionalLightShader: Shader? = null

    val controller = TouchScreenController()

    override fun onDrawFrame(gl: GL10) {
        controller.update()
        scene.update()

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        meshRenderers.forEach {
            it.currentShader = ambientShader
            it.render()
        }

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glDepthMask(false)
        GLES20.glDepthFunc(GLES20.GL_EQUAL)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        for (directionalLight in directionalLights) {
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

        camera.config = PerspectiveCameraComponent.Config(90f, width.toFloat() / height.toFloat())
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        GLES20.glFrontFace(GLES20.GL_CW)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        ambientColor.set(0.1f, 0.1f, 0.1f)
        uniformFillingVisitor.currentAmbientColor = ambientColor

        val rootObject = SceneObject()

        val sun = SceneObject()
        val sunDirectionalLightComponent = DirectionalLightComponent(Vector3f(0f, 0f, 1f), Vector3f(-1f, 0f, 0f))
        sun.addComponent(sunDirectionalLightComponent)
        directionalLights += sunDirectionalLightComponent
        rootObject.addChild(sun)

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
                listOf(
                    Vector3f(0f, 0f, -1f),
                    Vector3f(0f, 0f, -1f),
                    Vector3f(0f, 0f, -1f)
                ),
                listOf(0, 1, 2)
            )
        )
        triangleObject.addComponent(TransformationComponent(Vector3f(0f, 0f, 1f), Quaternionf(), Vector3f(1f, 1f, 1f)))
        val meshRenderer = MeshRendererComponent(uniformFillingVisitor) { camera }
        meshRenderers += meshRenderer
        triangleObject.addComponent(meshRenderer)
        triangleObject.addComponent(MaterialComponent(0xffffffff.toInt()))
        rootObject.addChild(triangleObject)

        scene.rootObject = rootObject

        controller.currentCamera = cameraObject
        controller.currentExposedObject = triangleObject

        shader = SimpleShader(vertexShaderCode, fragmentShaderCode)

        ambientShader = AmbientShader(
            context.assets.open("vertexShader.glsl").readBytes().toString(Charset.defaultCharset()),
            context.assets.open("ambientFragmentShader.glsl").readBytes().toString(Charset.defaultCharset())
        )

        directionalLightShader = DirectionalLightShader(
            context.assets.open("vertexShader.glsl").readBytes().toString(Charset.defaultCharset()),
            context.assets.open("directionalFragmentShader.glsl").readBytes().toString(Charset.defaultCharset())
        )

        val objLoader = Parse(object : BuilderInterface {

            override fun setObjFilename(filename: String?) {
                Log.d("!@#", "setObjFilename")
            }

            override fun addVertexGeometric(x: Float, y: Float, z: Float) {
                Log.d("!@#", "addVertexGeometric")
            }

            override fun addVertexTexture(u: Float, v: Float) {
                Log.d("!@#", "addVertexTexture")
            }

            override fun addVertexNormal(x: Float, y: Float, z: Float) {
                Log.d("!@#", "addVertexNormal")
            }

            override fun addPoints(values: IntArray?) {
                Log.d("!@#", "addPoints")
            }

            override fun addLine(values: IntArray?) {
                Log.d("!@#", "addLine")
            }

            override fun addFace(vertexIndices: IntArray?) {
                Log.d("!@#", "addFace")
            }

            override fun addObjectName(name: String?) {
                Log.d("!@#", "addObjectName")
            }

            override fun addMapLib(names: Array<out String>?) {
                Log.d("!@#", "addMapLib")
            }

            override fun setCurrentGroupNames(names: Array<out String>?) {
                Log.d("!@#", "setCurrentGroupNames")
            }

            override fun setCurrentSmoothingGroup(groupNumber: Int) {
                Log.d("!@#", "setCurrentSmoothingGroup")
            }

            override fun setCurrentUseMap(name: String?) {
                Log.d("!@#", "setCurrentUseMap")
            }

            override fun setCurrentUseMaterial(name: String?) {
                Log.d("!@#", "setCurrentUseMaterial")
            }

            override fun newMtl(name: String?) {
                Log.d("!@#", "newMtl")
            }

            override fun setXYZ(type: Int, x: Float, y: Float, z: Float) {
                Log.d("!@#", "setXYZ")
            }

            override fun setRGB(type: Int, r: Float, g: Float, b: Float) {
                Log.d("!@#", "setRGB")
            }

            override fun setIllum(illumModel: Int) {
                Log.d("!@#", "setIllum")
            }

            override fun setD(halo: Boolean, factor: Float) {
                Log.d("!@#", "setD")
            }

            override fun setNs(exponent: Float) {
                Log.d("!@#", "setNs")
            }

            override fun setSharpness(value: Float) {
                Log.d("!@#", "setSharpness")
            }

            override fun setNi(opticalDensity: Float) {
                Log.d("!@#", "setNi")
            }

            override fun setMapDecalDispBump(type: Int, filename: String?) {
                Log.d("!@#", "setMapDecalDispBump")
            }

            override fun setRefl(type: Int, filename: String?) {
                Log.d("!@#", "setRefl")
            }

            override fun doneParsingMaterial() {
                Log.d("!@#", "doneParsingMaterial")
            }

            override fun doneParsingObj(filename: String?) {
                Log.d("!@#", "doneParsingObj")
            }

        }, context.assets.open("monkey_head.obj"), "Monkey Head")
    }
}