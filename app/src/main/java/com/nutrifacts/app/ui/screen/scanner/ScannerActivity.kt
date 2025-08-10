package com.nutrifacts.app.ui.screen.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.util.concurrent.Executors

class ScannerActivity : ComponentActivity() {

    private val viewModel by viewModels<ScannerViewModel> {
        ProductViewModelFactory.getInstance(this)
    }

    // State izin kamera agar bisa diubah di onResume
    private var _hasPermission = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek awal izin kamera
        _hasPermission.value = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            NutrifactsTheme {
                val navController = rememberNavController()
                var hasPermission by _hasPermission
                var showPermissionDialog by remember { mutableStateOf(!hasPermission) }
                var toastShown by remember { mutableStateOf(false) }

                NavHost(navController = navController, startDestination = Screen.Scanner.route) {
                    composable(Screen.Scanner.route) {
                        val context = LocalContext.current
                        val lifecycleOwner = LocalLifecycleOwner.current
                        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
                        val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
                        var barcode by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(false) }

                        // Tampilkan dialog jika izin belum diberikan
                        if (showPermissionDialog && !hasPermission) {
                            AlertDialog(
                                onDismissRequest = { finish() },
                                title = {
                                    Text(
                                        "Izin Kamera Diperlukan",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                text = {
                                    Text(
                                        buildAnnotatedString {
                                            append("Untuk menggunakan fitur ")
                                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                            append("Scan Barcode")
                                            pop()
                                            append(", silakan nyalakan izin kamera di pengaturan aplikasi.")
                                        },
                                        textAlign = TextAlign.Justify
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        startActivity(intent)
                                    }) {
                                        Text("Buka Pengaturan", fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { finish() }) {
                                        Text("Batal")
                                    }
                                }
                            )
                        }

                        // Jika izin kamera sudah ada
                        if (hasPermission) {
                            // Tampilkan toast landscape hanya sekali
                            LaunchedEffect(Unit) {
                                if (!toastShown) {
                                    Toast.makeText(
                                        context,
                                        "Gunakan mode landscape untuk Scan Barcode Produk",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    toastShown = true
                                }
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.fillMaxSize()) {
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
                                                if (result.all { it.isDigit() } && barcode.isEmpty()) {
                                                    barcode = result
                                                    isLoading = true
                                                    try {
                                                        cameraProviderFuture.get().unbindAll()
                                                    } catch (_: Exception) { }
                                                    viewModel.getProductByBarcode(result)
                                                }
                                            })

                                            try {
                                                cameraProviderFuture.get().bindToLifecycle(
                                                    lifecycleOwner, selector, preview, imageAnalysis
                                                )
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            previewView
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                    )

                                    val apiResult by viewModel.result.collectAsState(initial = Result.Loading)

                                    LaunchedEffect(apiResult) {
                                        when (apiResult) {
                                            is Result.Success -> {
                                                isLoading = false
                                                if (barcode.isNotEmpty()) {
                                                    navController.navigate(Screen.Detail.createRoute(barcode))
                                                }
                                            }
                                            is Result.Error -> {
                                                isLoading = false
                                                val err = (apiResult as Result.Error).error
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {}
                                        }
                                    }
                                }

                                // Overlay loading
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
                    }

                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument("barcode") { type = NavType.StringType })
                    ) {
                        val barcodeArg = it.arguments?.getString("barcode") ?: ""
                        DetailScreen(barcode = barcodeArg)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Cek ulang izin kamera saat kembali dari pengaturan
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted && !_hasPermission.value) {
            _hasPermission.value = true // Langsung aktifkan mode kamera
        }
    }
}
