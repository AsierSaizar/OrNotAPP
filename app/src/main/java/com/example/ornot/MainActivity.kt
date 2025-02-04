package com.example.ornot

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opcional: ocultar las barras del sistema para pantalla completa
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            MaterialTheme {
                ImageTapDetector()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageTapDetector() {
    var imageIndex by remember { mutableStateOf(1) }
    val maxImages = 52
    val scope = rememberCoroutineScope()

    var tapCount1 by remember { mutableStateOf(0) }
    var tapCount2 by remember { mutableStateOf(0) }
    var inFirstSequence by remember { mutableStateOf(true) }

    var finalizeJob by remember { mutableStateOf<Job?>(null) }
    val tapThreshold = 1000L // 1 segundo
    val tapThreshold3 = 3500L // 3 segundos

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Cada toque cancela el temporizador anterior
                        finalizeJob?.cancel()

                        if (inFirstSequence) {
                            tapCount1++
                            // Reinicia el temporizador de 1 segundo para la primera secuencia
                            finalizeJob = scope.launch {
                                delay(tapThreshold)
                                // Si no hay toques en 1 segundo, finaliza la primera secuencia
                                inFirstSequence = false
                                // Inicia un temporizador de 3 segundos para esperar la segunda secuencia
                                finalizeJob = scope.launch {
                                    delay(tapThreshold3)
                                    // Si no hay toques en 3 segundos, finaliza la entrada
                                    val finalNumber = tapCount1.coerceIn(1, maxImages)
                                    imageIndex = finalNumber
                                    tapCount1 = 0
                                    tapCount2 = 0
                                    inFirstSequence = true
                                }
                            }
                        } else {
                            tapCount2++
                            // Reinicia el temporizador de 3 segundos para la segunda secuencia
                            finalizeJob = scope.launch {
                                delay(tapThreshold3)
                                // Si no hay toques en 3 segundos, finaliza la segunda secuencia
                                val finalNumber = (tapCount1 * 10 + tapCount2).coerceIn(1, maxImages)
                                imageIndex = finalNumber
                                tapCount1 = 0
                                tapCount2 = 0
                                inFirstSequence = true
                            }
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        // No es necesario hacer nada aquí
                        true
                    }
                    else -> false
                }
            }
    ) {
        FullScreenImage(imageIndex)
    }
}


@Composable
fun FullScreenImage(imageIndex: Int) {
    val context = LocalContext.current
    // Se espera que las imágenes se llamen "background_1", "background_2", …, "background_52"
    val imageResId = context.resources.getIdentifier(
        "background_$imageIndex",
        "drawable",
        context.packageName
    )
    androidx.compose.foundation.Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Full screen background",
        modifier = Modifier.fillMaxSize()
    )
}