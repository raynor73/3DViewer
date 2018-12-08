package ilapin.a3dviewer.renderer

import android.opengl.GLES20
import ilapin.a3dengine.*
import org.joml.Matrix4f
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MeshRendererComponent(
    private val uniformFillingVisitor: UniformFillingVisitor,
    private val cameraProvider: () -> CameraComponent?
) : SceneObjectComponent() {

    companion object {

        private const val COORDINATES_PER_POSITION = 3

        private const val COORDINATES_PER_NORMAL = 3

        //private const val COMPONENTS_PER_VERTEX = COORDINATES_PER_POSITION + COORDINATES_PER_NORMAL;
    }

    private var cachedVertexBuffer: Buffer? = null
    private var cachedNormalBuffer: Buffer? = null
    private var cachedIndexBuffer: Buffer? = null
    private var cachedNumberOfIndices: Int? = null

    var currentShader: Shader? = null

    private val mvpMatrix = Matrix4f()
    private val mvpMatrixFloatArray = FloatArray(16)

    fun render() {
        val shader = currentShader ?: return
        val material = sceneObject?.getComponent(MaterialComponent::class.java) ?: return
        val transformation = sceneObject?.getComponent(TransformationComponent::class.java) ?: return
        val viewProjectionMatrix = cameraProvider.invoke()?.getViewProjectionMatrix() ?: return

        if (cachedVertexBuffer == null) {
            val mesh = sceneObject?.getComponent(MeshComponent::class.java) ?: return
            val numberOfVertices = mesh.vertices.size
            val numberOfIndices = mesh.indices.size

            val verticesFloatArray = FloatArray(numberOfVertices * COORDINATES_PER_POSITION)
            val normalsFloatArray = FloatArray(numberOfVertices * COORDINATES_PER_NORMAL)
            for (i in 0 until numberOfVertices) {
                val vertex = mesh.vertices[i]
                verticesFloatArray[i * COORDINATES_PER_POSITION] = vertex.x
                verticesFloatArray[i * COORDINATES_PER_POSITION + 1] = vertex.y
                verticesFloatArray[i * COORDINATES_PER_POSITION + 2] = vertex.z

                val normal = mesh.normals[i]
                normalsFloatArray[i * COORDINATES_PER_NORMAL] = normal.x
                normalsFloatArray[i * COORDINATES_PER_NORMAL + 1] = normal.y
                normalsFloatArray[i * COORDINATES_PER_NORMAL + 2] = normal.z
            }
            cachedVertexBuffer = ByteBuffer.allocateDirect(verticesFloatArray.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(verticesFloatArray)
                    position(0)
                }
            }
            cachedNormalBuffer = ByteBuffer.allocateDirect(normalsFloatArray.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(normalsFloatArray)
                    position(0)
                }
            }

            cachedNumberOfIndices = numberOfIndices

            val indicesShortArray = ShortArray(numberOfIndices)
            for (i in 0 until numberOfVertices) {
                indicesShortArray[i] = mesh.indices[i].toShort()
            }
            cachedIndexBuffer = ByteBuffer.allocateDirect(numberOfIndices * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(indicesShortArray)
                    position(0)
                }
            }
        }
        val vertexBuffer = cachedVertexBuffer ?: return
        val normalBuffer = cachedNormalBuffer ?: return
        val indexBuffer = cachedIndexBuffer ?: return
        val numberOfIndices = cachedNumberOfIndices ?: return

        GLES20.glUseProgram(shader.program)

        val positionHandle = GLES20.glGetAttribLocation(shader.program, "positionAttribute")
        val normalHandle = GLES20.glGetAttribLocation(shader.program, "normalAttribute")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(normalHandle)

        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDINATES_PER_POSITION,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glVertexAttribPointer(
            normalHandle,
            COORDINATES_PER_NORMAL,
            GLES20.GL_FLOAT,
            false,
            0,
            normalBuffer
        )

        uniformFillingVisitor.currentMaterial = material
        shader.accept(uniformFillingVisitor)

        GLES20.glGetUniformLocation(shader.program, "mvpMatrixUniform").also { mvpMatrixHandle ->
            viewProjectionMatrix.get(mvpMatrix)
            mvpMatrix.translate(transformation.getPosition())
            mvpMatrix.scale(transformation.getScale())
            mvpMatrix.rotate(transformation.getRotation())
            mvpMatrix.get(mvpMatrixFloatArray)
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrixFloatArray, 0)
        }

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}