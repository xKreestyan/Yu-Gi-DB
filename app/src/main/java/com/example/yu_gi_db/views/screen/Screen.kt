package com.example.yu_gi_db.views.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel
import com.example.yu_gi_db.views.CardZoomView
import com.example.yu_gi_db.views.CardsScreenView
import com.example.yu_gi_db.views.ImageRotation
import com.example.yu_gi_db.views.InformationViewPreview
import com.example.yu_gi_db.views.InitCardsScreenView
import com.example.yu_gi_db.views.InitInformationView
import com.example.yu_gi_db.views.InitLargeCardView
import com.example.yu_gi_db.views.InitScaffoldView
import com.example.yu_gi_db.views.LargeCardUIContinuousSpellPreview
import com.example.yu_gi_db.views.MenuView
import com.example.yu_gi_db.views.ScaffoldView
import com.example.yu_gi_db.views.TopBarViewPreview_Full
import com.example.yu_gi_db.views.WaitIndicatorView
import com.example.yu_gi_db.views.navigation.Navigation

//test
@Composable
fun InitMainScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<CardListViewModel>()
    val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    if (isLoadingInitialData) {
        SplashScreen(modifier = modifier)
    }
    else {
        Navigation()
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // Accedi alla configurazione corrente
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Determina il fattore di altezza in base all'orientamento
    val heightFraction = if (isPortrait) 0.25f else 0.3f // Modificato per landscape a 0.3f

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
                    .padding(top = this.maxHeight / (2) + this.maxHeight / (10)),
                contentAlignment = Alignment.TopCenter
            ) {
                WaitIndicatorView(
                    modifier = Modifier.fillMaxHeight(heightFraction) // Applica il fattore dinamico
                )
            }
        }
    }
}
@Composable
fun MenuScreen(navController: NavHostController? = null) {
    MenuView(navController)
}

@Composable
fun DataBaseScreen(modifier: Modifier = Modifier, initialSearchCriteria: AdvancedSearchCriteria? = null, navController: NavHostController? = null) {
    InitScaffoldView(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.app_name),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            initialSearchCriteria = initialSearchCriteria
        )
    }
}


@Composable
fun LargeCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController? = null,
) {
    InitScaffoldView(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.card_detail_title_default),
        navController = navController
    ) { innerPadding ->
        InitLargeCardView(
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
    InitScaffoldView(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.zoomed_card_title),
        navController = navController
    ) { innerPadding ->
        CardZoomView(url, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun InformationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    InitScaffoldView(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.info_screen_title),
        navController = navController,
    ) { innerPadding ->
        InitInformationView(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun SavedCardsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
) {
    InitScaffoldView(
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



@Preview(showBackground = true, name = "Menu Screen Portrait")
@Composable
fun MenuScreenPreviewscreen() {
    YuGiDBTheme {
        MenuScreen()
    }
}
@Preview(showBackground = true)
@Composable
fun DataBaseScreen1Preview() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                val carteEsempio = listOf(
                    SmallPlayingCard(
                        id = 1,
                        name = "Exodia il Proibito",
                        imageUrlSmall = "url_exodia",
                        isFavorite = true
                    ),
                    SmallPlayingCard(
                        id = 2,
                        name = "Kuriboh",
                        imageUrlSmall = "url_kuriboh",
                        isFavorite = false
                    )
                )
                var criteri by remember { mutableStateOf(AdvancedSearchCriteria(name = "Test")) }
                CardsScreenView(
                    modifier = Modifier.padding(innerPadding),
                    cards = carteEsempio,
                    isLoading = false,
                    errorMessage = null,
                    searchCriteria = criteri,
                    onSearchCriteriaChange = { criteri = it },
                    onCardItemClick = {},
                    onToggleFavorite = {}
                )
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LargeCardScreenPreview() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)){
                LargeCardUIContinuousSpellPreview()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardZoomScreenPreview() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                CardZoomView(url = "", modifier = Modifier.padding(innerPadding))
            }
        )
    }
}


@Preview(showBackground = true, name = "InfoScreenView Preview")
@Composable
fun InfoScreenViewPreview() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                InformationViewPreview(modifier = Modifier.padding(innerPadding))
            }
        )
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - Empty")
@Composable
fun SavedCardsScreenEmptyPreview() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                val carteEsempio = listOf(
                    SmallPlayingCard(
                        id = 1,
                        name = "Exodia il Proibito",
                        imageUrlSmall = "url_exodia",
                        isFavorite = true
                    ),
                )
                var criteri by remember { mutableStateOf(AdvancedSearchCriteria(name = "Test")) }
                CardsScreenView(
                    modifier = Modifier.padding(innerPadding),
                    cards = carteEsempio,
                    isLoading = false,
                    errorMessage = null,
                    searchCriteria = criteri,
                    onSearchCriteriaChange = { criteri = it },
                    searchAdvancedBoolean = true
                )
            }
        )
    }
}
