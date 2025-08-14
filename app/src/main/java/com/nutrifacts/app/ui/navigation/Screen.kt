package com.nutrifacts.app.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("Landing")
    object Login : Screen("Login")
    object Signup : Screen("Daftar")
    object Home : Screen("Beranda")
    object Search : Screen("Cari")
    object History : Screen("History")
    object Scanner : Screen("Scanner")
    object Detail : Screen("Home/{barcode}") {
        fun createRoute(barcode: String) = "Home/$barcode"
    }
    object News : Screen("News/{newsId}") {
        fun createRoute(newsId: Int) = "News/$newsId"
    }
    object Profile : Screen("Profil")
    object Account : Screen("Akun")
    object Saved : Screen("Produk Tersimpan")
    object Notifications : Screen("Nontifikasi")
    object Settings : Screen("Pengaturan")
}
