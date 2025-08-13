package com.nutrifacts.app.ui.screen.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.factory.ProductViewModelFactory
import com.nutrifacts.app.ui.navigation.Screen
import java.util.concurrent.Executors
import kotlinx.coroutines.delay

private val CAMERAX_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

@Composable
fun ScannerScreen(
    navController: NavHostController,
    viewModel: ScannerViewModel = viewModel(
        factory = ProductViewModelFactory(Injection.provideProductRepository(LocalContext.current))
    ),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) navController.navigateUp() else hasPermission = true
        }
    )

    var barcode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showBarcodeDetectedLine by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val onBackPressedCallback = rememberUpdatedState(
        newValue = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigateUp()
            }
        }
    )
    BackHandler(enabled = onBackPressedCallback.value.isEnabled) {
        onBackPressedCallback.value.handleOnBackPressed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val onBarcodeScanned: (String) -> Unit = { result ->
                        if (!isProcessing) {
                            barcode = result
                            isProcessing = true
                            showBarcodeDetectedLine = true
                            cameraProviderFuture.get().unbindAll()
                            viewModel.getProductByBarcode(result)
                        }
                    }

                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeScanner(onBarcodeScanned))

                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner, selector, preview, imageAnalysis
                    )

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isProcessing) {
                val infiniteTransition = rememberInfiniteTransition(label = "scanner_line")
                val yOffset by infiniteTransition.animateFloat(
                    initialValue = -20.dp.value,
                    targetValue = 20.dp.value,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 500, easing = FastOutLinearInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = "scanner_y_offset"
                )

                LaunchedEffect(Unit) {
                    delay(300)
                    showBarcodeDetectedLine = false
                    isLoading = true
                }

                if(showBarcodeDetectedLine){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp)
                            .offset(y = yOffset.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color.Green)
                        )
                    }
                }
            }

            val apiResult by viewModel.result.collectAsState(initial = Result.Loading)

            LaunchedEffect(apiResult) {
                when (apiResult) {
                    is Result.Success -> {
                        isLoading = false
                        isProcessing = false
                        if (barcode.isNotEmpty()) {
                            navController.navigate(Screen.Detail.createRoute(barcode))
                            barcode = ""
                            // Memastikan state di-reset setelah sukses
                            viewModel.resetResult()
                        }
                    }
                    is Result.Error -> {
                        isLoading = false
                        val errorMessage = (apiResult as Result.Error).error
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

                        delay(300)
                        isProcessing = false
                        barcode = ""
                        // Memastikan state di-reset setelah gagal
                        viewModel.resetResult()
                    }
                    else -> {}
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}