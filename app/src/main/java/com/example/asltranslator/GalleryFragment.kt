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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.databinding.FragmentGalleryBinding
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GalleryFragment : Fragment(), GestureRecognizerHelper.GestureRecognizerListener {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gestureRecognizerHelper = GestureRecognizerHelper(requireContext(), this)

        binding.btnHome.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, it))
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            }.copy(Bitmap.Config.ARGB_8888, true)

            binding.imagePreview.setImageBitmap(bitmap)

            // MediaPipe processing
            val mpImage = BitmapImageBuilder(bitmap).build()
            gestureRecognizerHelper.recognizeImage(mpImage)
        }
    }

    override fun onResults(result: GestureRecognizerResult) {
        activity?.runOnUiThread {
            binding.overlay.setResults(result, binding.imagePreview.height, binding.imagePreview.width, com.google.mediapipe.tasks.vision.core.RunningMode.IMAGE)
            val gesture = result.gestures().firstOrNull()?.firstOrNull()
            binding.tvResult.text = "Image result: ${gesture?.categoryName() ?: "Unrecognized"}"
        }
    }

    override fun onError(error: String) { /* Log error */ }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}