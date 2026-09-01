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

Los datos se guardan en una base de datos **PostgreSQL** mediante un servidor API (Node.js) que se levanta con **Docker**.

---

## 🛠️ Requisitos Previos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta | Versión | Descarga |
|---|---|---|
| **Android Studio** | Ladybug o superior | [developer.android.com](https://developer.android.com/studio) |
| **JDK** | 11 o superior | Viene incluido con Android Studio |
| **Docker Desktop** | Última versión | [docker.com](https://www.docker.com/products/docker-desktop/) |
| **Git** | Última versión | [git-scm.com](https://git-scm.com/) |

### Versiones del proyecto
- **AGP (Android Gradle Plugin):** 9.3.1
- **Kotlin:** 2.2.10
- **Compose BOM:** 2026.02.01
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 37
- **compileSdk:** 37

---

## 🚀 Pasos para levantar el proyecto

### Paso 1: Clonar el repositorio
```bash
git clone https://github.com/TU_USUARIO/DigitalBankApp.git
cd DigitalBankApp
```

### Paso 2: Levantar el Backend con Docker
Abre **Docker Desktop** y asegúrate de que esté corriendo (el icono de la ballena en la barra de tareas).

Luego abre una **terminal** (PowerShell o CMD) y ejecuta:
```bash
cd backend
docker-compose up --build
```

Espera hasta que veas este mensaje:
```
🏦 BANCO DIGITAL - Servidor API
📡 Servidor escuchando en: http://localhost:3000
✅ Base de datos PostgreSQL conectada y tabla usuarios_registro lista.
```

> **Nota:** Si tienes PostgreSQL instalado en tu PC, no te preocupes. Docker usa el puerto **5433** para no chocar con tu PostgreSQL local (puerto 5432).

### Paso 3: Verificar que el backend funciona
Abre tu navegador y visita:
- **http://localhost:3000/health** → Debe mostrar: `{"status":"OK"}`
- **http://localhost:3000/api/users** → Debe mostrar: `[]` (lista vacía al inicio)

### Paso 4: Abrir el proyecto en Android Studio
1. Abre Android Studio
2. Selecciona **Open** y navega a la carpeta `DigitalBankApp`
3. Espera a que Gradle sincronice las dependencias (puede tomar unos minutos la primera vez)

### Paso 5: Conectar tu celular Android
1. Conecta tu celular por **cable USB**
2. Activa la **Depuración USB** en las opciones de desarrollador de tu celular
3. Verifica que Android Studio detecte tu dispositivo en la barra superior

### Paso 6: Configurar la IP de tu computadora
Tu celular necesita saber la IP de tu PC para enviar los datos. Para encontrar tu IP:

**En Windows (PowerShell):**
```bash
ipconfig | findstr "IPv4"
```

Luego abre el archivo `app/src/main/java/com/banco/digital/data/repository/RegisterRepository.kt` y cambia la IP por la tuya:
```kotlin
private val endpoints = listOf(
    "http://TU_IP_AQUI:3000/",   // ← Cambia esto por tu IP
    "http://127.0.0.1:3000/",
    "http://10.0.2.2:3000/"
)
```

> **Importante:** Tu celular y tu PC deben estar conectados a la **misma red Wi-Fi**.

### Paso 7: Ejecutar la app
1. Haz clic en el botón ▶️ **Run** en Android Studio
2. La app se instalará en tu celular
3. ¡Regístrate y ve los datos en `http://localhost:3000/api/users`!

---

## 📊 Ver los datos en PostgreSQL (Opcional)

Si quieres ver los datos directamente en tu aplicación de PostgreSQL (como pgAdmin, DBeaver, etc.):

| Campo | Valor |
|---|---|
| **Host** | `localhost` |
| **Puerto** | `5433` |
| **Base de datos** | `banco_digital_db` |
| **Usuario** | `postgres` |
| **Contraseña** | `Orki-12-31` |

La tabla con los registros se llama: `usuarios_registro`

---

## 📂 Estructura del Proyecto

```
DigitalBankApp/
├── app/                          ← Código de la app Android
│   └── src/main/java/com/banco/digital/
│       ├── data/
│       │   ├── api/              ← Servicio de conexión al backend
│       │   ├── local/            ← Base de datos SQLite local (respaldo)
│       │   ├── model/            ← Modelos de datos
│       │   └── repository/       ← Repositorio (envía datos al servidor)
│       └── ui/
│           ├── components/       ← Componentes reutilizables (barra de progreso, etc.)
│           ├── screens/          ← Pantallas de la app (Register Steps 1-8, Login)
│           └── viewmodel/        ← ViewModels
├── backend/                      ← Servidor API + Docker
│   ├── docker-compose.yml        ← Levanta PostgreSQL + servidor con un comando
│   ├── Dockerfile                ← Configuración del contenedor del servidor
│   ├── server.js                 ← API del servidor (Node.js + Express)
│   └── package.json              ← Dependencias de Node.js
└── README.md                     ← Este archivo
```

---

## 🔧 Comandos útiles

| Comando | Qué hace |
|---|---|
| `docker-compose up` | Levanta el servidor y la base de datos |
| `docker-compose down` | Detiene todo |
| `docker-compose up --build` | Reconstruye y levanta (si cambias server.js) |
| `docker logs banco-digital-servidor-api` | Ver los logs del servidor |
| `docker logs banco-digital-base-de-datos` | Ver los logs de PostgreSQL |

---

## 📱 Tecnologías utilizadas

- **Kotlin** + **Jetpack Compose** (UI declarativa)
- **CameraX** (captura de fotos del DNI y selfie)
- **Google ML Kit** (detección facial y reconocimiento de texto)
- **Retrofit** + **OkHttp** (comunicación con el servidor)
- **SQLite** (base de datos local de respaldo en el celular)
- **Node.js** + **Express** (servidor API)
- **PostgreSQL 16** (base de datos principal)
- **Docker** (contenedores para fácil despliegue)
