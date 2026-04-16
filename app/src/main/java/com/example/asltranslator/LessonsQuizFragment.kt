package com.example.asltranslator

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.ui.screens.LessonsQuizScreen
import com.example.asltranslator.ui.theme.ASLTranslatorTheme
import org.json.JSONArray

data class Question(val imagePath: String, val answer: String, val options: List<String>)

class QuizViewModel : ViewModel() {
    val questions = mutableListOf<Question>()
    var currentQuestionIndex by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)
    var isLoaded = false
    var currentShuffledOptions by mutableStateOf<List<String>>(emptyList())
}

class LessonsQuizFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()

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
                        if (viewModel.currentQuestionIndex < viewModel.questions.size) {
                            LessonsQuizScreen(
                                viewModel = viewModel,
                                onCheckAnswer = { checkAnswer(it) },
                                onNavigateHome = { findNavController().popBackStack(R.id.homeFragment, false) }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!viewModel.isLoaded) {
            loadQuestionsFromJson()
            viewModel.isLoaded = true
        }
    }

    private fun loadQuestionsFromJson() {
        try {
            val jsonStream = requireContext().assets.open("questions.json")
            val size = jsonStream.available()
            val buffer = ByteArray(size)
            jsonStream.read(buffer)
            jsonStream.close()

            val jsonString = String(buffer, Charsets.UTF_8)
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val image = item.getString("image")
                val answer = item.getString("answer")
                val optionsArray = item.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                viewModel.questions.add(Question(image, answer, options))
            }
            viewModel.questions.shuffle()
            
            if (viewModel.questions.isNotEmpty()) {
                viewModel.currentShuffledOptions = viewModel.questions[0].options.shuffled()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]
        if (selectedAnswer == currentQuestion.answer) {
            viewModel.score++
        }
        
        viewModel.currentQuestionIndex++
        
        if (viewModel.currentQuestionIndex < viewModel.questions.size) {
            viewModel.currentShuffledOptions = viewModel.questions[viewModel.currentQuestionIndex].options.shuffled()
        } else {
            viewModel.currentShuffledOptions = emptyList()
            showResultDialog()
        }
    }

    private fun showResultDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("The quiz is over!")
            .setMessage("You have achieved a score of: ${viewModel.score} / ${viewModel.questions.size}")
            .setPositiveButton("OK") { _, _ ->
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .setCancelable(false)
            .show()
    }
}
