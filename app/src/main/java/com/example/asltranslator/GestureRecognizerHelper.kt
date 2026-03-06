package com.example.asltranslator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GestureRecognizerHelper(
    val context: Context,
    val listener: GestureRecognizerListener,
    // default livestream mode
    var runningMode: RunningMode = RunningMode.LIVE_STREAM
) {
    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    // method called before the processing begins
    fun setupGestureRecognizer() {
        // close old instance if exists
        gestureRecognizer?.close()

        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(Delegate.CPU)
            .setModelAssetPath("gesture_recognizer.task")

        try {
            val optionsBuilder = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setRunningMode(runningMode)

            // asynchronous listeners for livestream mode
            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder.setResultListener(this::returnLivestreamResult)
                optionsBuilder.setErrorListener(this::returnLivestreamError)
            }

            gestureRecognizer = GestureRecognizer.createFromOptions(context, optionsBuilder.build())
        } catch (e: Exception) {
            listener.onError("Initialization error: ${e.message}")
        }
    }

    // method for recognizing signs from images in gallery
    fun recognizeImage(mpImage: MPImage) {
        // choose correct mode
        if (runningMode != RunningMode.IMAGE) {
            runningMode = RunningMode.IMAGE
            setupGestureRecognizer()
        }

        try {
            val result = gestureRecognizer?.recognize(mpImage)
            if (result != null) {
                listener.onResults(result)
            }
        } catch (e: Exception) {
            listener.onError("Error processing image: ${e.message}")
        }
    }

    // method for recognizing signs from real life stream
    fun recognizeLiveStream(imageProxy: ImageProxy) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            runningMode = RunningMode.LIVE_STREAM
            setupGestureRecognizer()
        }

        val frameTime = SystemClock.uptimeMillis()

        // extract imageproxy data in a bitmap
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )

        imageProxy.use {
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        }

        // compute rotation and mirroring
        val matrix = Matrix().apply {
            // rotate image according to the orietntation of the camera sensor
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // mirroring image
            postScale(
                -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
            )
        }

        // create rotated bitmap
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        // transform to mpimage and send to recognize logic
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        recognizeAsync(mpImage, frameTime)
    }

    @VisibleForTesting
    fun recognizeAsync(mpImage: MPImage, frameTime: Long) {
        // we're using running mode livestream so the recognition result will be returned in returnLivestreamResult function
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(result: GestureRecognizerResult, input: MPImage) {
        listener.onResults(result)
    }

    private fun returnLivestreamError(error: RuntimeException) {
        listener.onError(error.message ?: "An unknown error occurred")
    }

    interface GestureRecognizerListener {
        fun onResults(result: GestureRecognizerResult)
        fun onError(error: String)
    }
}