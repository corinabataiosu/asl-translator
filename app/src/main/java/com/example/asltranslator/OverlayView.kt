package com.example.asltranslator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: GestureRecognizerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        // line design
        linePaint.color = Color.parseColor("#F29285")
        linePaint.strokeWidth = 8f
        linePaint.style = Paint.Style.STROKE

        // point design
        pointPaint.color = Color.WHITE
        pointPaint.strokeWidth = 12f
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { gestureResult ->
            for (landmarks in gestureResult.landmarks()) {

                // points
                for (normalizedLandmark in landmarks) {
                    // mirroring
                    val x = (1 - normalizedLandmark.x()) * width
                    val y = normalizedLandmark.y() * height
                    canvas.drawPoint(x, y, pointPaint)
                }

                // lines
                HandLandmarker.HAND_CONNECTIONS.forEach {
                    val start = landmarks.get(it!!.start())
                    val end = landmarks.get(it.end())

                    canvas.drawLine(
                        width - (start.x() * width),
                        start.y() * height,
                        width - (end.x() * width),
                        end.y() * height,
                        linePaint
                    )
                }
            }
        }
    }

    // function called in MainActivity when receiving new results
    fun setResults(
        gestureRecognizerResult: GestureRecognizerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = gestureRecognizerResult

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }
}