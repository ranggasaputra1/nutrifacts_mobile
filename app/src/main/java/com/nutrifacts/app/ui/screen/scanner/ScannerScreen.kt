package com.nutrifacts.app.ui.screen.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.factory.ProductViewModelFactory
import com.nutrifacts.app.ui.navigation.Screen
import java.util.concurrent.Executors

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
    val barcode = remember { mutableStateOf("") }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) navController.navigateUp() else hasPermission = true
        }
    )

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

    Column(modifier = Modifier.fillMaxSize()) {
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
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor, BarcodeScanner { result ->
                        if (result.all { it.isDigit() } && barcode.value.isEmpty()) {
                            barcode.value = result
                            cameraProviderFuture.get().unbindAll()
                        }
                    })

                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner, selector, preview, imageAnalysis
                    )

                    previewView
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )

            LaunchedEffect(barcode.value) {
                if (barcode.value.isNotEmpty()) {
                    viewModel.getProductByBarcode(barcode.value)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.result.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            navController.navigate(Screen.Detail.createRoute(barcode.value))
                        }
                        is Result.Error -> {
                            // Bisa tambahkan Toast error di sini kalau mau
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
