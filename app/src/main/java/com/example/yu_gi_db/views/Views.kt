package com.example.yu_gi_db.views

import android.util.Log // Importa Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints // Aggiunto per SplashScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel


@Composable
fun InitMainScreen(modifier: Modifier = Modifier) {
    YuGiDBTheme {
        val viewModel = hiltViewModel<CardListViewModel>()
        val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()

        if (isLoadingInitialData) {
            SplashScreen(modifier = modifier)
        }
        else {
            MainScreen(modifier = modifier)
        }
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        BoxWithConstraints( // Modificato per usare BoxWithConstraints
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.screen_yu_gi_db),
                contentDescription = stringResource(id = R.string.splash_content_description),
                contentScale = ContentScale.FillBounds, // o Crop
                modifier = Modifier.fillMaxSize()
            )
            // Box per posizionare WaitIndicatorView al centro della metà inferiore
            Box(
                modifier = Modifier
                    .fillMaxSize() // Prende tutto lo spazio per un allineamento più semplice
                    .padding(bottom = this.maxHeight / 8), // Sposta il centro verso l'alto dalla metà inferiore
                contentAlignment = Alignment.BottomCenter
            ) {
                WaitIndicatorView(
                    Modifier.size(this@BoxWithConstraints.maxWidth / 3) // Usa maxWidth del genitore
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SmallCardItemView(card: SmallPlayingCard, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.imageUrlSmall)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground), // Considera placeholder specifici
                error = painterResource(R.drawable.ic_launcher_background), // Considera error placeholder specifici
                contentDescription = stringResource(R.string.card_image_description, card.id),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(260.dp, 350.dp) // Dimensioni fisse, valuta se renderle dinamiche
            )
            Text(
                text = card.id.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SmallCardsListView(cards: List<SmallPlayingCard>, modifier: Modifier = Modifier) {
    Log.d("CardsScreenView", "Displaying LazyVerticalGrid with ${cards.size} cards.")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { card -> card.id }) { card ->
            SmallCardItemView(card = card)
        }
    }
}

// MODIFICA APPLICATA QUI
@Composable
fun WaitIndicatorView(modifier: Modifier = Modifier){
    CircularProgressIndicator(
        modifier = modifier, // Il modifier passato viene applicato qui
        color = MaterialTheme.colorScheme.surface, // Colore per migliore visibilità
        strokeWidth = 6.dp // Spessore del tratto per migliore visibilità
    )
}
// FINE MODIFICA

@Composable
fun ErrorMessageView(text: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Justify, // Considera TextAlign.Center se più appropriato
        modifier = Modifier.padding(16.dp)
    )
    }
}

@Composable
fun InitCardsScreenView(modifier: Modifier = Modifier, cardListViewModel: CardListViewModel = hiltViewModel()) {
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

    CardsScreenView(
        modifier = modifier,
        cards = filteredCards,
        isLoading = isLoading,
        errorMessage = error,
        searchQuery = searchQuery,
        onSearchQueryChange = { newQuery -> searchQuery = newQuery }
    )
}

@Composable
private fun CardsScreenView(
    modifier: Modifier = Modifier,
    cards: List<SmallPlayingCard>,
    isLoading: Boolean,
    errorMessage: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
){ 
    Log.d("CardsScreenView", "Number of cards received: ${cards.size}, isLoading: $isLoading, error: $errorMessage")
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier.fillMaxSize()) { 
            OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text(stringResource(R.string.search_bar_label)) },
            singleLine = true,
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            })
        )
            if (isLoading) {
               WaitIndicatorView() // Ora WaitIndicatorView non ha testo e ha uno stile diverso
            }
            else if (errorMessage != null) {
                ErrorMessageView(stringResource(R.string.error_message_generic)+": $errorMessage")
            }
            else if (cards.isEmpty()) {
                Log.d("CardsScreenView", "Displaying 'No cards found or saved' message.")
                ErrorMessageView( if (searchQuery.isNotBlank()) stringResource(R.string.no_cards_found_search) else stringResource(R.string.no_cards_saved))
            }
            else {
                SmallCardsListView(cards = cards)
            }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    YuGiDBTheme { // Aggiunto Theme per coerenza con altre preview
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
fun CardItemPreview() {
    YuGiDBTheme {
        SmallCardItemView(
            card = SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg")
        )
    }
}

@Preview(showBackground = true, name = "SmallCardsList - Populated")
@Composable
fun SmallCardsListPreview() {
    YuGiDBTheme {
        SmallCardsListView(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg")
            )
        )
    }
}

// Preview per WaitIndicatorView modificato
@Preview(showBackground = true, name = "WaitIndicatorView (New)")
@Composable
fun WaitIndicatorViewPreviewNew() {
    YuGiDBTheme {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center){ // Box per dare una dimensione
            WaitIndicatorView(Modifier.fillMaxSize(0.8f)) // Esempio di utilizzo con modifier
        }
    }
}

@Preview(showBackground = true, name = "CardsScreen - Populated")
@Composable
fun CardsScreenPopulatedPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg")
            ),
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Empty")
@Composable
fun CardsScreenEmptyPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Loading")
@Composable
fun CardsScreenLoadingPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true, // Qui WaitIndicatorView (modificato) verrebbe mostrato
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Error")
@Composable
fun CardsScreenErrorPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Network request failed",
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
