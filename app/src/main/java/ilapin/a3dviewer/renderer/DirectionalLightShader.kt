package ilapin.a3dviewer.renderer

class DirectionalLightShader(
    vertexShaderCode: String,
    fragmentShaderCode: String
) : Shader(vertexShaderCode, fragmentShaderCode) {

    override fun accept(visitor: UniformFillingVisitor) {
        visitor.visitDirectionalLightShader(this)
    }
}