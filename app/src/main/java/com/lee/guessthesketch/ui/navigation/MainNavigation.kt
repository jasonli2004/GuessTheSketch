package com.lee.guessthesketch.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object about : Screen("about")
    object level : Screen("level")
    object easy : Screen("easy")
    object medium : Screen("medium")
    object hard : Screen("hard")
}