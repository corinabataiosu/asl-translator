package com.example.asltranslator.ui.screens

import android.widget.ImageView
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
fun GalleryScreen(
    detectedText: String,
    onImageViewCreated: (ImageView) -> Unit,
    onOverlayViewCreated: (OverlayView) -> Unit,
    onPickImage: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        OutlinedButton(
            onClick = onPickImage,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Load photo from gallery", fontSize = 18.sp)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        onImageViewCreated(this)
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
        }

        Text(
            text = detectedText,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        FloatingActionButton(
            onClick = onNavigateBack,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home_filled),
                contentDescription = "Home"
            )
        }
    }
}
