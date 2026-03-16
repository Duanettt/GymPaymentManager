package com.duanett.gymmanager.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.duanett.gymmanager.presentation.customers.AddCustomerScreen
import com.duanett.gymmanager.presentation.customers.CustomerListScreen

private object Routes {
    const val CUSTOMER_LIST = "customer_list"
    const val ADD_CUSTOMER  = "add_customer"
}

@Composable
fun GymNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CUSTOMER_LIST
    ) {
        composable(Routes.CUSTOMER_LIST) {
            CustomerListScreen(
                onNavigateToAdd    = { navController.navigate(Routes.ADD_CUSTOMER) },
                onNavigateToDetail = { /* TODO: customer detail screen */ }
            )
        }

        composable(Routes.ADD_CUSTOMER) {
            AddCustomerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
