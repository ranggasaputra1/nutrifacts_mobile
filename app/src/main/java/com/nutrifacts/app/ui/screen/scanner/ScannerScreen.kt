package com.nutrifacts.app.ui.screen.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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


private val CAMERAX_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
)

@Composable
fun ScannerScreen(
    navController: NavHostController,
    viewModel: ScannerViewModel = viewModel(
        factory = ProductViewModelFactory(Injection.provideProductRepository(LocalContext.current))
    ),
) {
//    val activity = LocalContext.current as Activity
//    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    val onBackPressedCallback =
        rememberUpdatedState(newValue = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to the previous screen
                navController.navigateUp()
            }
        })
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val barcode = remember {
        mutableStateOf("")
    }
    val cameraProviderFuture by remember {
        mutableStateOf(ProcessCameraProvider.getInstance(context))
    }

    var hasPermission by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                navController.navigateUp()
            } else {
                hasPermission = true
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    BackHandler(enabled = onBackPressedCallback.value.isEnabled) {
        onBackPressedCallback.value.handleOnBackPressed()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (hasPermission) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(previewView.width, previewView.height))
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        BarcodeScanner { result ->
                            barcode.value = result
                        }
                    )
                    try {
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner, selector, preview, imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )

            LaunchedEffect(barcode) {
                viewModel.getProductByBarcode(barcode.value)
                viewModel.result.collect { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {

                            }

                            is Result.Success -> {
                                navController.navigate(
                                    Screen.Detail.createRoute(barcode.value)
                                )
                            }

                            is Result.Error -> {
                                Toast.makeText(
                                    context,
                                    result.error,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun hasRequiredPermissions(): Boolean {
    return CAMERAX_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            LocalContext.current,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}