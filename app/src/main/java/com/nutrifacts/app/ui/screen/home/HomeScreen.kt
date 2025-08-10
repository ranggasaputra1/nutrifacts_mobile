package com.nutrifacts.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val userSession =
        UserPreference.getInstance(LocalContext.current.dataStore).getSession().collectAsState(
            initial = UserModel(0, "", false)
        ).value

    val listState = rememberLazyListState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.8f),
                        backgroundColor
                    )
                )
            )
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 32.dp, bottom = 80.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Selamat datang kembali,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Pengguna!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fakta Nutrisi Hari Ini",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🥦",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Fakta Nutrisi Pertama
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color.Black.copy(alpha = 0.05f),
                                spotColor = Color.Black.copy(alpha = 0.05f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalDining,
                                        contentDescription = "Ikon nutrisi",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Tahukah Anda?",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Vitamin C tidak hanya ditemukan pada buah jeruk. Brokoli dan paprika merah juga kaya Vitamin C dan baik untuk kekebalan tubuh.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Tambahan Spacer untuk memberi jarak antara card
                    Spacer(modifier = Modifier.height(16.dp))

                    // Card Fakta Nutrisi Kedua
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color.Black.copy(alpha = 0.05f),
                                spotColor = Color.Black.copy(alpha = 0.05f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalDining,
                                        contentDescription = "Ikon nutrisi",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Fakta Menarik",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Makan siang dengan nasi, ayam, dan sayuran hijau memberikan energi stabil sepanjang sore dan menjaga fokus lebih baik.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}