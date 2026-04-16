package com.example.asltranslator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.R
import com.example.asltranslator.ui.screens.HomeScreen
import com.example.asltranslator.ui.theme.ASLTranslatorTheme

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ASLTranslatorTheme {
                    HomeScreen(
                        onNavigateToLessons = { findNavController().navigate(R.id.action_homeFragment_to_lessonsStudyFragment) },
                        onNavigateToCamera = { findNavController().navigate(R.id.action_homeFragment_to_cameraFragment) },
                        onNavigateToGallery = { findNavController().navigate(R.id.action_homeFragment_to_galleryFragment) }
                    )
                }
            }
        }
    }
}
