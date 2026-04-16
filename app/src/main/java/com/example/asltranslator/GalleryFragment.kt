package com.example.asltranslator

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.ui.screens.GalleryScreen
import com.example.asltranslator.ui.theme.ASLTranslatorTheme
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GalleryFragment : Fragment(), GestureRecognizerHelper.GestureRecognizerListener {

    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    
    private var imageView: ImageView? = null
    private var overlayView: OverlayView? = null
    
    private var detectedText by mutableStateOf("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ASLTranslatorTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        GalleryScreen(
                            detectedText = detectedText,
                            onImageViewCreated = { imageView = it },
                            onOverlayViewCreated = { overlayView = it },
                            onPickImage = { pickImageLauncher.launch("image/*") },
                            onNavigateBack = { findNavController().popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gestureRecognizerHelper = GestureRecognizerHelper(requireContext(), this)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, it))
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            }.copy(Bitmap.Config.ARGB_8888, true)

            imageView?.setImageBitmap(bitmap)
            overlayView?.clear()
            detectedText = ""

            // MediaPipe processing
            val mpImage = BitmapImageBuilder(bitmap).build()
            gestureRecognizerHelper.recognizeImage(mpImage)
        }
    }

    override fun onResults(result: GestureRecognizerResult) {
        activity?.runOnUiThread {
            if (imageView != null && overlayView != null) {
                overlayView?.setResults(
                    result,
                    imageView!!.height,
                    imageView!!.width,
                    RunningMode.IMAGE
                )
            }
            val gesture = result.gestures().firstOrNull()?.firstOrNull()
            detectedText = "Image result: ${gesture?.categoryName() ?: "Unrecognized"}"
        }
    }

    override fun onError(error: String) { /* Log error */ }

    override fun onDestroyView() {
        super.onDestroyView()
        imageView = null
        overlayView = null
    }
}