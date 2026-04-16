package com.example.asltranslator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.example.asltranslator.ui.theme.ASLTranslatorTheme
import com.example.asltranslator.ui.screens.CameraScreen

class CameraFragment : Fragment(), GestureRecognizerHelper.GestureRecognizerListener {

    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var cameraExecutor: ExecutorService

    private var previewView: PreviewView? = null
    private var overlayView: OverlayView? = null
    
    // Using mutable state to trigger Compose recomposition for detection text
    private var detectedText by mutableStateOf("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ASLTranslatorTheme {
                    CameraScreen(
                        detectedText = detectedText,
                        onPreviewViewCreated = { 
                            previewView = it 
                            setupCameraIfReady()
                        },
                        onOverlayViewCreated = { overlayView = it },
                        onNavigateBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        gestureRecognizerHelper = GestureRecognizerHelper(
            requireContext(),
            this,
            RunningMode.LIVE_STREAM
        )

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupCameraIfReady() {
        if (allPermissionsGranted() && previewView != null) {
            startCamera()
        }
    }

    private fun startCamera() {
        if (previewView == null) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView!!.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        gestureRecognizerHelper.recognizeLiveStream(imageProxy)
                        imageProxy.close()
                    }
                }

            cameraProvider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onResults(result: GestureRecognizerResult) {
        activity?.runOnUiThread {
            if (previewView != null && overlayView != null) {
                overlayView?.setResults(
                    result,
                    previewView!!.height,
                    previewView!!.width,
                    RunningMode.LIVE_STREAM
                )
            }

            val gesture = result.gestures().firstOrNull()?.firstOrNull()
            if (gesture != null && gesture.score() > 0.6f) {
                detectedText = "Detected: ${gesture.categoryName()}"
                sendNotification(gesture.categoryName())
            } else {
                detectedText = ""
            }
        }
    }

    private fun sendNotification(label: String) {
        val builder = NotificationCompat.Builder(requireContext(), "ASL_NOTIF")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New translations")
            .setContentText("Letter detected: $label")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(1, builder.build())
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) startCamera() else Toast.makeText(context, "Refused permission", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        previewView = null
        overlayView = null
    }

    override fun onError(error: String) { /* Log error */ }
}