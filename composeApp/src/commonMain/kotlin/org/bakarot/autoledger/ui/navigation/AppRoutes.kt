package org.bakarot.autoledger.ui.navigation

sealed class AppRoutes(val route: String) {
    data object Dashboard : AppRoutes("dashboard")

    data object Transactions : AppRoutes("transactions")

    data object Charts : AppRoutes("charts")

    data object Settings : AppRoutes("settings")
}
