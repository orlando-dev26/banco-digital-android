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
import com.banco.digital.ui.screens.BiometricSetupScreen
import com.banco.digital.ui.screens.LoginScreen
import com.banco.digital.ui.screens.MainContainerScreen
import com.banco.digital.ui.screens.RegisterScreen
import com.banco.digital.ui.screens.TransactionDetailScreen
import com.banco.digital.ui.screens.TransferHoldScreen
import com.banco.digital.ui.screens.TransferResultScreen
import com.banco.digital.ui.screens.TransferScreen
import com.banco.digital.ui.screens.VerifySmsScreen
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
                            onNavigateBack = { navController.popBackStack() },
                            onRegisterSuccess = { _, _, _, _, _, _, _, _ ->
                                navController.navigate("biometric_setup")
                            }
                        )
                    }

                    composable("verify_sms") {
                        VerifySmsScreen(
                            onVerifyClick = { email, pin, smsCode ->
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onCancelClick = { navController.popBackStack() }
                        )
                    }

                    composable("biometric_setup") {
                        BiometricSetupScreen(
                            onEnableBiometricsClick = {
                                authenticateWithBiometrics(
                                    onSuccess = {
                                        navController.navigate("main") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            },
                            onSkipClick = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainContainerScreen(
                            onNavigateToTransfer = { navController.navigate("transfer") },
                            onNavigateToTransactionDetail = { navController.navigate("transaction_detail") },
                            onLogoutClick = {
                                navController.navigate("login") {
                                    popUpTo(0) // Limpiar historial
                                }
                            }
                        )
                    }

                    // --- PANTALLA DE TRANSFERENCIA (FORMULARIO) ---
                    composable("transfer") {
                        TransferScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onTransferSubmit = { destination, amount, description, idempotencyKey ->
                                // Aquí saltamos a la pantalla de resultado
                                navController.navigate("transfer_result")
                            }
                        )
                    }

                    // --- PANTALLA DE RESULTADO (VOUCHER) ---
                    composable("transfer_result") {
                        TransferResultScreen(
                            // Cuando le de a "Volver al Inicio", regresamos al contenedor y limpiamos el historial
                            onNavigateHome = {
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }

                    // --- DETALLE DE MOVIMIENTO HISTÓRICO ---
                    composable("transaction_detail") {
                        TransactionDetailScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // --- PANTALLA DE RETENCIÓN DE FRAUDE ---
                    composable("transfer_hold") {
                        TransferHoldScreen(
                            onNavigateHome = {
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
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