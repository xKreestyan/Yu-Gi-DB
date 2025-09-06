package com.example.yu_gi_db.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.ui.theme.MenuScreen
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel



@Composable
fun InitMainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    val viewModel = hiltViewModel<CardListViewModel>()
    val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    if (isLoadingInitialData) {
        SplashScreen(modifier = modifier,navController)
    }
    else {
        //MainScreen(modifier = modifier,navController)

        Navigation()
        //navController?.navigate(Screen.MainScreen.route)
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    Box{
        BoxWithConstraints(
            modifier = modifier
                .fillMaxHeight()
        ) {
            ImageRotation(R.drawable.yu_gi_oh_schermata_principale_v ,R.drawable.yu_gi_oh_schermata_principale_o,modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = this.maxHeight / 8),
                contentAlignment = Alignment.BottomCenter
            ) {
                WaitIndicatorView( // Assicurati che sia definita e importata
                    Modifier.size(this@BoxWithConstraints.maxWidth / 5) // <<< DIMENSIONE MODIFICATA
                )
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.app_name),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView( // Assicurati che sia definita e importata
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@Composable
fun Main1Screen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    MenuScreen(navController)
}


@Composable
fun InitLargePlayingCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController?=null,
    viewModel: CardListViewModel = hiltViewModel()
) {
    val largeCard by viewModel.selectedLargeCard.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingLargeCard.collectAsStateWithLifecycle()
    val error by viewModel.largeCardError.collectAsStateWithLifecycle()

    LaunchedEffect(cardId) {
        viewModel.fetchLargeCardById(cardId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedLargeCard()
        }
    }

    AppScreen(
        modifier = modifier,
        appBarTitle = largeCard?.name ?: stringResource(id = R.string.card_detail_title_default),
        navController = navController
    ) { innerPadding ->
        if(optionErrorView( // Assicurati che sia definita e importata
                modifier = modifier.padding(innerPadding),
                isLoading = isLoading,
                errorMessage = error,
                isEmpty = (largeCard == null)
            ))
        {
            LargeCardItemView(modifier.padding(innerPadding),card = largeCard, navController = navController) // Assicurati che sia definita e importata
        }
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.card_image_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoSection( // Assicurati che sia definita e importata
                title = stringResource(R.string.info_section_about_title)
            ) {
                Text(
                    text = stringResource(R.string.info_section_about_content),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSection(
                title = stringResource(R.string.info_section_version_title)
            ) {
                Text(
                    text = stringResource(R.string.version),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSection(
                title = stringResource(R.string.info_section_developer_title)
            ) {
                Text(
                    text = stringResource(R.string.name_and_company),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSection(
                title = stringResource(R.string.info_section_credits_title)
            ) {
                Text(
                    text = stringResource(R.string.info_section_credits_content),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SavedCardsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    cardListViewModel: CardListViewModel = hiltViewModel()
) {
    /*
    val savedCards by viewModel.savedCardsList.collectAsStateWithLifecycle() // Assicurati che savedCardsList esista nel ViewModel
    val isLoading by viewModel.isLoadingSavedCards.collectAsStateWithLifecycle() // Assicurati che isLoadingSavedCards esista

    LaunchedEffect(Unit) {
        viewModel.fetchSavedCards() // Assicurati che fetchSavedCards esista
    }*/
    val cards by cardListViewModel.smallCards.collectAsStateWithLifecycle()
    val isLoading by cardListViewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    val error by cardListViewModel.initialDataError.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Log.d("InitCardsScreenView", "Number of cards from ViewModel: ${cards.size}")

    val filteredCards = if (searchQuery.isBlank()) {
        cards
    } else {
        cards.filter {
            it.id.toString().contains(searchQuery, ignoreCase = true)
        }
    }

    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.saved_cards_title), // Aggiungi questa stringa a strings.xml
        navController = navController
    ) { innerPadding ->
        if (optionErrorView(
                modifier = Modifier.padding(innerPadding), // optionErrorView si posizionerà all'interno di questo Box
                isLoading = isLoading,
                isEmpty = cards.isEmpty(),
                errorMessage = error,
                //isNotBlank = false // Per usare R.string.no_cards_saved quando isEmpty
            )
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), // La griglia riempie il Box con padding
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards, key = { card -> card.id }) { card ->
                    // Assumendo che SmallPlayingCardView esista e accetti questi parametri
                    // e che SmallPlayingCard abbia un campo 'name' e 'card_images'
                    SmallCardItemView(
                        card = card,
                        navController = navController
                    )
                }
            }
        }
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
fun MainScreenPreview() {
    YuGiDBTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CardZoomScreenPreview() {
    YuGiDBTheme {
        CardZoomScreen(url = "")
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
        // Per una preview "vera", potresti voler usare un ViewModel fake
        // che espone una lista vuota e isLoading = false
        SavedCardsScreen(navController = null /* o un NavController fake */)
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - With Data")
@Composable
fun SavedCardsScreenWithDataPreview() {
    YuGiDBTheme {
        // Per una preview "vera", potresti voler usare un ViewModel fake
        // che espone dati di esempio e isLoading = false
        // Ad esempio, creando un CardListViewModelFake
        SavedCardsScreen(navController = null /* o un NavController fake */)
    }
}

// Assicurati che le seguenti Composable siano definite e accessibili:
// - StandardTopAppBar (in Views.kt o altrove)
// - WaitIndicatorView
// - InitCardsScreenView
// - optionErrorView
// - LargeCardItemView
// - InfoSection
// - SmallPlayingCardView
// E che le classi modello come SmallPlayingCard e CardImage siano corrette.
// Ricorda di aggiungere le stringhe mancanti a strings.xml:
// R.string.saved_cards_title (es. "Carte Salvate")
// R.string.no_saved_cards (es. "Nessuna carta salvata.")
