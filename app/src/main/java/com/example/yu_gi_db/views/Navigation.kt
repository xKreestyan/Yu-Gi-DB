package com.example.yu_gi_db.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
sealed class Screen(
    val route: String // This string will now be the full route pattern, e.g., "CardScreen/{cardId}"
) {
    object SplashScreen : Screen("SplashScreen")
    object MainScreen : Screen("MainScreen")
    object CardScreen : Screen("CardScreen/{cardId}") { // route property is "CardScreen/{cardId}"
        const val ARG_CARD_ID = "cardId" // Define argument key as a constant
        fun createRoute(cardId: String): String {
            // Replaces the placeholder {cardId} with the actual cardId value
            return this.route.replace("{$ARG_CARD_ID}", cardId)
        }
    }

    object InfoScreen : Screen("InfoScreen")
    // Add other screens here
}
/*
  navController?.navigate(Screen.MainScreen.route) {
            // Opzione per pulire lo stack in modo che l'utente non torni alla SplashScreen premendo "indietro"
            popUpTo(Screen.SplashScreen.route) { inclusive = true }
        }

  // Example navigation to CardScreen:
  // navController?.navigate(Screen.CardScreen.createRoute("someActualCardId"))
*/
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(Screen.MainScreen.route) { // Uses Screen.MainScreen.route ("MainScreen")
            InitMainScreen(navController = navController)
        }

        composable(Screen.SplashScreen.route) { // Uses Screen.SplashScreen.route ("SplashScreen")
            SplashScreen(navController = navController)
        }


        composable(
            route = Screen.CardScreen.route, // Uses Screen.CardScreen.route ("CardScreen/{cardId}")
            arguments = listOf(navArgument(Screen.CardScreen.ARG_CARD_ID) { // Use the const for arg name
                type = NavType.StringType
                // nullable = false by default. Specify if it can be null or needs a default value.
            })
        ) { backStackEntry ->
            val cardIdString = backStackEntry.arguments?.getString(Screen.CardScreen.ARG_CARD_ID)
            val cardIdInt = cardIdString?.toIntOrNull() ?: -1 // Default a -1 se null o non valido

            InitLargePlayingCard( // Assumendo che questo sia il Composable corretto
                navController = navController,
                cardId = cardIdInt
            )

        }

        composable(Screen.InfoScreen.route) {

            // InfoScreenView(navController = navController)
        }
        // Add other composables here
    }
}
