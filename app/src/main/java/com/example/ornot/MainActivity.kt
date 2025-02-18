package com.example.ornot

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var tapCount1 = 0
    private var tapCount2 = 0
    private var inFirstSequence = true
    private var finalizeJob: Job? = null
    private val tapThreshold = 1000L // 1 segundo para finalizar la primera secuencia
    private val tapThreshold2 = 2000L // 2 segundos para finalizar la segunda secuencia
    private val tapThreshold3 = 2500L // 2.5 segundos para finalizar la entrada
    private val maxImages = 52

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar las barras del sistema para pantalla completa
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            // Cancelar cualquier temporizador anterior
            finalizeJob?.cancel()

            if (inFirstSequence) {
                tapCount1++
                // Iniciar un temporizador para finalizar la primera secuencia
                finalizeJob = lifecycleScope.launch {
                    delay(tapThreshold)
                    inFirstSequence = false
                    // Iniciar otro temporizador para esperar la segunda secuencia
                    finalizeJob = launch {
                        delay(tapThreshold3)
                        val finalNumber = tapCount1.coerceIn(1, maxImages)
                        ImageTapDetector.updateImageIndex(finalNumber)
                        resetTapCounts()
                    }
                }
            } else {
                tapCount2++
                // Iniciar un temporizador para finalizar la segunda secuencia
                finalizeJob = lifecycleScope.launch {
                    delay(tapThreshold2)
                    val finalNumber = (tapCount1 * 10 + tapCount2).coerceIn(1, maxImages)
                    ImageTapDetector.updateImageIndex(finalNumber)
                    resetTapCounts()
                }
            }
            return true // Indicar que el evento fue manejado
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun resetTapCounts() {
        tapCount1 = 0
        tapCount2 = 0
        inFirstSequence = true
    }
}

object ImageTapDetector {
    private val _imageIndex = mutableStateOf(1)
    val imageIndex: State<Int> get() = _imageIndex

    fun updateImageIndex(newIndex: Int) {
        _imageIndex.value = newIndex
    }
}

@Composable
fun ImageTapDetector() {
    val imageIndex by ImageTapDetector.imageIndex

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable() // Necesario para capturar eventos de hardware
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
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Fondo de pantalla completo",
        modifier = Modifier.fillMaxSize()
    )
}
