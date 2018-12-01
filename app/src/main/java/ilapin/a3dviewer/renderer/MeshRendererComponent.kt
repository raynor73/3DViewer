package ilapin.a3dviewer.renderer

import android.opengl.GLES20
import ilapin.a3dengine.CameraComponent
import ilapin.a3dengine.MaterialComponent
import ilapin.a3dengine.MeshComponent
import ilapin.a3dengine.SceneObjectComponent
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MeshRendererComponent(
    private val shader: Shader,
    private val cameraProvider: () -> CameraComponent?
) : SceneObjectComponent() {

    companion object {

        private const val COORDINATES_PER_VERTEX = 3
    }

    private var cachedVertexBuffer: Buffer? = null
    private var cachedIndexBuffer: Buffer? = null
    private var cachedNumberOfIndices: Int? = null

    private val colorFloatArray = FloatArray(4)
    private val mvpMatrixFloatArray = FloatArray(16)

    fun render() {
        val material = sceneObject?.getComponent(MaterialComponent::class.java) ?: return
        val mvpMatrix = cameraProvider.invoke()?.getViewProjectionMatrix() ?: return

        if (cachedVertexBuffer == null) {
            val mesh = sceneObject?.getComponent(MeshComponent::class.java) ?: return
            val numberOfVertices = mesh.vertices.size
            val numberOfIndices = mesh.indices.size

            val verticesFloatArray = FloatArray(numberOfVertices * COORDINATES_PER_VERTEX)
            for (i in 0 until numberOfVertices) {
                val vertex = mesh.vertices[i]
                verticesFloatArray[i * COORDINATES_PER_VERTEX] = vertex.x
                verticesFloatArray[i * COORDINATES_PER_VERTEX + 1] = vertex.y
                verticesFloatArray[i * COORDINATES_PER_VERTEX + 2] = vertex.z
            }
            cachedVertexBuffer = ByteBuffer.allocateDirect(verticesFloatArray.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(verticesFloatArray)
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
        val indexBuffer = cachedIndexBuffer ?: return
        val numberOfIndices = cachedNumberOfIndices ?: return

        GLES20.glUseProgram(shader.program)

        // get handle to vertex shader's vPosition member
        GLES20.glGetAttribLocation(shader.program, "positionAttribute").also { positionHandle ->
            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(positionHandle)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                positionHandle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            GLES20.glGetUniformLocation(shader.program, "colorUniform").also { colorHandle ->
                // Set color for drawing the triangle
                colorFloatArray[0] = (material.color ushr 24) / 255f
                colorFloatArray[1] = ((material.color ushr 16) and 0xff) / 255f
                colorFloatArray[2] = ((material.color ushr 8) and 0xff) / 255f
                colorFloatArray[3] = (material.color and 0xff) / 255f
                GLES20.glUniform4fv(colorHandle, 1, colorFloatArray, 0)
            }

            GLES20.glGetUniformLocation(shader.program, "mvpMatrixUniform").also { mvpMatrixHandle ->
                mvpMatrix.get(mvpMatrixFloatArray)
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrixFloatArray, 0)
            }

            // Draw the triangle
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle)
        }
    }
}