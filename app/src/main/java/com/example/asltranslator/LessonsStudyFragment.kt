package com.example.asltranslator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.databinding.FragmentLessonsStudyBinding

class LessonsStudyFragment : Fragment() {

    private var _binding: FragmentLessonsStudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup YouTube WebView
        val videoUrl = "https://www.youtube.com/embed/cGavOVNDj1s?start=46"
        
        binding.webviewYoutube.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
        }
        binding.webviewYoutube.webChromeClient = WebChromeClient()
        binding.webviewYoutube.loadUrl(videoUrl)

        binding.btnTakeQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_lessonsStudyFragment_to_lessonsQuizFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
