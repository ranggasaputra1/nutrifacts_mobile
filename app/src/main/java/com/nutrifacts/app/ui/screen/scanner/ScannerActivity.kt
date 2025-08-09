package com.nutrifacts.app.ui.screen.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.ui.factory.ProductViewModelFactory
import com.nutrifacts.app.ui.navigation.Screen
import com.nutrifacts.app.ui.screen.detail.DetailScreen
import com.nutrifacts.app.ui.screen.scanner.ui.theme.NutrifactsTheme

class ScannerActivity : ComponentActivity() {

    private val viewModel by viewModels<ScannerViewModel> {
        ProductViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to the previous screen
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        setContent {
            NutrifactsTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Scanner.route) {
                    composable(Screen.Scanner.route) {
                        var barcode by remember {
                            mutableStateOf("")
                        }
                        val context = LocalContext.current
                        val lifecycleOwner = LocalLifecycleOwner.current
                        val cameraProviderFuture = remember {
                            ProcessCameraProvider.getInstance(context)
                        }
                        var hasPermission by remember {
                            mutableStateOf(hasRequiredPermissions())
                        }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission(),
                            onResult = { granted ->
                                hasPermission = granted
                            }
                        )
                        LaunchedEffect(key1 = true) {
                            if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
                        }
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (hasPermission) {
                                Toast.makeText(
                                    context,
                                    "Please use landscape mode to scan",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                AndroidView(
                                    factory = { context ->
                                        val previewView = PreviewView(context)
                                        val preview = Preview.Builder().build()
                                        val selector = CameraSelector.Builder()
                                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                            .build()
                                        preview.setSurfaceProvider(previewView.surfaceProvider)
                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setTargetResolution(
                                                Size(
                                                    previewView.width,
                                                    previewView.height
                                                )
                                            )
                                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                            .build()
                                        imageAnalysis.setAnalyzer(
                                            ContextCompat.getMainExecutor(context),
                                            BarcodeScanner { result ->
                                                barcode = result
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
                                    viewModel.result.collect { result ->
                                        if (result != null && barcode != "" && barcode != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    viewModel.getProductByBarcode(barcode)
                                                }

                                                is Result.Success -> {
                                                    navController.navigate(
                                                        Screen.Detail.createRoute(barcode)
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
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument("barcode") { type = NavType.StringType })
                    ) {
                        val barcode = it.arguments?.getString("barcode") ?: ""
                        DetailScreen(barcode = barcode)
                    }
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
        )
    }
}