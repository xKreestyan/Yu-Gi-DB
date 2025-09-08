package com.example.yu_gi_db.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.views.Screen.ZoomCardScreen.ARG_IMAGE_URL

// This string will now be the full route pattern, e.g., "CardScreen/{cardId}"
sealed class Screen(val route: String) {
    object InitMainScreen : Screen("InitMainScreen")
    object SplashScreen : Screen("SplashScreen")
    object DataBaseScreen1 : Screen("DataBaseScreen1")
    object DataBaseAdvancedSearchType: Screen("DataBaseAdvancedSearchType/{type}"){
        const val ARG_TYPE = "type"
        fun createRoute(type: String): String {
            return this.route.replace("{$ARG_TYPE}", type)
        }
        /*fun createRouteAttribute( attribute: String): String {
            return this.route.replace("{$ARG_ATTRIBUTE}", attribute)
        }
        fun createRouteLivello(livello : Int): String {
            return this.route.replace("{$ARG_LIVERELO}", livello.toString())
        }*/
    }
    object DataBaseAdvancedSearchAttribute : Screen("DataBaseAdvancedSearchAttribute/{attribute}") {
        const val ARG_ATTRIBUTE = "attribute"
        fun createRoute(attribute: String): String {
            return this.route.replace("{$ARG_ATTRIBUTE}", attribute)
        }
    }
    object DataBaseAdvancedSearchLivello : Screen("DataBaseAdvancedSearchLivello/{Livello}"){
            const val ARG_LIVERELO = "Livello"
            fun createRoute( Livello: Int): String {
                return this.route.replace("{$ARG_LIVERELO}", Livello.toString())
            }


    }

    object MenuScreen1 : Screen("MenuScreen1")
    object InfoScreen : Screen("InfoScreen")
    object SavedCardsScreen : Screen("SavedCardsScreen")
    object CardScreen : Screen("CardScreen/{cardId}") { // route property is "CardScreen/{cardId}"
        const val ARG_CARD_ID = "cardId"
        fun createRoute(cardId: Int): String {
            return "CardScreen/$cardId"
        }
    }
    object ZoomCardScreen : Screen("cardZoom/{imageUrl}") { // Nuova route con placeholder
        const val ARG_IMAGE_URL = "imageUrl" // Chiave per l'argomento
        fun createRoute(imageUrl: String): String {
            return this.route.replace("{$ARG_IMAGE_URL}", imageUrl)
        }
    }



}
/*
  navController?.navigate(Screen.DataBaseScreen1.route) {
            // Opzione per pulire lo stack in modo che l'utente non torni alla SplashScreen premendo "indietro"
            popUpTo(Screen.SplashScreen.route) { inclusive = true }
        }

  // Example navigation to CardScreen:
  // navController?.navigate(Screen.CardScreen.createRoute(123)) // Example with Int
*/
@Composable
fun Navigation() {
    val navController = rememberNavController()
    YuGiDBTheme {
        NavHost(navController = navController, startDestination = Screen.MenuScreen1.route) {

            composable(Screen.InitMainScreen.route) { // Uses Screen.DataBaseScreen1.route ("DataBaseScreen1")
                InitMainScreen(navController = navController)
            }
            composable(Screen.SplashScreen.route) { // Uses Screen.SplashScreen.route ("SplashScreen")
                SplashScreen(navController = navController)
            }
            composable(Screen.DataBaseScreen1.route) { // Uses Screen.DataBaseScreen1.route ("DataBaseScreen1")
                DataBaseScreen1(navController = navController)
            }
            composable(
                route = Screen.DataBaseAdvancedSearchType.route,
                arguments = listOf(navArgument(Screen.DataBaseAdvancedSearchType.ARG_TYPE) { type = NavType.StringType })
            ) { backStackEntry ->
                DataBaseScreen1(navController = navController,
                    initialSearchCriteria = AdvancedSearchCriteria(type=  backStackEntry.arguments?.getString(Screen.DataBaseAdvancedSearchType.ARG_TYPE))
                )

            }
           composable(
                route = Screen.DataBaseAdvancedSearchAttribute.route,
                arguments = listOf(navArgument(Screen.DataBaseAdvancedSearchAttribute.ARG_ATTRIBUTE) { type = NavType.StringType })
            ) { backStackEntry ->
                DataBaseScreen1(navController = navController,
                    initialSearchCriteria = AdvancedSearchCriteria(attribute=  backStackEntry.arguments?.getString(Screen.DataBaseAdvancedSearchAttribute.ARG_ATTRIBUTE))
                )

            }
            composable(
                route = Screen.DataBaseAdvancedSearchLivello.route,
                arguments = listOf(navArgument(Screen.DataBaseAdvancedSearchLivello.ARG_LIVERELO) { type = NavType.StringType })
            ) { backStackEntry ->
                DataBaseScreen1(navController = navController,
                    initialSearchCriteria = AdvancedSearchCriteria(level= ( (backStackEntry.arguments?.getString(Screen.DataBaseAdvancedSearchLivello.ARG_LIVERELO)) ?:"").toInt()  )
                )

            }




            composable(Screen.MenuScreen1.route) { // Uses Screen.DataBaseScreen1.route ("DataBaseScreen1")
                MenuScreen1(navController = navController)
            }
            composable(Screen.InfoScreen.route) {
                InformationScreen(navController = navController)
            }
            composable(Screen.SavedCardsScreen.route) {
                SavedCardsScreen(navController = navController)
            }
            composable(
                route = Screen.CardScreen.route, // Uses Screen.CardScreen.route ("CardScreen/{cardId}")
                arguments = listOf(navArgument(Screen.CardScreen.ARG_CARD_ID) { // Use the const for arg name
                    type = NavType.IntType // nullable = false by default. Specify if it can be null or needs a default value.
                })
            )
            { backStackEntry ->
                InitLargePlayingCardScreen( // Assumendo che questo sia il Composable corretto
                    navController = navController,
                    cardId =backStackEntry.arguments?.getInt(Screen.CardScreen.ARG_CARD_ID) ?:-1
                )

            }

            composable(
                route = Screen.ZoomCardScreen.route, // Usa la nuova route "cardZoom/{imageUrl}"
                arguments = listOf(navArgument(Screen.ZoomCardScreen.ARG_IMAGE_URL) {
                    type = NavType.StringType
                })
            )
            { backStackEntry ->
                val imageUrl = backStackEntry.arguments?.getString(Screen.ZoomCardScreen.ARG_IMAGE_URL)
                    CardZoomScreen(url = imageUrl ?: "", navController = navController)
            }
            // Add other composables here
        }
    }
}
