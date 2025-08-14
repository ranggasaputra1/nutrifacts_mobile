package com.nutrifacts.app.ui.screen.detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.local.entity.History
import com.nutrifacts.app.data.model.ProductModel
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.factory.ProductViewModelFactory
import com.nutrifacts.app.utils.DateConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    barcode: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProductViewModelFactory.getInstance(LocalContext.current)
    ),
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val currentTimeMillis = System.currentTimeMillis()
    val formattedDate = DateConverter.convertMillisToString(currentTimeMillis)
    var isSaved by remember { mutableStateOf(false) }
    var thisSavedProductId by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(false) }

    val user by UserPreference.getInstance(LocalContext.current.dataStore).getSession()
        .collectAsState(
            initial = UserModel(0, "", false)
        )

    val productResult by viewModel.result.collectAsState(initial = Result.Loading)
    val savedProductResult by viewModel.saved.collectAsState(initial = Result.Loading)

    LaunchedEffect(key1 = user.id, key2 = barcode) {
        if (user.id != 0) {
            viewModel.getSavedProduct(user.id)
        }
    }

    // ✅ Optimasi: Panggil stringResource() di luar blok remember
    val caloriesLabel = stringResource(id = R.string.calories)
    val totalFatLabel = stringResource(id = R.string.total_fat)
    val satFatLabel = stringResource(id = R.string.sat_fat)
    val transFatLabel = stringResource(id = R.string.trans_fat)
    val cholesterolLabel = stringResource(id = R.string.cholesterol)
    val sodiumLabel = stringResource(id = R.string.sodium)
    val carbohydrateLabel = stringResource(id = R.string.carbohydrate)
    val fiberLabel = stringResource(id = R.string.fiber)
    val sugarLabel = stringResource(id = R.string.sugar)
    val proteinLabel = stringResource(id = R.string.protein)
    val vitaminALabel = stringResource(id = R.string.vitamin_a)
    val vitaminCLabel = stringResource(id = R.string.vitamin_c)
    val vitaminDLabel = stringResource(id = R.string.vitamin_d)
    val calciumLabel = stringResource(id = R.string.calcium)
    val ironLabel = stringResource(id = R.string.iron)


    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (val resultState = productResult) {
            is Result.Loading -> {
                loading = true
                viewModel.getProductByBarcode(barcode)
            }

            is Result.Success -> {
                loading = false
                val productData = resultState.data

                // ✅ Optimasi: Menggunakan 'remember' dengan nilai string yang sudah ada
                val formattedNutrientData by remember(productData) {
                    mutableStateOf(mapOf(
                        caloriesLabel to formatNutrientValue(productData.calories, "kkal"),
                        totalFatLabel to formatNutrientValue(productData.fat, "g"),
                        satFatLabel to formatNutrientValue(productData.saturatedFat, "g"),
                        transFatLabel to formatNutrientValue(productData.transFat, "g"),
                        cholesterolLabel to formatNutrientValue(productData.cholesterol, "mg"),
                        sodiumLabel to formatNutrientValue(productData.sodium, "mg"),
                        carbohydrateLabel to formatNutrientValue(productData.carbohydrate, "g"),
                        fiberLabel to formatNutrientValue(productData.dietaryFiber, "g"),
                        sugarLabel to formatNutrientValue(productData.sugar, "g"),
                        proteinLabel to formatNutrientValue(productData.proteins, "g"),
                        vitaminALabel to productData.vitaminA,
                        vitaminCLabel to productData.vitaminC,
                        vitaminDLabel to productData.vitaminD,
                        calciumLabel to productData.calcium,
                        ironLabel to productData.iron
                    ))
                }

                LaunchedEffect(key1 = barcode) {
                    viewModel.insertHistory(
                        History(
                            name = productData.name,
                            company = productData.company,
                            photoUrl = productData.photoUrl,
                            barcode = productData.barcode,
                            user_id = user.id,
                            dateAdded = formattedDate
                        )
                    )
                }

                when (val savedState = savedProductResult) {
                    is Result.Success -> {
                        val savedProductData = savedState.data
                        val foundSavedProduct = savedProductData.find { it.barcode == productData.barcode }
                        isSaved = foundSavedProduct != null
                        thisSavedProductId = foundSavedProduct?.id ?: 0
                    }
                    is Result.Error -> {
                        Toast.makeText(context, savedState.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = productData.photoUrl,
                            contentDescription = stringResource(id = R.string.product_img),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = Color.White
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-32).dp)
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = productData.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                when {
                                    productData.labelHalal.equals("Halal", ignoreCase = true) -> {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_halal_logo),
                                            contentDescription = "Logo Halal",
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(70.dp)
                                                .padding(start = 8.dp)
                                        )
                                    }
                                    productData.labelHalal.equals("Non-Halal", ignoreCase = true) -> {
                                        Text(
                                            text = "Non-Halal",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    productData.labelHalal.isBlank() -> {
                                        Text(
                                            text = "Belum ditemukan data sertifikasi produk.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (user.id != 0) {
                                    if (isSaved) {
                                        IconButton(onClick = {
                                            viewModel.deleteSavedProduct(thisSavedProductId)
                                            isSaved = false
                                            Toast.makeText(context, "Produk Dihapus dari Produk Tersimpan", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_bookmark_24),
                                                contentDescription = stringResource(id = R.string.save),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            viewModel.saveProduct(productData.name, productData.company, productData.photoUrl, productData.barcode, user.id)
                                            isSaved = true
                                            Toast.makeText(context, "Produk Disimpan", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
                                                contentDescription = stringResource(id = R.string.save)
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Product by ${productData.company}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(id = R.string.nutrition_facts),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            formattedNutrientData.forEach { (label, value) ->
                                NutritionData(label = label, value = value)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            DetailContentSection(productData)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        if (productData.information.isNotEmpty()) {
                            Text(
                                text = "Informasi Produk",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = productData.information,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        if (productData.keterangan.isNotEmpty()) {
                            Text(
                                text = "Keterangan Nutrisi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = productData.keterangan,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Text(
                            text = "Product by ${productData.company}",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            is Result.Error -> {
                loading = false
                Toast.makeText(context, (resultState as Result.Error).error, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
        LinearLoading(
            isLoading = loading,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

fun formatNutrientValue(value: String, defaultUnit: String): String {
    val hasUnit = value.any { it.isLetter() }
    return if (hasUnit) {
        value
    } else {
        "$value $defaultUnit"
    }
}

@Composable
fun DetailContentSection(productData: ProductModel) {
    var showNutrilevel by remember { mutableStateOf(false) }
    var nutrilevelLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showNutrilevelDialog by remember { mutableStateOf(false) }

    if (showNutrilevelDialog) {
        NutrilevelDialog(
            level = productData.nutritionLevel,
            productData = productData,
            onDismiss = { showNutrilevelDialog = false }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nutrilevel",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (showNutrilevel) {
            Text(
                text = productData.nutritionLevel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (productData.nutritionLevel) {
                    "A" -> Color(0xFF4CAF50)
                    "B" -> Color(0xFF8BC34A)
                    "C" -> Color(0xFFCDDC39)
                    "D" -> Color(0xFFFFEB3B)
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.clickable {
                    showNutrilevelDialog = true
                }
            )
        } else {
            if (nutrilevelLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Button(onClick = {
                    nutrilevelLoading = true
                    coroutineScope.launch {
                        delay(2000)
                        nutrilevelLoading = false
                        showNutrilevel = true
                    }
                }) {
                    Text("Lihat Level Nutrisi")
                }
            }
        }
    }
}

@Composable
fun NutrilevelDialog(level: String, productData: ProductModel, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    val akgCategories = listOf(
        R.string.akg_anak_anak_desc,
        R.string.akg_remaja_desc,
        R.string.akg_dewasa_desc,
        R.string.akg_lansia_desc
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Informasi Nutrilevel: $level",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            val descriptionResId = when (level) {
                "A" -> R.string.nutrilevel_a_desc
                "B" -> R.string.nutrilevel_b_desc
                "C" -> R.string.nutrilevel_c_desc
                "D" -> R.string.nutrilevel_d_desc
                else -> R.string.nutrilevel_unknown_desc
            }

            Column {
                Text(
                    text = stringResource(id = descriptionResId),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.akg_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    akgCategories.forEach { resId ->
                        Text(
                            text = stringResource(id = resId),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val annotatedString = buildAnnotatedString {
                    append(stringResource(id = R.string.nutrilevel_source))
                    pushStringAnnotation(
                        tag = "URL",
                        annotation = "https://peraturan.bpk.go.id/Details/138621/permenkes-no-28-tahun-2019"
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                        append(stringResource(id = R.string.permenkes_akg_title))
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            uriHandler.openUri(it.item)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun NutritionData(modifier: Modifier = Modifier, label: String, value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}