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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.example.asltranslator.databinding.FragmentCameraBinding
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), GestureRecognizerHelper.GestureRecognizerListener {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        gestureRecognizerHelper = GestureRecognizerHelper(
            requireContext(),
            this,
            com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM
        )

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
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
            if (_binding == null) return@runOnUiThread

            // drawing Overlay
            binding.overlay.setResults(result, binding.viewFinder.height, binding.viewFinder.width, com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)

            // notifications logic
            val gesture = result.gestures().firstOrNull()?.firstOrNull()
            if (gesture != null && gesture.score() > 0.6f) {
                binding.tvOutput.text = "Detected: ${gesture.categoryName()}"
                sendNotification(gesture.categoryName())
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
        _binding = null
    }

    override fun onError(error: String) { /* Log error */ }
}