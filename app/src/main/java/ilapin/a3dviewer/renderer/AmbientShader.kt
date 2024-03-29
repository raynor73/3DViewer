package ilapin.a3dviewer.renderer

class AmbientShader(
    vertexShaderCode: String,
    fragmentShaderCode: String
) : Shader(vertexShaderCode, fragmentShaderCode) {

    override fun accept(visitor: UniformFillingVisitor) {
        visitor.visitAmbientShader(this)
    }
}