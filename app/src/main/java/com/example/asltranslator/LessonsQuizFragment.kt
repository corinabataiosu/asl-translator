package com.example.asltranslator

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.databinding.FragmentLessonsQuizBinding
import org.json.JSONArray
import java.io.InputStream

data class Question(val imagePath: String, val answer: String, val options: List<String>)

class QuizViewModel : ViewModel() {
    val questions = mutableListOf<Question>()
    var currentQuestionIndex = 0
    var score = 0
    var isLoaded = false
    var currentShuffledOptions = listOf<String>()
}

class LessonsQuizFragment : Fragment() {

    private var _binding: FragmentLessonsQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!viewModel.isLoaded) {
            loadQuestionsFromJson()
            viewModel.isLoaded = true
        }

        binding.btnHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        val buttons = listOf(
            binding.btnOption1,
            binding.btnOption2,
            binding.btnOption3,
            binding.btnOption4
        )

        buttons.forEach { button ->
            button.setOnClickListener { v ->
                val selectedAnswer = (v as Button).text.toString()
                checkAnswer(selectedAnswer)
            }
        }

        if (viewModel.questions.isNotEmpty()) {
            displayQuestion()
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

    private fun displayQuestion() {
        if (viewModel.currentQuestionIndex < viewModel.questions.size) {
            val question = viewModel.questions[viewModel.currentQuestionIndex]

            binding.tvQuizProgress.text = "Question ${viewModel.currentQuestionIndex + 1} of ${viewModel.questions.size}"

            try {
                val inputStream: InputStream = requireContext().assets.open(question.imagePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.ivHandSign.setImageBitmap(bitmap)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val buttons = listOf(
                binding.btnOption1,
                binding.btnOption2,
                binding.btnOption3,
                binding.btnOption4
            )

            if (viewModel.currentShuffledOptions.isEmpty()) {
                viewModel.currentShuffledOptions = question.options.shuffled()
            }

            for (i in buttons.indices) {
                if (i < viewModel.currentShuffledOptions.size) {
                    buttons[i].text = viewModel.currentShuffledOptions[i]
                    buttons[i].visibility = View.VISIBLE
                } else {
                    buttons[i].visibility = View.GONE
                }
            }
        } else {
            showResultDialog()
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
        }
        
        displayQuestion()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
