package com.banco.digital.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.YuvImage
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors
import kotlinx.coroutines.delay

// =====================================================================
// 1. CAPTURA DE DOCUMENTO DNI (CameraX + Overlay Rectangular Canvas)
// =====================================================================
@Composable
fun DniCameraCapture(
    tituloGuia: String,
    onPhotoCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedUriString by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Guardar imagen de galería temporalmente a un archivo físico
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            
            val photoFile = File(context.cacheDir, "dni_gallery_${System.currentTimeMillis()}.jpg")
            val outputStream = java.io.FileOutputStream(photoFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            
            capturedBitmap = bitmap
            capturedUriString = photoFile.absolutePath
        }
    }

    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)

    if (capturedBitmap != null && capturedUriString != null) {
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

            Spacer(modifier = Modifier.height(24.dp))

            // Previsualización de la foto capturada
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF6EE7B7), RoundedCornerShape(16.dp)),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        capturedBitmap = null
                        capturedUriString = null
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text("Reintentar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = mintGradient)
                        .clickable { capturedUriString?.let { onPhotoCaptured(it) } },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Confirmar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDarkText
                    )
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
                        
                        // Añadir auto-focus continuo
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
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

            // Launcher para Cámara Nativa del Celular
            var tempPhotoPath by remember { mutableStateOf<String?>(null) }
            var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
            val nativeCameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success && tempPhotoPath != null) {
                    // La cámara nativa ya guardó la foto en tempPhotoPath
                    val file = File(tempPhotoPath!!)
                    if (file.exists()) {
                        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                        capturedBitmap = bitmap
                        capturedUriString = file.absolutePath
                    }
                }
            }

            // Botones inferiores (Galería y Cámara Nativa)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Galería
                IconButton(
                    onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.size(64.dp).background(Color(0x80000000), CircleShape)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                // Botón Cámara Nativa (Reemplaza al defectuoso CameraX)
                FloatingActionButton(
                    onClick = { 
                        val photoFile = File(context.cacheDir, "dni_native_${System.currentTimeMillis()}.jpg")
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        tempPhotoPath = photoFile.absolutePath
                        tempPhotoUri = uri
                        nativeCameraLauncher.launch(uri)
                    },
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cámara Nativa",
                        modifier = Modifier.size(40.dp)
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
// 2. ESCANEO FACIAL CON PRUEBA DE VIDA INTERACTIVA
// =====================================================================
@OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun FaceLivenessCameraCapture(
    onFramesCaptured: (List<ByteArray>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    
    // Animar la barra de progreso suavemente
    val animatedProgress by animateFloatAsState(
        targetValue = recordingProgress, 
        animationSpec = tween(1000) // Animación fluida de 1 segundo
    )
    
    // Estados de movimiento (Liveness estricto)
    var lookedLeft by remember { mutableStateOf(false) }
    var lookedRight by remember { mutableStateOf(false) }
    var lookedUp by remember { mutableStateOf(false) }
    var lookedDown by remember { mutableStateOf(false) }

    val capturedFrames = remember { mutableListOf<ByteArray>() }
    var frameTimer: Long = 0

    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val executor = Executors.newSingleThreadExecutor()
                
                // Configurar detector de rostros de ML Kit en modo RÁPIDO (solo para rastrear ángulos)
                val options = FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build()
                val detector = FaceDetection.getClient(options)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        
                        // Guardar 1 frame cada 150ms para el backend (más rápido para más ángulos)
                        if (isRecording) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - frameTimer >= 150) {
                                frameTimer = currentTime
                                try {
                                    val yBuffer = imageProxy.planes[0].buffer
                                    val uBuffer = imageProxy.planes[1].buffer
                                    val vBuffer = imageProxy.planes[2].buffer
                                    val ySize = yBuffer.remaining()
                                    val uSize = uBuffer.remaining()
                                    val vSize = vBuffer.remaining()
                                    val nv21 = ByteArray(ySize + uSize + vSize)
                                    yBuffer.get(nv21, 0, ySize)
                                    vBuffer.get(nv21, ySize, vSize)
                                    uBuffer.get(nv21, ySize + vSize, uSize)

                                    val yuvImage = YuvImage(
                                        nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null
                                    )
                                    val out = ByteArrayOutputStream()
                                    yuvImage.compressToJpeg(
                                        android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height),
                                        80, out
                                    )
                                    capturedFrames.add(out.toByteArray())
                                } catch (e: Exception) {
                                    Log.e("FaceLiveness", "Error converting frame", e)
                                }
                            }
                        }

                        // Análisis de rostro (Interactividad estricta)
                        if (mediaImage != null && isRecording) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            detector.process(image)
                                .addOnSuccessListener { faces ->
                                    if (faces.isNotEmpty()) {
                                        val face = faces[0]
                                        val yaw = face.headEulerAngleY // Rotación Izquierda/Derecha
                                        val pitch = face.headEulerAngleX // Rotación Arriba/Abajo
                                        
                                        // Ángulos más estrictos
                                        if (yaw < -25f) lookedLeft = true
                                        if (yaw > 25f) lookedRight = true
                                        if (pitch > 15f) lookedUp = true
                                        if (pitch < -10f) lookedDown = true
                                        
                                        // Actualizar barra de progreso según los 4 movimientos clave
                                        val completedSteps = listOf(lookedLeft, lookedRight, lookedUp, lookedDown).count { it }
                                        recordingProgress = completedSteps / 4f

                                        // Si completó los movimientos, enviar al servidor
                                        if (recordingProgress >= 1f) {
                                            isRecording = false
                                            onFramesCaptured(capturedFrames.toList())
                                        }
                                    }
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
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("FaceLiveness", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Óvalo con la barra animada
        FaceOvalOverlayCanvas(
            progress = animatedProgress,
            modifier = Modifier.fillMaxSize()
        )

        // Botón Iniciar (solo si no está grabando y no terminó)
        if (!isRecording && recordingProgress < 1f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(32.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = mintGradient)
                    .clickable { 
                        // Iniciar grabación interactiva
                        isRecording = true 
                        capturedFrames.clear()
                        lookedLeft = false
                        lookedRight = false
                        lookedUp = false
                        lookedDown = false
                        recordingProgress = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Empezar Prueba de Vida",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryDarkText
                )
            }
        }
    }
}

// =====================================================================
// 3. CANVAS OVERLAY (Óvalo de la Cara)
// =====================================================================
@Composable
fun FaceOvalOverlayCanvas(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val ovalWidth = size.width * 0.72f
        val ovalHeight = ovalWidth * 1.35f
        val left = (size.width - ovalWidth) / 2f
        val top = (size.height - ovalHeight) / 2f - 30.dp.toPx()

        val ovalRect = androidx.compose.ui.geometry.Rect(left, top, left + ovalWidth, top + ovalHeight)

        // Fondo oscuro con agujero ovalado
        val path = androidx.compose.ui.graphics.Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
            addOval(ovalRect)
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }
        drawPath(path, Color(0xDD000000))

        // Borde del óvalo (Fijo base)
        drawOval(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = ovalRect.topLeft,
            size = ovalRect.size,
            style = Stroke(width = 4.dp.toPx())
        )

        // Barra de progreso circular interactiva
        if (progress > 0f) {
            drawArc(
                color = Color(0xFF10B981), // Verde Mint
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(ovalRect.left - 10f, ovalRect.top - 10f),
                size = androidx.compose.ui.geometry.Size(ovalRect.width + 20f, ovalRect.height + 20f),
                style = Stroke(
                    width = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
