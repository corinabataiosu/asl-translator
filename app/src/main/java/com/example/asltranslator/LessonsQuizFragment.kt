package com.example.asltranslator

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asltranslator.databinding.FragmentLessonsQuizBinding
import org.json.JSONArray
import java.io.InputStream

class LessonsQuizFragment : Fragment() {

    private var _binding: FragmentLessonsQuizBinding? = null
    private val binding get() = _binding!!

    data class Question(val imagePath: String, val answer: String, val options: List<String>)

    private val questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadQuestionsFromJson()

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

        if (questions.isNotEmpty()) {
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
                questions.add(Question(image, answer, options))
            }
            questions.shuffle() // Randomize the order of questions
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]

            binding.tvQuizProgress.text = "Întrebarea ${currentQuestionIndex + 1} din ${questions.size}"

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

            val shuffledOptions = question.options.shuffled()
            for (i in buttons.indices) {
                if (i < shuffledOptions.size) {
                    buttons[i].text = shuffledOptions[i]
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
        val currentQuestion = questions[currentQuestionIndex]
        if (selectedAnswer == currentQuestion.answer) {
            score++
        }
        currentQuestionIndex++
        displayQuestion()
    }

    private fun showResultDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Quiz Terminat")
            .setMessage("Ai obținut scorul: $score / ${questions.size}")
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
