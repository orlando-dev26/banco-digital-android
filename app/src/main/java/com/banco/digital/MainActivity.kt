package com.banco.digital

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banco.digital.ui.screens.LoginScreen
import com.banco.digital.ui.screens.MainContainerScreen
import com.banco.digital.ui.screens.RegisterScreen
import com.banco.digital.ui.screens.TransferScreen
import com.banco.digital.ui.theme.DigitalBankAppTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalBankAppTheme {
                // El enrutador oficial de Jetpack Compose
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = { navController.navigate("register") },
                            onLoginSuccess = {
                                // Navegamos a la app y borramos el login del historial
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onBiometricClick = {
                                authenticateWithBiometrics(
                                    onSuccess = {
                                        navController.navigate("main") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onNavigateBack = { navController.popBackStack() }, // Vuelve atrás correctamente
                            onRegisterSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainContainerScreen(
                            onNavigateToTransfer = { navController.navigate("transfer") } // Al hacer clic, navega a transfer
                        )
                    }

                    // --- NUEVA PANTALLA DE TRANSFERENCIA ---
                    composable("transfer") {
                        TransferScreen(
                            onNavigateBack = { navController.popBackStack() }, // El botón regresar funciona
                            onTransferSubmit = { destination, amount, description, idempotencyKey ->
                                // Por ahora solo volvemos atrás.
                                // En el próximo paso crearemos la pantalla de "Verificación / Éxito"
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

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
                    onSuccess()
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