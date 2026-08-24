package com.banco.digital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.banco.digital.ui.screens.MainContainerScreen
import com.banco.digital.ui.theme.DigitalBankAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalBankAppTheme {
                // LLAMAMOS DIRECTAMENTE AL CONTENEDOR PRINCIPAL
                // Este ya contiene el Scaffold y la barra de navegación inferior
                MainContainerScreen()
            }
        }
    }
}