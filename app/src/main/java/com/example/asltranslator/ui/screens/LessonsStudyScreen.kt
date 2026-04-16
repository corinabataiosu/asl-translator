package com.example.asltranslator.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.asltranslator.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LessonsStudyScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 80.dp) // space for FAB
        ) {
            // webview for youtube
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                        }
                        webChromeClient = WebChromeClient()
                        loadUrl("https://www.youtube.com/embed/cGavOVNDj1s?start=46")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 32.dp)
            )

            Text(
                text = "ASL learning Guide",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Watch the video to learn more about ASL alphabet.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Divider(
                modifier = Modifier.padding(bottom = 32.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Text(
                text = "Do you think you're ready?",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedButton(
                onClick = onNavigateToQuiz,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text("Take quiz", fontSize = 18.sp)
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
                .padding(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home_filled),
                contentDescription = "Home"
            )
        }
    }
}
