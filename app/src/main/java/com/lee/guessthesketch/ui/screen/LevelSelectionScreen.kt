package com.lee.guessthesketch.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun LevelSelectScreen(
    onEasySelected : () -> Unit,
    onMediumSelected : () -> Unit,
    onHardSelected : () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Level", fontSize = 24.sp)
        Button(onClick = { onEasySelected() }) {
            Text(text = "Easy")
        }
        Button(onClick = { onMediumSelected() }) {
            Text(text = "Medium")
        }
        Button(onClick = { onHardSelected() }) {
            Text(text = "Hard")
        }
    }
}