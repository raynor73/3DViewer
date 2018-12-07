package ilapin.a3dviewer.renderer

import android.opengl.GLES20
import ilapin.a3dengine.MaterialComponent
import org.joml.Vector3fc

class UniformFillingVisitor {

    var currentMaterial: MaterialComponent? = null
    var currentAmbientColor: Vector3fc? = null

    private val colorFloatArray = FloatArray(4)

    fun visitAmbientShader(shader: Shader) {
        val material = currentMaterial ?: return
        val ambientColor = currentAmbientColor ?: return

        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(shader.program, "diffuseColorUniform").also { colorHandle ->
            // Set color for drawing the triangle
            colorFloatArray[0] = (material.diffuseColorUniform ushr 24) / 255f
            colorFloatArray[1] = ((material.diffuseColorUniform ushr 16) and 0xff) / 255f
            colorFloatArray[2] = ((material.diffuseColorUniform ushr 8) and 0xff) / 255f
            colorFloatArray[3] = (material.diffuseColorUniform and 0xff) / 255f
            GLES20.glUniform4fv(colorHandle, 1, colorFloatArray, 0)
        }

        GLES20.glGetUniformLocation(shader.program, "ambientColorUniform").also { colorHandle ->
            colorFloatArray[0] = ambientColor.x()
            colorFloatArray[1] = ambientColor.y()
            colorFloatArray[2] = ambientColor.z()
            GLES20.glUniform3fv(colorHandle, 1, colorFloatArray, 0)
        }
    }

    fun visitShader(shader: Shader) {
        val material = currentMaterial ?: return

        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(shader.program, "diffuseColorUniform").also { colorHandle ->
            // Set color for drawing the triangle
            colorFloatArray[0] = (material.diffuseColorUniform ushr 24) / 255f
            colorFloatArray[1] = ((material.diffuseColorUniform ushr 16) and 0xff) / 255f
            colorFloatArray[2] = ((material.diffuseColorUniform ushr 8) and 0xff) / 255f
            colorFloatArray[3] = (material.diffuseColorUniform and 0xff) / 255f
            GLES20.glUniform4fv(colorHandle, 1, colorFloatArray, 0)
        }
    }
}