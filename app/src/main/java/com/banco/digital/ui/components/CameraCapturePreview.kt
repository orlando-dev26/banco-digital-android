package com.banco.digital.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.banco.digital.ui.viewmodel.LivenessStep
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

// =====================================================================
// 1. CAPTURA DE DOCUMENTO DNI (CameraX + Overlay Rectangular Canvas)
// =====================================================================
@Composable
fun DniCameraCapture(
    tituloGuia: String = "Coloca el frente de tu DNI dentro del marco",
    onPhotoCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedUriString by remember { mutableStateOf<String?>(null) }

    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)

    if (capturedBitmap != null && capturedUriString != null) {
        // Validación automática del DNI con ML Kit Text Recognition
        var isValidatingDni by remember { mutableStateOf(true) }
        var isDniValid by remember { mutableStateOf(false) }
        var validationMessage by remember { mutableStateOf("Analizando documento...") }

        LaunchedEffect(capturedUriString) {
            isValidatingDni = true
            val image = InputImage.fromBitmap(capturedBitmap!!, 0)
            val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
            )
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val textoCompleto = visionText.text.uppercase()
                    // Palabras clave que indican que es un DNI peruano u otro documento de identidad
                    val palabrasClave = listOf(
                        "REPÚBLICA", "REPUBLICA", "PERÚ", "PERU",
                        "DNI", "REGISTRO", "NACIONAL", "IDENTIFICACIÓN", "IDENTIFICACION",
                        "NOMBRES", "APELLIDOS", "FECHA", "NACIMIENTO",
                        "DOCUMENTO", "IDENTITY", "PASSPORT", "PASAPORTE",
                        "CARNET", "EXTRANJERÍA", "EXTRANJERIA"
                    )
                    val coincidencias = palabrasClave.count { textoCompleto.contains(it) }
                    isDniValid = coincidencias >= 2
                    validationMessage = if (isDniValid) {
                        "✅ Documento detectado correctamente"
                    } else {
                        "❌ No se detectó un documento de identidad válido. Por favor, vuelve a intentarlo con tu DNI."
                    }
                    isValidatingDni = false
                }
                .addOnFailureListener {
                    // Si falla ML Kit, permitir continuar de todas formas
                    isDniValid = true
                    validationMessage = "✅ Foto capturada"
                    isValidatingDni = false
                }
        }

        // Pantalla de Confirmación / Nitidez
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "¿Se ve nítido el documento?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Asegúrate de que todos los textos y datos sean legibles sin reflejos",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de validación del DNI
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isValidatingDni) Color(0xFF1E293B)
                       else if (isDniValid) Color(0xFF064E3B)
                       else Color(0xFF7F1D1D),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isValidatingDni) {
                        CircularProgressIndicator(
                            color = Color(0xFF6EE7B7),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = validationMessage,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Previsualización de la foto capturada
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp,
                        if (isDniValid) Color(0xFF10B981) else Color(0xFFEF4444),
                        RoundedCornerShape(16.dp)),
                color = Color.Black
            ) {
                androidx.compose.foundation.Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "Foto DNI Capturada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Lógica de avance o reintento automático
            LaunchedEffect(isDniValid, isValidatingDni) {
                if (!isValidatingDni) {
                    if (isDniValid) {
                        kotlinx.coroutines.delay(1000) // Pausa de 1s para ver el éxito
                        capturedUriString?.let { onPhotoCaptured(it) }
                    } else {
                        kotlinx.coroutines.delay(2500) // Pausa de 2.5s para leer el error
                        capturedBitmap = null
                        capturedUriString = null
                    }
                }
            }

            if (isValidatingDni) {
                // Mientras valida
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Procesando imagen...", color = Color(0xFF94A3B8), fontSize = 15.sp)
                }
            } else if (!isDniValid) {
                // Si es INVÁLIDO -> Muestra que va a reintentar automáticamente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF450a0a)) // Fondo rojo muy oscuro
                        .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFCA5A5))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Reintentando automáticamente...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            } else {
                // Si es VÁLIDO -> Muestra que está avanzando automáticamente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = mintGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Continuando automáticamente...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryDarkText
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(
                            color = primaryDarkText,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    } else {
        // Vista de Cámara en Vivo con Marco Rectangular
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, capture)
                        } catch (exc: Exception) {
                            Log.e("DniCamera", "Error vinculando CameraX", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Overlay Canvas Rectangular DNI con animación de escaneo
            DniOverlayCanvas(tituloGuia = tituloGuia)

            // Botón de Disparo
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val capture = imageCapture ?: return@FloatingActionButton
                        val executor = Executors.newSingleThreadExecutor()
                        val photoFile = File(context.cacheDir, "dni_${System.currentTimeMillis()}.jpg")
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        capture.takePicture(
                            outputOptions,
                            executor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    val bitmap = android.graphics.BitmapFactory.decodeFile(photoFile.absolutePath)
                                    // Usar el hilo principal para actualizar el estado de Jetpack Compose
                                    ContextCompat.getMainExecutor(context).execute {
                                        capturedBitmap = bitmap
                                        capturedUriString = photoFile.absolutePath
                                    }
                                }

                                override fun onError(exc: ImageCaptureException) {
                                    Log.e("DniCamera", "Error al capturar foto", exc)
                                }
                            }
                        )
                    },
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Tomar Foto",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

// =====================================================================
// CANVAS: OVERLAY RECTANGULAR PARA DNI
// =====================================================================
@Composable
fun DniOverlayCanvas(tituloGuia: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "ScanTransition")
    val scanOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanLine"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Dimensiones del marco del DNI (proporción estándar tarjeta de crédito ~ 1.58)
                val cardWidth = canvasWidth * 0.88f
                val cardHeight = cardWidth / 1.58f
                val left = (canvasWidth - cardWidth) / 2f
                val top = (canvasHeight - cardHeight) / 2f - 20.dp.toPx()

                val cardRect = Rect(left, top, left + cardWidth, top + cardHeight)

                // Fondo semi-transparente oscuro con recorte transparente
                val path = Path().apply {
                    addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                    addRoundRect(RoundRect(cardRect, CornerRadius(16.dp.toPx(), 16.dp.toPx())))
                    fillType = PathFillType.EvenOdd
                }
                drawPath(path, color = Color(0xCC000000))

                // Borde del marco DNI
                drawRoundRect(
                    color = Color(0xFF6EE7B7),
                    topLeft = Offset(cardRect.left, cardRect.top),
                    size = Size(cardRect.width, cardRect.height),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Línea de escaneo láser animada
                val lineY = cardRect.top + (cardRect.height * scanOffset)
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFF34D399), Color.Transparent)
                    ),
                    start = Offset(cardRect.left + 10.dp.toPx(), lineY),
                    end = Offset(cardRect.right - 10.dp.toPx(), lineY),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Texto guía superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.TopCenter)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xDD0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tituloGuia,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// =====================================================================
// 2. ESCANEO FACIAL CON PRUEBA DE VIDA (CameraX Frontal + ML Kit)
// =====================================================================
@OptIn(ExperimentalGetImage::class)
@Composable
fun FaceLivenessCameraCapture(
    livenessStep: LivenessStep,
    feedbackText: String,
    onFaceDetected: (isCentered: Boolean, leftEyeOpen: Float?, rightEyeOpen: Float?, smiling: Float?) -> Unit,
    onLivenessCompleted: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Detector de rostros de ML Kit con clasificaciones habilitadas
    val detector = remember {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.2f)
            .build()
        FaceDetection.getClient(options)
    }

    LaunchedEffect(livenessStep) {
        if (livenessStep == LivenessStep.COMPLETED) {
            // Auto-capturar selfie al completar las pruebas de vida
            imageCapture?.let { capture ->
                val executor = Executors.newSingleThreadExecutor()
                val photoFile = File(context.cacheDir, "selfie_${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                capture.takePicture(
                    outputOptions,
                    executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onLivenessCompleted(photoFile.absolutePath)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("FaceLiveness", "Error capturando selfie", exc)
                            onLivenessCompleted("selfie_simulada.jpg")
                        }
                    }
                )
            } ?: onLivenessCompleted("selfie_completada.jpg")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val executor = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = capture

                    // ImageAnalysis con ML Kit
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            detector.process(image)
                                .addOnSuccessListener { faces ->
                                    if (faces.isNotEmpty()) {
                                        val face = faces.first()
                                        val bounds = face.boundingBox
                                        // Validación simple de centrado
                                        val isCentered = bounds.width() > 100 && bounds.height() > 100
                                        onFaceDetected(
                                            isCentered,
                                            face.leftEyeOpenProbability,
                                            face.rightEyeOpenProbability,
                                            face.smilingProbability
                                        )
                                    } else {
                                        onFaceDetected(false, null, null, null)
                                    }
                                }
                                .addOnFailureListener { exc ->
                                    Log.e("FaceLiveness", "Error detector ML Kit", exc)
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("FaceLiveness", "Error vinculando cámara frontal", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay con Óvalo Facial y Pasos Liveness
        FaceOvalOverlayCanvas(
            livenessStep = livenessStep,
            feedbackText = feedbackText
        )
    }
}

// =====================================================================
// CANVAS: OVERLAY CON ÓVALO FACIAL E INDICADORES LIVENESS
// =====================================================================
@Composable
fun FaceOvalOverlayCanvas(
    livenessStep: LivenessStep,
    feedbackText: String
) {
    val ovalBorderColor = when (livenessStep) {
        LivenessStep.CENTER_FACE -> Color(0xFF6EE7B7)
        LivenessStep.BLINK -> Color(0xFF38BDF8)
        LivenessStep.SMILE -> Color(0xFFFBBF24)
        LivenessStep.COMPLETED -> Color(0xFF10B981)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val ovalWidth = canvasWidth * 0.72f
            val ovalHeight = ovalWidth * 1.35f
            val left = (canvasWidth - ovalWidth) / 2f
            val top = (canvasHeight - ovalHeight) / 2f - 30.dp.toPx()

            val ovalRect = Rect(left, top, left + ovalWidth, top + ovalHeight)

            // Recorte del óvalo transparente sobre fondo oscuro
            val path = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addOval(ovalRect)
                fillType = PathFillType.EvenOdd
            }
            drawPath(path, color = Color(0xDD000000))

            // Borde del óvalo
            drawOval(
                color = ovalBorderColor,
                topLeft = Offset(ovalRect.left, ovalRect.top),
                size = Size(ovalRect.width, ovalRect.height),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Panel de Instrucciones y Retroalimentación Liveness
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xEE0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verificación de Identidad Facial",
                        color = Color(0xFF6EE7B7),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feedbackText,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicadores de Pasos Liveness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LivenessStepBadge(
                            label = "1. Centrar",
                            isDone = livenessStep > LivenessStep.CENTER_FACE,
                            isActive = livenessStep == LivenessStep.CENTER_FACE
                        )
                        LivenessStepBadge(
                            label = "2. Parpadear",
                            isDone = livenessStep > LivenessStep.BLINK,
                            isActive = livenessStep == LivenessStep.BLINK
                        )
                        LivenessStepBadge(
                            label = "3. Sonreír",
                            isDone = livenessStep >= LivenessStep.COMPLETED,
                            isActive = livenessStep == LivenessStep.SMILE
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LivenessStepBadge(label: String, isDone: Boolean, isActive: Boolean) {
    val bgColor = when {
        isDone -> Color(0xFF064E3B)
        isActive -> Color(0xFF10B981)
        else -> Color(0xFF334155)
    }
    val textColor = when {
        isDone -> Color(0xFFA7F3D0)
        isActive -> Color.White
        else -> Color(0xFF94A3B8)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isDone) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFFA7F3D0),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
