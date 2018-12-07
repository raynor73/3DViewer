package ilapin.a3dviewer.renderer

import android.content.Context

class AmbientShader(context: Context) : Shader(
    context.assets.open("ambientVertexShader.glsl").readBytes().toString(),
    context.assets.open("ambientFragmentShader.glsl").readBytes().toString()
)