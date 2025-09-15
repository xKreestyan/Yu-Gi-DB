package com.example.yu_gi_db.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel // Per hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Per collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel

//test
@Composable
fun InitMainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    val viewModel = hiltViewModel<CardListViewModel>()
    val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    if (isLoadingInitialData) {
        SplashScreen(modifier = modifier,navController)
    }
    else {
        Navigation()
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier, navController: NavHostController? = null) {
    Box {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
        ) {
            ImageRotation(
                R.drawable.yu_gi_oh_schermata_principale_v,
                R.drawable.yu_gi_oh_schermata_principale_o,
                Modifier.fillMaxSize()
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = this.maxHeight / 2),
                contentAlignment = Alignment.TopCenter
            ) {
                WaitIndicatorView(
                    modifier = Modifier .size(this@BoxWithConstraints.maxWidth / 3)
                )
            }
        }
    }
}
@Composable
fun MenuScreen1(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    MenuScreen(navController)
}

@Composable
fun DataBaseScreen1(modifier: Modifier = Modifier,initialSearchCriteria: AdvancedSearchCriteria? = null, navController: NavHostController? = null) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.app_name),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            initialSearchCriteria =initialSearchCriteria
        )
    }
}


@Composable
fun LargePlayingCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController? = null,
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.card_detail_title_default) ,
        navController = navController
    ) { innerPadding ->
        InitLargePlayingCard(
            modifier = Modifier.padding(innerPadding),
            cardId = cardId,
            navController = navController
        )
    }
}

@Composable
fun CardZoomScreen(
    url: String,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.zoomed_card_title),
        navController = navController
    ) { innerPadding ->
         CardZoomView(url,modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun InformationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.info_screen_title),
        navController = navController,
    ) { innerPadding ->
        InitInformationView(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SavedCardsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.saved_cards_title),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            initialSearchCriteria = AdvancedSearchCriteria(isFavorite = true)
        )
    }
}

/*----------------------------------------------------------------------------------------------------*/
// Preview functions

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    YuGiDBTheme {
        SplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DataBaseScreen1Preview() {
    YuGiDBTheme {
        DataBaseScreen1()
    }
}
@Preview(showBackground = true)
@Composable
fun CardZoomScreenPreview() {
    YuGiDBTheme {
        CardZoomScreen(url = "https://images.ygoprodeck.com/images/cards/34541863.jpg")
    }
}


@Preview(showBackground = true, name = "InfoScreenView Preview")
@Composable
fun InfoScreenViewPreview() {
    YuGiDBTheme {
      InformationScreen()
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - Empty")
@Composable
fun SavedCardsScreenEmptyPreview() {
    YuGiDBTheme {
        SavedCardsScreen(navController = null)
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - With Data")
@Composable
fun SavedCardsScreenWithDataPreview() {
    YuGiDBTheme {
        SavedCardsScreen(navController = null)
    }
}
