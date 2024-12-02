package com.lee.guessthesketch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lee.guessthesketch.ui.screen.SketchScreen
import com.lee.guessthesketch.ui.screen.SketchViewModel
import com.lee.guessthesketch.ui.screen.MainScreen
import androidx.navigation.compose.composable
import com.lee.guessthesketch.ui.navigation.Screen
import com.lee.guessthesketch.ui.screen.AboutScreen
import com.lee.guessthesketch.ui.screen.LevelSelectScreen
import com.lee.guessthesketch.ui.screen.login.LoginScreen
import com.lee.guessthesketch.ui.theme.GuessTheSketchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuessTheSketchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onStartSelected = { navController.navigate(Screen.level.route) },
                onAboutSelected = { navController.navigate(Screen.about.route) },
            )
        }
        composable(Screen.level.route) {
            LevelSelectScreen(
                onEasySelected = { navController.navigate(Screen.easy.route) },
                onMediumSelected = { navController.navigate(Screen.medium.route) },
                onHardSelected = { navController.navigate(Screen.hard.route) },
            )
        }
        composable(Screen.about.route) {
            AboutScreen()
        }

        composable(Screen.easy.route) {
            SketchScreen(modifier =  Modifier.fillMaxSize(), difficulty = 1)
        }
        composable(Screen.medium.route) {
            SketchScreen(modifier =  Modifier.fillMaxSize(), difficulty = 2)
        }
        composable(Screen.hard.route) {
            SketchScreen(modifier =  Modifier.fillMaxSize(), difficulty = 3)
        }

    }
}



