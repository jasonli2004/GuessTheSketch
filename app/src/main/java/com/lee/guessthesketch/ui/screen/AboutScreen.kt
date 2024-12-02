package com.lee.guessthesketch.ui.screen


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A11CB), // Rich Violet
                        Color(0xFF2575FC)  // Soft Blue
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "About Guess the Sketch",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Description
        Text(
            text = "Guess the Sketch is an engaging Android app built with Jetpack Compose and Kotlin, integrating the power of OpenAI's GPT-4 Vision Model. Test your creativity and AI's recognition skills in a fun sketching game!",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Features
        Text(
            text = "🚀 Features",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FeatureItem("Interactive Sketch Canvas: Players are given a word and must sketch their interpretation on the canvas.")
        FeatureItem("AI Recognition: Submit your sketch to the AI, which provides its top 5 guesses for what the drawing represents.")
        FeatureItem("Win Condition: If the given word is among the AI's guesses, the player wins!")
        FeatureItem("Difficulty Levels: Choose from different difficulty levels for a customized challenge.")
        FeatureItem("Leaderboards: Compete with friends and track your progress with cloud-based leaderboards. (Under development)")

        Spacer(modifier = Modifier.height(16.dp))

        // How to Play
        Text(
            text = "🤔 How to Play",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FeatureItem("1. The app assigns you a word to sketch.")
        FeatureItem("2. Draw your interpretation on the canvas.")
        FeatureItem("3. Submit your sketch to the AI.")
        FeatureItem("4. If the AI guesses your word in its top 5 responses, you win!")

//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Back Button
//        Button(
//            onClick = onBackClick,
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCA28)),
//            modifier = Modifier
//                .fillMaxWidth(0.6f)
//                .padding(vertical = 16.dp)
//        ) {
//            Text(text = "Back", fontWeight = FontWeight.Bold)
//        }
    }
}

@Composable
fun FeatureItem(feature: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = feature,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
        )
    }
}