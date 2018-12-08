package ilapin.a3dviewer.renderer

import android.opengl.GLES20
import ilapin.a3dengine.DirectionalLightComponent
import ilapin.a3dengine.MaterialComponent
import org.joml.Vector3fc

class UniformFillingVisitor {

    var currentMaterial: MaterialComponent? = null
    var currentDirectionalLight: DirectionalLightComponent? = null
    var currentAmbientColor: Vector3fc? = null

    private val bufferFloatArray = FloatArray(4)

    fun visitAmbientShader(shader: Shader) {
        val material = currentMaterial ?: return
        val ambientColor = currentAmbientColor ?: return

        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(shader.program, "diffuseColorUniform").also { colorHandle ->
            // Set color for drawing the triangle
            bufferFloatArray[0] = (material.diffuseColorUniform ushr 24) / 255f
            bufferFloatArray[1] = ((material.diffuseColorUniform ushr 16) and 0xff) / 255f
            bufferFloatArray[2] = ((material.diffuseColorUniform ushr 8) and 0xff) / 255f
            bufferFloatArray[3] = (material.diffuseColorUniform and 0xff) / 255f
            GLES20.glUniform4fv(colorHandle, 1, bufferFloatArray, 0)
        }

        GLES20.glGetUniformLocation(shader.program, "ambientColorUniform").also { colorHandle ->
            bufferFloatArray[0] = ambientColor.x()
            bufferFloatArray[1] = ambientColor.y()
            bufferFloatArray[2] = ambientColor.z()
            GLES20.glUniform3fv(colorHandle, 1, bufferFloatArray, 0)
        }
    }

    fun visitShader(shader: Shader) {
        val material = currentMaterial ?: return

        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(shader.program, "colorUniform").also { colorHandle ->
            // Set color for drawing the triangle
            bufferFloatArray[0] = (material.diffuseColorUniform ushr 24) / 255f
            bufferFloatArray[1] = ((material.diffuseColorUniform ushr 16) and 0xff) / 255f
            bufferFloatArray[2] = ((material.diffuseColorUniform ushr 8) and 0xff) / 255f
            bufferFloatArray[3] = (material.diffuseColorUniform and 0xff) / 255f
            GLES20.glUniform4fv(colorHandle, 1, bufferFloatArray, 0)
        }
    }

    fun visitDirectionalLightShader(shader: Shader) {
        val material = currentMaterial ?: return
        val directionalLight = currentDirectionalLight ?: return

        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(shader.program, "diffuseColorUniform").also { colorHandle ->
            // Set color for drawing the triangle
            bufferFloatArray[0] = (material.diffuseColorUniform ushr 24) / 255f
            bufferFloatArray[1] = ((material.diffuseColorUniform ushr 16) and 0xff) / 255f
            bufferFloatArray[2] = ((material.diffuseColorUniform ushr 8) and 0xff) / 255f
            bufferFloatArray[3] = (material.diffuseColorUniform and 0xff) / 255f
            GLES20.glUniform4fv(colorHandle, 1, bufferFloatArray, 0)
        }

        GLES20.glGetUniformLocation(shader.program, "directionalLightUniform.color").also { colorHandle ->
            val color = directionalLight.getColor()
            bufferFloatArray[0] = color.x()
            bufferFloatArray[1] = color.y()
            bufferFloatArray[2] = color.z()
            GLES20.glUniform3fv(colorHandle, 1, bufferFloatArray, 0)
        }

        GLES20.glGetUniformLocation(shader.program, "directionalLightUniform.direction").also { colorHandle ->
            val direction = directionalLight.getDirection()
            bufferFloatArray[0] = direction.x()
            bufferFloatArray[1] = direction.y()
            bufferFloatArray[2] = direction.z()
            GLES20.glUniform3fv(colorHandle, 1, bufferFloatArray, 0)
        }
    }
}