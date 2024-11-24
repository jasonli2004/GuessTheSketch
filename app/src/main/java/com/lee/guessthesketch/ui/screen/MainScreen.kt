package com.lee.guessthesketch.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lee.guessthesketch.R

@Composable
fun MainScreen(
    onStartSelected: () -> Unit,
    onAboutSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Guess The Sketch!", fontSize = 24.sp, fontFamily = FontFamily.SansSerif)

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            //just fit screen
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(horizontal = 50.dp)
        )

        Spacer(
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Button(onClick = { onStartSelected() }) {
            Text(text = "Start")
        }
        Button(onClick = { onAboutSelected() }) {
            Text(text = "About")
        }
    }
}
