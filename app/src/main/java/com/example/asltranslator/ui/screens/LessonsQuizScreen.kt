package com.example.asltranslator.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asltranslator.QuizViewModel
import com.example.asltranslator.R
import java.io.InputStream

@Composable
fun LessonsQuizScreen(
    viewModel: QuizViewModel,
    onCheckAnswer: (String) -> Unit,
    onNavigateHome: () -> Unit
) {
    val index = viewModel.currentQuestionIndex
    if (index >= viewModel.questions.size) return

    val question = viewModel.questions[index]
    val context = LocalContext.current

    // ensure options are shuffled
    if (viewModel.currentShuffledOptions.isEmpty()) {
        viewModel.currentShuffledOptions = question.options.shuffled()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // hand sign image
            val bitmap = remember(question.imagePath) {
                try {
                    val inputStream: InputStream = context.assets.open(question.imagePath)
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    bmp
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Hand sign image",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(200.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                )
            }

            // progress
            Text(
                text = "Question ${index + 1} of ${viewModel.questions.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Choose the correct option:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            // options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                viewModel.currentShuffledOptions.forEach { option ->
                    Button(
                        onClick = { onCheckAnswer(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(text = option)
                    }
                }
            }
        }

        // home
        FloatingActionButton(
            onClick = onNavigateHome,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home_filled),
                contentDescription = "Home"
            )
        }
    }
}
