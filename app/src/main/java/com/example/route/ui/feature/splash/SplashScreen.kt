package com.example.route.ui.feature.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.route.R
import com.example.route.ui.theme.RouteTheme

private val SplashGlowColor = Color(0xFF8BB0D8)

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.width * 0.9f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SplashGlowColor.copy(alpha = 0.72f), Color.Transparent),
                    center = Offset(x = size.width / 2f, y = 0f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x = size.width / 2f, y = 0f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SplashGlowColor.copy(alpha = 0.72f), Color.Transparent),
                    center = Offset(x = size.width / 2f, y = size.height),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x = size.width / 2f, y = size.height)
            )
        }

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Route Logo",
            modifier = Modifier.size(width = 220.dp, height = 100.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    RouteTheme {
        SplashScreen()
    }
}
