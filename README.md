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
- **http://localhost:3000/health** → Estado de API Node.js
- **http://localhost:8000/health** → Estado de KYC IA FastAPI
- **http://localhost:3000/api/users** → Lista de usuarios en base de datos

### Paso 5: Ejecutar en tu celular
1. Abre el proyecto en Android Studio.
2. Asegúrate de cambiar la IP en `app/src/main/java/com/banco/digital/data/api/RegisterApiService.kt` y `KycApiService.kt` por tu IPv4 local de red Wi-Fi (ej. `192.168.1.XX`).
3. Conecta tu celular y ejecuta la app.

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

```
DigitalBankApp/
├── app/                          ← Código de la app Android (Jetpack Compose)
├── backend/                      ← Docker compose y servidores
│   ├── .env                      ← Contraseñas (ignorado en git)
│   ├── server.js                 ← Servidor Node.js (API a PostgreSQL)
│   └── kyc/
│       ├── main.py               ← Servidor FastAPI (Python) para IA Biométrica
│       └── requirements.txt      ← Librerías Python (DeepFace, EasyOCR, MediaPipe)
└── README.md                     
```

---

## 📱 Tecnologías utilizadas

- **Kotlin** + **Jetpack Compose**
- **CameraX** (Captura de DNI e imágenes)
- **Retrofit** + **OkHttp** (Conexión API)
- **Node.js** + **Express** (API Gateway)
- **PostgreSQL 16** (Base de datos relacional)
- **Python / FastAPI** (Microservicio de Inteligencia Artificial)
- **MediaPipe / DeepFace / EasyOCR** (Validación KYC, FaceID y Liveness check en RAM)
- **Docker** (Orquestación de microservicios)
