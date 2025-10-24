package com.application.transactions.navigation

import com.application.transactions.presentation.ui.TransactionsScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.transactions(randomUserId()),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Destinations.TRANSACTIONS_ROUTE,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) {
            TransactionsScreen()
        }
    }
}

private val demoUserIds = listOf(
    10000, // no transactions
    10010, // single pending
    10011, // grouped pending
    10012, // single completed
    10013, // grouped completed
    10014, // single pending + completed
    10015, // grouped pending + completed
    100001 // error
)

fun randomUserId(): Int = demoUserIds.random()
