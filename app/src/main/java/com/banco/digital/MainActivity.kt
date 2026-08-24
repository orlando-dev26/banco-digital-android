package com.banco.digital

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.banco.digital.ui.screens.LoginScreen
import com.banco.digital.ui.screens.MainContainerScreen
import com.banco.digital.ui.screens.RegisterScreen
import com.banco.digital.ui.theme.DigitalBankAppTheme

// IMPORTANTE: Cambiamos ComponentActivity a FragmentActivity para la biometría
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalBankAppTheme {
                var currentScreen by remember { mutableStateOf("Login") }

                when (currentScreen) {
                    "Login" -> {
                        LoginScreen(
                            onNavigateToRegister = { currentScreen = "Register" },
                            onLoginSuccess = { currentScreen = "MainApp" },
                            // Pasamos la función que invoca al sensor
                            onBiometricClick = {
                                authenticateWithBiometrics(
                                    onSuccess = { currentScreen = "MainApp" }
                                )
                            }
                        )
                    }
                    "Register" -> {
                        RegisterScreen(
                            onNavigateBack = { currentScreen = "Login" },
                            onRegisterSuccess = { currentScreen = "MainApp" }
                        )
                    }
                    "MainApp" -> {
                        MainContainerScreen()
                    }
                }
            }
        }
    }

    // Función que invoca el hardware de Face ID / Huella
    private fun authenticateWithBiometrics(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Identidad verificada", Toast.LENGTH_SHORT).show()
                    onSuccess() // Si la huella es correcta, entra a la app
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Fallo al verificar", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Desbloqueo de Banca Digital")
            .setSubtitle("Usa tu Face ID o Huella Digital para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}