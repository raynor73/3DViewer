package ilapin.a3dviewer.renderer

class SimpleShader(
    vertexShaderCode: String,
    fragmentShaderCode: String
) : Shader(vertexShaderCode, fragmentShaderCode) {

    override fun accept(visitor: UniformFillingVisitor) {
        visitor.visitShader(this)
    }
}