package com.example.asltranslator.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.asltranslator.OverlayView
import com.example.asltranslator.R

@Composable
fun CameraScreen(
    detectedText: String,
    onPreviewViewCreated: (PreviewView) -> Unit,
    onOverlayViewCreated: (OverlayView) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    onPreviewViewCreated(this)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        AndroidView(
            factory = { ctx ->
                OverlayView(ctx, null).apply {
                    onOverlayViewCreated(this)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // text output
        if (detectedText.isNotEmpty()) {
            Text(
                text = detectedText,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .background(Color(0x80000000))
                    .padding(16.dp)
            )
        }

        // home button
        FloatingActionButton(
            onClick = onNavigateBack,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home_filled),
                contentDescription = "Home"
            )
        }
    }
}
