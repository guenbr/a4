package com.neu.mobileapplicationdevelopment202430.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neu.mobileapplicationdevelopment202430.viewmodel.ProductViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    viewModel: ProductViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ProductList.route) {
            ProductListScreen(viewModel = viewModel)
        }
    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object ProductList : Screen("product_list")
}