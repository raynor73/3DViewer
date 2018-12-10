package ilapin.a3dviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private const val PICK_FILE_REQUEST_CODE = 1
    }

    private lateinit var glView: GLSurfaceView

    private var isFullscreen = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val renderer = GLSurfaceViewRenderer(this)
            val gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {

                override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                    val normalizedDistanceX = -distanceX / glView.width * 2
                    val normalizedDistanceY = distanceY / glView.height * 2
                    renderer.controller.queue.put(
                        TouchScreenController.TouchEvent.ScrollEvent(normalizedDistanceX, normalizedDistanceY)
                    )
                    return true
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    toggleFullscreen()
                    return true
                }
            })
            gestureDetector.setIsLongpressEnabled(false)
            val longPressGestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent?) {
                    renderer.controller.queue.put(TouchScreenController.TouchEvent.LongPressEvent)
                }
            })
            //gestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {})
            val scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    renderer.controller.queue.put(TouchScreenController.TouchEvent.ScaleEvent(detector.scaleFactor))
                    return true
                }
            })
            glView = GLSurfaceView(this)
            glView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                scaleGestureDetector.onTouchEvent(event)
                longPressGestureDetector.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        renderer.controller.queue.put(TouchScreenController.TouchEvent.TerminalEvent)
                    }
                }
                true
            }
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            container.addView(glView, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isFullscreen) {
            hideControls()
        } else {
            showControls()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.pick_file) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("!@#", "data: $data")
        }
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            showControls()
        } else {
            hideControls()
        }
    }

    private fun showControls() {
        isFullscreen = false
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        controls.animate().alpha(1f).start()
    }

    private fun hideControls() {
        isFullscreen = true
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        controls.animate().alpha(0f).start()
    }
}
