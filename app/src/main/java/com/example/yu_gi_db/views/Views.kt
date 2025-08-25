package com.example.yu_gi_db.views

import android.util.Log // Importa Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue // Aggiunto import mancante
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
//import androidx.lifecycle.viewmodel.compose.viewModel // Rimosso se non più usato in CardsScreenView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel
import kotlinx.coroutines.delay



@Composable
fun InitMainScreen(modifier: Modifier = Modifier) {
    var splash = rememberSaveable { mutableStateOf(true) }
    YuGiDBTheme {
        val viewModel = hiltViewModel<CardListViewModel>()
        LaunchedEffect(Unit) {
            delay(2500)
            splash.value = false
        }
        if (splash.value) {
            SplashScreen()
        }
        else {
            MainScreen()

        }
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding -> // Use Scaffold for basic screen structure
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.screen_yu_gi_db),
                contentDescription = stringResource(id = R.string.splash_content_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize() // Example size
            )
            Box(modifier.padding()){
                WaitIndicatorView(modifier.padding(150.dp))
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
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.card_image_description, card.id), // Descrizione più significativa
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(260.dp, 350.dp)
            )
            Text(
                // text = "ID: ${card.id}", // Potresti voler mostrare il nome se disponibile
                text = card.id.toString(), // Mostra l'ID o il nome della carta
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
@Composable
fun SmallCardsListView(cards: List<SmallPlayingCard>, modifier: Modifier = Modifier) {
    // Log per confermare che stiamo per comporre la LazyVerticalGrid
    Log.d("CardsScreenView", "Displaying LazyVerticalGrid with ${cards.size} cards.")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(), // Riempi lo spazio disponibile nel Box
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Rimosso l'OutlinedTextField da qui
        items(cards, key = { card -> card.id }) { card ->
            SmallCardItemView(card = card)
        }
    }
}



@Composable
fun WaitIndicatorView(modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.loading_text), modifier = modifier.padding(bottom = 16.dp))
        CircularProgressIndicator(Modifier.fillMaxSize(0.5f))
    }

}
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
        textAlign = TextAlign.Justify,
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

    // Log per vedere quanti elementi contiene 'cards'
    Log.d("InitCardsScreenView", "Number of cards from ViewModel: ${cards.size}")

    val filteredCards = if (searchQuery.isBlank()) {
        cards
    } else {
        cards.filter {
            // Filtra per ID. Se avessi un campo 'name' in SmallPlayingCard, potresti usare:
            // it.name.contains(searchQuery, ignoreCase = true)
            it.id.toString().contains(searchQuery, ignoreCase = true)
        }
    }

    CardsScreenView(
        modifier = modifier,
        cards = filteredCards, // Passa la lista filtrata
        isLoading = isLoading,
        errorMessage = error,
        searchQuery = searchQuery, // Passa la query di ricerca
        onSearchQueryChange = { newQuery -> searchQuery = newQuery } // Passa la callback per aggiornare la query
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
    Log.d("CardsScreenView", "Number of cards received: ${cards.size}, isLoading: $isLoading, error: $errorMessage")// Log per vedere quanti elementi riceve CardsScreenView
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier.fillMaxSize()) { // Usa Column per organizzare TextField e il contenuto sottostante
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
               WaitIndicatorView()
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
    SplashScreen()
}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme { // Wrap with Theme for preview
        MainScreen()
    }
}



@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    MaterialTheme {
        SmallCardItemView(
            card = SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg")
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Populated")
@Composable
fun SmallCardsListPreview() {
    MaterialTheme {
        SmallCardsListView(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg"),
                SmallPlayingCard(id = 3, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6973839.jpg"),
                SmallPlayingCard(id = 4, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/69838399.jpg")

            )
        )
    }

}




@Preview(showBackground = true, name = "Saved Cards Content - Populated")
@Composable
fun CardsScreenPopulatedPreview() {
    MaterialTheme {
        CardsScreenView(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg"),
                SmallPlayingCard(id = 3, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6973839.jpg"),
                SmallPlayingCard(id = 4, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/69838399.jpg")

            ),
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }

}
@Preview(showBackground = true, name = "Saved Cards Content - Empty")
@Composable
fun CardsScreenEmptyPreview() {
    MaterialTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - After Search Empty")
@Composable
fun CardsScreenAfterSearchEmptyPreview() {
    MaterialTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchQuery = "XYZ", // Esempio di ricerca
            onSearchQueryChange = {}
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Loading")
@Composable
fun CardsScreenLoadingPreview() {
    MaterialTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Error")
@Composable
fun CardsScreenErrorPreview() {
    MaterialTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Network request failed",
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

