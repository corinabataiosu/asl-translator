package com.example.asltranslator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.ui.theme.ASLTranslatorTheme
import com.example.asltranslator.ui.screens.LessonsStudyScreen

class LessonsStudyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ASLTranslatorTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LessonsStudyScreen(
                            onNavigateToQuiz = { findNavController().navigate(R.id.action_lessonsStudyFragment_to_lessonsQuizFragment) },
                            onNavigateHome = { findNavController().popBackStack(R.id.homeFragment, false) }
                        )
                    }
                }
            }
        }
    }
}
