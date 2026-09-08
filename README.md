# Banco Digital App (Android Nativo)

Aplicacion movil Android de un banco digital con registro biometrico completo (DNI + reconocimiento facial), desarrollada 100% nativa con Kotlin y Jetpack Compose.

## Tecnologias Utilizadas (Versiones Exactas)

Este proyecto fue desarrollado bajo las siguientes especificaciones y versiones exactas para garantizar total compatibilidad:

- Entorno de Desarrollo: Android Studio Ladybug (o superior)
- Lenguaje: Kotlin
- Interfaz Grafica: Jetpack Compose (Material Design 3)
- Java Development Kit (JDK): JavaVersion.VERSION_11 (Java 11)
- SDK de Android: minSdk 26, targetSdk 37
- Navegacion: Jetpack Navigation Compose (v2.7.7)
- Camara: AndroidX CameraX (v1.4.0)
- Machine Learning (Visualizacion UI): Google ML Kit Face Detection (v16.1.6)
- Consumo de API (Red): Retrofit (v2.11.0) con OkHttp Logging Interceptor (v4.12.0)
- Biometria Local: AndroidX Biometric (v1.1.0)
- Estado y Arquitectura: ViewModel Compose (v2.8.7) - Arquitectura MVVM

## Estructura del Proyecto

El codigo fuente de la aplicacion movil se encuentra organizado bajo el patron de arquitectura MVVM y Clean Architecture simplificada. A continuacion, el detalle exacto de cada directorio y componente UI que hemos creado en Android Studio:

`	ext
DigitalBankApp/
|-- app/
|   |-- src/main/java/com/banco/digital/
|   |   |-- MainActivity.kt             <-- Punto de entrada principal de la aplicacion
|   |   |
|   |   |-- data/                       <-- Capa de Datos (Data Layer)
|   |   |   |-- api/
|   |   |   |   |-- KycApiService.kt       <-- Endpoints para servicios de validacion KYC (IA)
|   |   |   |   |-- RegisterApiService.kt  <-- Endpoints para el registro de usuarios
|   |   |   |-- local/
|   |   |   |   |-- DatabaseHelper.kt      <-- Gestor de persistencia local temporal
|   |   |   |-- model/
|   |   |   |   |-- UsuarioRegistro.kt     <-- Modelos de datos (Data Classes)
|   |   |   |-- repository/
|   |   |       |-- RegisterRepository.kt  <-- Patron Repositorio centralizando llamadas API y Local
|   |   |
|   |   |-- ui/                         <-- Capa de Interfaz de Usuario (UI Layer)
|   |   |   |-- components/
|   |   |   |   |-- CameraCapturePreview.kt <-- Componente reutilizable para abrir CameraX
|   |   |   |-- screens/                  <-- Vistas completas de la aplicacion
|   |   |   |   |-- BiometricSetupScreen.kt
|   |   |   |   |-- CardsScreen.kt
|   |   |   |   |-- HomeScreen.kt
|   |   |   |   |-- LoginScreen.kt
|   |   |   |   |-- MainContainerScreen.kt
|   |   |   |   |-- NotificationsScreen.kt
|   |   |   |   |-- ProfileScreen.kt
|   |   |   |   |-- RegisterScreen.kt          <-- Contenedor principal del flujo de registro
|   |   |   |   |-- RegisterStep1PersonalData.kt
|   |   |   |   |-- RegisterStep2Contact.kt
|   |   |   |   |-- RegisterStep2Password.kt
|   |   |   |   |-- RegisterStep3Terms.kt
|   |   |   |   |-- RegisterStep4DniFront.kt
|   |   |   |   |-- RegisterStep5DniBack.kt
|   |   |   |   |-- RegisterStep6FacialLiveness.kt
|   |   |   |   |-- RegisterStep7Verifying.kt
|   |   |   |   |-- RegisterStep8Welcome.kt
|   |   |   |   |-- TransactionDetailScreen.kt
|   |   |   |   |-- TransferHoldScreen.kt
|   |   |   |   |-- TransferResultScreen.kt
|   |   |   |   |-- TransferScreen.kt
|   |   |   |   |-- VerifySmsScreen.kt
|   |   |   |-- theme/                    <-- Configuracion visual global
|   |   |   |   |-- Color.kt               <-- Paleta de colores del banco
|   |   |   |   |-- Theme.kt               <-- Temas claro/oscuro
|   |   |   |   |-- Type.kt                <-- Tipografias (Typography)
|   |   |
|   |   |-- viewmodel/                  <-- Capa de Logica de Presentacion
|   |       |-- RegisterViewModel.kt       <-- Estado y logica del flujo completo de registro
|   |
|   |-- build.gradle.kts                <-- Configuracion de dependencias del modulo
|-- build.gradle.kts                    <-- Configuracion del proyecto raiz
`

## Guia de Ejecucion

1. Abre **Android Studio Ladybug**.
2. Selecciona *Open* e importa la carpeta raiz del proyecto.
3. Espera a que Gradle sincronice todas las dependencias.
4. Conecta un dispositivo fisico mediante USB (Depuracion USB habilitada) o inicia un Emulador (AVD).
5. (Opcional) Si necesitas probar conexiones, asegurate de modificar las IPs en las interfaces pi/ por tu red local actual.
6. Presiona el boton **Run (Shift + F10)** para compilar y lanzar la aplicacion.
