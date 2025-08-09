package com.nutrifacts.app.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("Landing")
    object Login : Screen("Login")
    object Signup : Screen("Signup")
    object Home : Screen("Home")
    object Search : Screen("Search")
    object History : Screen("History")
    object Scanner : Screen("Scanner")
    object Detail : Screen("Home/{barcode}") {
        fun createRoute(barcode: String) = "Home/$barcode"
    }
    object News : Screen("News/{newsId}") {
        fun createRoute(newsId: Int) = "News/$newsId"
    }
    object Profile : Screen("Profile")
    object Account : Screen("Account")
    object Saved : Screen("Saved Products")
    object Notifications : Screen("Notifications")
    object Settings : Screen("Settings")
}
