package com.nutrifacts.app.ui.screen.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrifacts.app.R
import com.nutrifacts.app.ui.theme.RedApple
import com.nutrifacts.app.ui.theme.YellowApple
import com.nutrifacts.app.ui.components.GradientButton

@Composable
fun LandingScreen(modifier: Modifier = Modifier, navigateToLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.food_products),
            contentDescription = "food_illustration",
            modifier = modifier.size(300.dp)
        )
        Spacer(modifier = modifier.height(16.dp))
        Text(
            stringResource(R.string.landing_welcome),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineLarge,
            modifier = modifier.width(300.dp)
        )
        Spacer(modifier = modifier.height(60.dp))
        GradientButton(
            text = stringResource(R.string.get_started),
            textColor = Color.Black,
            gradient = Brush.horizontalGradient(
                colors = listOf(
                    RedApple,
                    YellowApple
                ),
                startX = 50f,
            ),
            onClick = navigateToLogin
        )
    }
}

@Composable
fun NutriButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = { onClick() },
        modifier
            .width(300.dp)
            .padding(vertical = 16.dp)
    ) {
        Text(stringResource(R.string.get_started), style = MaterialTheme.typography.titleMedium)
    }
}