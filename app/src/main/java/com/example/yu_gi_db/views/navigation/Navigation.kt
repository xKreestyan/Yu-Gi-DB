package com.example.yu_gi_db.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.views.screen.CardZoomScreen
import com.example.yu_gi_db.views.screen.DataBaseScreen
import com.example.yu_gi_db.views.screen.InformationScreen
import com.example.yu_gi_db.views.screen.InitMainScreen
import com.example.yu_gi_db.views.screen.LargeCardScreen
import com.example.yu_gi_db.views.screen.MenuScreen
import com.example.yu_gi_db.views.screen.SavedCardsScreen
import com.example.yu_gi_db.views.screen.SplashScreen

// This string will now be the full route pattern, e.g., "CardScreen/{cardId}"
sealed class Screen(val route: String) {
    object InitMainScreen : Screen("InitMainScreen")
    object SplashScreen : Screen("SplashScreen")
    object DataBaseScreen1 : Screen("DataBaseScreen1")

    // New consolidated advanced search screen object
    object DataBaseAdvancedSearch : Screen("DataBaseAdvancedSearch/{searchType}/{searchValue}") {
        const val ARG_SEARCH_TYPE = "searchType"
        const val ARG_SEARCH_VALUE = "searchValue"

        // Main function to create the route
        fun createRoute(searchType: String, searchValue: String): String {
            return route.replace("{$ARG_SEARCH_TYPE}", searchType)
                        .replace("{$ARG_SEARCH_VALUE}", searchValue)
        }

        fun createRouteForType(type: String): String {
            return createRoute("type", type)
        }

        fun createRouteForAttribute(attribute: String): String {
            return createRoute("attribute", attribute)
        }

        fun createRouteForLevel(level: Int): String {
            return createRoute("level", level.toString())
        }

        fun createRouteForId(id: String): String { // Assuming ID is passed as a string
            return createRoute("id", id)
        }

        fun createRouteForName(name: String): String {
            return createRoute("name", name)
        }

        fun createRouteForAtkMin(atkMin: Int): String {
            return createRoute("atkMin", atkMin.toString())
        }

        fun createRouteForAtkMax(atkMax: Int): String {
            return createRoute("atkMax", atkMax.toString())
        }

        fun createRouteForDefMin(defMin: Int): String {
            return createRoute("defMin", defMin.toString())
        }

        fun createRouteForDefMax(defMax: Int): String {
            return createRoute("defMax", defMax.toString())
        }

        fun createRouteForIsFavorite(isFavorite: Boolean): String {
            return createRoute("isFavorite", isFavorite.toString())
        }

        fun createRouteForSetName(setName: String): String {
            return createRoute("setName", setName)
        }

        fun createRouteForSetCode(setCode: String): String {
            return createRoute("setCode", setCode)
        }
    }

    object MenuScreen1 : Screen("MenuScreen1")
    object InfoScreen : Screen("InfoScreen")
    object SavedCardsScreen : Screen("SavedCardsScreen")
    object CardScreen : Screen("CardScreen/{cardId}") {
        const val ARG_CARD_ID = "cardId"
        fun createRoute(cardId: Int): String {
            return "CardScreen/$cardId"
        }
    }
    object ZoomCardScreen : Screen("cardZoom/{imageUrl}") {
        const val ARG_IMAGE_URL = "imageUrl"
        fun createRoute(imageUrl: String): String {
            return this.route.replace("{$ARG_IMAGE_URL}", imageUrl)
        }
    }
}

/*
  navController?.navigate(Screen.DataBaseScreen1.route) {
            popUpTo(Screen.SplashScreen.route) { inclusive = true }
        }

  // Example navigation to CardScreen:
  // navController?.navigate(Screen.CardScreen.createRoute(123))
  // Example navigation to DataBaseAdvancedSearch:
  // navController?.navigate(Screen.DataBaseAdvancedSearch.createRoute("type", "Spell Card"))
  // navController?.navigate(Screen.DataBaseAdvancedSearch.createRoute("attribute", "DARK"))
  // navController?.navigate(Screen.DataBaseAdvancedSearch.createRoute("level", "4"))
*/
@Composable
fun Navigation() {
    val navController = rememberNavController()
    YuGiDBTheme {
        NavHost(navController = navController, startDestination = Screen.MenuScreen1.route) {

            composable(Screen.InitMainScreen.route) {
                InitMainScreen(navController = navController)
            }
            composable(Screen.SplashScreen.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.DataBaseScreen1.route) {
                // This is for direct navigation to DataBaseScreen1 without pre-filled search
                DataBaseScreen(navController = navController)
            }

            // New composable for consolidated advanced search
            composable(
                route = Screen.DataBaseAdvancedSearch.route,
                arguments = listOf(
                    navArgument(Screen.DataBaseAdvancedSearch.ARG_SEARCH_TYPE) { type = NavType.StringType },
                    navArgument(Screen.DataBaseAdvancedSearch.ARG_SEARCH_VALUE) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val searchType = backStackEntry.arguments?.getString(Screen.DataBaseAdvancedSearch.ARG_SEARCH_TYPE)
                val searchValue = backStackEntry.arguments?.getString(Screen.DataBaseAdvancedSearch.ARG_SEARCH_VALUE)
                var criteria = AdvancedSearchCriteria() // Default empty criteria

                if (searchType != null && searchValue != null) {
                    criteria = when (searchType) {
                        "type" -> AdvancedSearchCriteria(type = searchValue)
                        "attribute" -> AdvancedSearchCriteria(attribute = searchValue)
                        "level" -> AdvancedSearchCriteria(level = searchValue.toIntOrNull())
                        "id" -> AdvancedSearchCriteria(idQuery = searchValue)
                        "name" -> AdvancedSearchCriteria(name = searchValue)
                        "atkMin" -> AdvancedSearchCriteria(atkMin = searchValue.toIntOrNull())
                        "atkMax" -> AdvancedSearchCriteria(atkMax = searchValue.toIntOrNull())
                        "defMin" -> AdvancedSearchCriteria(defMin = searchValue.toIntOrNull())
                        "defMax" -> AdvancedSearchCriteria(defMax = searchValue.toIntOrNull())
                        "isFavorite" -> AdvancedSearchCriteria(isFavorite = searchValue.toBooleanStrictOrNull())
                        "setName" -> AdvancedSearchCriteria(setNameQuery = searchValue)
                        "setCode" -> AdvancedSearchCriteria(setCodeQuery = searchValue)
                        else -> AdvancedSearchCriteria() // Or handle as an error/default
                    }
                }
                DataBaseScreen(
                    navController = navController,
                    initialSearchCriteria = criteria
                )
            }

            composable(Screen.MenuScreen1.route) {
                MenuScreen(navController = navController)
            }
            composable(Screen.InfoScreen.route) {
                InformationScreen(navController = navController)
            }
            composable(Screen.SavedCardsScreen.route) {
                SavedCardsScreen(navController = navController)
            }
            composable(
                route = Screen.CardScreen.route,
                arguments = listOf(navArgument(Screen.CardScreen.ARG_CARD_ID) {
                    type = NavType.IntType
                })
            ) { backStackEntry ->
                LargeCardScreen(
                    navController = navController,
                    cardId = backStackEntry.arguments?.getInt(Screen.CardScreen.ARG_CARD_ID) ?: -1
                )
            }

            composable(
                route = Screen.ZoomCardScreen.route,
                arguments = listOf(navArgument(Screen.ZoomCardScreen.ARG_IMAGE_URL) {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val imageUrl = backStackEntry.arguments?.getString(Screen.ZoomCardScreen.ARG_IMAGE_URL)
                CardZoomScreen(
                    url = imageUrl ?: "",
                    navController = navController
                )
            }
        }
    }
}
