package ilapin.a3dviewer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.a3dengine.Scene
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer : GLSurfaceView.Renderer {

    private val scene = Scene()

    init {

    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0.5f, 0f, 1f)
    }
}