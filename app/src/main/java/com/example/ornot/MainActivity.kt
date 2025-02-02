package com.example.ornot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.ornot.ui.theme.OrNotTheme

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import kotlinx.coroutines.delay

import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Oculta barras de estado y navegación
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        setContent {
            OrNotTheme {
                FullScreenImage()
            }
        }
    }
}

@Composable
fun FullScreenImage() {
    var imageIndex by remember { mutableStateOf(1) }
    val maxImages = 52

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L) // Espera 5 segundos
            imageIndex = (imageIndex % maxImages) + 1
        }
    }

    FullScreenImage(imageIndex)
}



@Composable
fun FullScreenImage(i: Int) {
    val imageResId = LocalContext.current.resources.getIdentifier(
        "background_$i",
        "drawable",
        LocalContext.current.packageName
    )

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Full screen background",
        modifier = Modifier.fillMaxSize()
    )
}


