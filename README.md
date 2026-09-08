# 🏦 Banco Digital App

Aplicación móvil Android de un banco digital con registro biométrico completo (DNI + reconocimiento facial), desarrollada con **Kotlin + Jetpack Compose**.

## 📋 Descripción

Esta app simula el proceso de apertura de cuenta de un banco digital. El usuario pasa por un registro de 8 pasos que incluye:
1. Datos personales (nombre, documento, fecha de nacimiento, correo, celular)
2. Creación de contraseña segura
3. Aceptación de términos y condiciones
4. Foto del DNI (frontal)
5. Foto del DNI (reverso)
6. Selfie / reconocimiento facial
7. Verificación de identidad
8. Bienvenida y número de cuenta generado

Los datos de los usuarios se guardan en una base de datos **PostgreSQL** mediante un servidor API (Node.js), y las validaciones biométricas con IA se realizan en un servidor de **FastAPI (Python)**. Ambos se levantan fácilmente con **Docker**.

---

## 🛠️ Requisitos Previos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta | Versión |
|---|---|
| **Android Studio** | Ladybug o superior |
| **JDK** | 11 o superior |
| **Docker Desktop** | Última versión |
| **Git** | Última versión |

---

## 🚀 Pasos para levantar el proyecto

### Paso 1: Clonar el repositorio
```bash
git clone https://github.com/TU_USUARIO/DigitalBankApp.git
cd DigitalBankApp
```

### Paso 2: Crear el archivo `.env` (Opcional pero recomendado)
En la carpeta `backend`, puedes crear un archivo llamado `.env` para configurar tu contraseña de base de datos segura.
```env
DB_PASSWORD=TuContraseñaSegura
```
*(Este archivo está ignorado en git para proteger tu contraseña).*

### Paso 3: Levantar el Backend con Docker
Abre **Docker Desktop** y asegúrate de que esté corriendo. Luego abre una **terminal** y ejecuta:
```bash
cd backend
docker-compose up --build -d
```

Esto levantará 3 contenedores:
1. `banco-digital-base-de-datos`: PostgreSQL (Puerto 5433).
2. `banco-digital-servidor-api`: API Node.js para guardar usuarios (Puerto 3000).
3. `banco-digital-servidor-kyc-ia`: Microservicio Python FastAPI de IA Biométrica (Puerto 8000).

### Paso 4: Verificar que el backend funciona
Abre tu navegador y visita:
- **http://localhost:3000/health** -> Estado de API Node.js
- **http://localhost:8000/health** -> Estado de KYC IA FastAPI
- **http://localhost:3000/api/users** -> Lista de usuarios en base de datos

### Paso 5: Ejecutar en tu celular
1. Abre el proyecto en Android Studio.
2. Asegúrate de cambiar la IP en `app/src/main/java/com/banco/digital/data/api/RegisterApiService.kt` y `KycApiService.kt` por tu IPv4 local de red Wi-Fi (ej. `192.168.1.XX`).
3. Conecta tu celular y ejecuta la app.

---

## 🐧 Ejecución Nativa en Linux (Recomendado)

Dado que Docker fue diseñado originalmente para Linux, ejecutar este backend en tu partición o sistema operativo Linux (Ubuntu, Fedora, Arch) ofrece **ventajas masivas de rendimiento** en comparación con Windows/WSL2:

1. **Cero consumo extra de RAM:** No existe la máquina virtual `VmmemWSL`. Docker correrá directamente en el Kernel de tu computadora, liberando los 3 GB - 5 GB de memoria que Windows reserva inútilmente.
2. **Liberación Instantánea de Caché:** Cuando la IA termine de procesar un DNI, la RAM regresará automáticamente a tu sistema sin quedarse "atascada" como en WSL.
3. **Aceleración por GPU más sencilla:** Habilitar tu tarjeta gráfica (NVIDIA) para el modelo biométrico facial toma solo unos clics en Linux, permitiendo validaciones en milisegundos.

**Pasos para Linux:**
1. Instala `docker` y `docker-compose` a nivel de sistema (`sudo apt install docker-compose`).
2. Clona el repositorio y navega al backend: `cd backend`.
3. Ejecuta `sudo docker-compose up --build -d`.
4. (Opcional) Si en el futuro reactivamos **DeepFace/MTCNN**, tu sistema Linux soportará el peso de las librerías sin estrangular el procesador, aprovechando el acceso nativo a tu hardware.

---

## 📊 Privacidad y Manejo de Datos (Importante)

Para garantizar la seguridad de los usuarios, **no se guarda ninguna foto en el disco ni en la base de datos**.
1. La base de datos solo almacena las URLs de referencia (texto) a la memoria caché local temporal del celular.
2. El servidor Python (FastAPI) procesa el *Liveness detection* y la lectura de OCR del DNI **estrictamente en la memoria RAM**.
3. Tan pronto el servidor envía el resultado (Verificado / Rechazado), las imágenes son descartadas permanentemente de la RAM.
4. Las fotos nunca tocan el disco duro del servidor de IA ni se almacenan en PostgreSQL.

---

## 📊 Ver los datos en PostgreSQL (Opcional)

Si quieres ver los datos directamente en tu aplicación de PostgreSQL (como pgAdmin 4):

| Campo | Valor |
|---|---|
| **Host** | `localhost` |
| **Puerto** | `5433` (Para no chocar con el 5432) |
| **Base de datos** | `banco_digital_db` |
| **Usuario** | `postgres` |
| **Contraseña** | La que hayas puesto en `.env` (o `postgres-banco` por defecto) |

La tabla con los registros se llama: `usuarios_registro`

---

## 📂 Estructura del Proyecto

El código fuente de la aplicación móvil se encuentra organizado bajo el patrón de arquitectura MVVM y Clean Architecture simplificada. A continuación, el detalle exacto de cada directorio y componente UI que hemos creado en Android Studio:

```text
DigitalBankApp/
|-- app/
|   |-- src/main/java/com/banco/digital/
|   |   |-- MainActivity.kt             <-- Punto de entrada principal
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
```

---

## 📱 Tecnologías utilizadas

- **Android Studio Ladybug** (o superior)
- **Kotlin**
- **Jetpack Compose** (Material Design 3)
- **Java 11** (JavaVersion.VERSION_11)
- **Android SDK** (minSdk 26, targetSdk 37)
- **Jetpack Navigation Compose** (v2.7.7)
- **CameraX** (v1.4.0)
- **Retrofit** (v2.11.0) con OkHttp Logging Interceptor (v4.12.0)
- **Google ML Kit Face Detection** (v16.1.6)
- **AndroidX Biometric** (v1.1.0)
- **ViewModel Compose** (v2.8.7) - Arquitectura MVVM
