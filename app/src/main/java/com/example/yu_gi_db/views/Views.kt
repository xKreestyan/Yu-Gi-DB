package com.example.yu_gi_db.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController // Importa NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel


@Composable
fun InitMainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    YuGiDBTheme {
        val viewModel = hiltViewModel<CardListViewModel>()
        val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()

        if (isLoadingInitialData) {
            SplashScreen(modifier = modifier,navController)
        }
        else {
            MainScreen(modifier = modifier,navController)
        }
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
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
fun MainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
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
            modifier = Modifier.padding(innerPadding),
            navController = navController // Passa navController se InitCardsScreenView lo accetta
        )
    }
}

@Composable
fun SmallCardItemView(
    card: SmallPlayingCard,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null // Aggiunto NavController
) {
    Card(modifier = modifier.padding(8.dp),
        onClick ={
            // Dovresti definire una route specifica per i dettagli della carta, es. Screen.CardDetailScreen.route
            // e potenzialmente passare l'ID della carta come argomento.
            // Per ora, userò la navigazione placeholder che avevi, assicurandoti che usi il navController corretto:
            navController?.navigate(Screen.CardScreen.createRoute(card.id.toString())) { // Esempio: naviga a MainScreen

            }
        }
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
fun SmallCardsListView(
    cards: List<SmallPlayingCard>,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null // Aggiunto per passarlo a SmallCardItemView
) {
    Log.d("CardsScreenView", "Displaying LazyVerticalGrid with ${cards.size} cards.")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { card -> card.id }) { card ->
            SmallCardItemView(card = card, navController = navController) // Passa navController
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitLargePlayingCard(
    cardId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null, // Utile per la navigazione Indietro
    viewModel: CardListViewModel = hiltViewModel()
) {
    val largeCard by viewModel.selectedLargeCard.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingLargeCard.collectAsStateWithLifecycle()
    val error by viewModel.largeCardError.collectAsStateWithLifecycle()

    LaunchedEffect(cardId) {
        Log.d("InitLargePlayingCard", "LaunchedEffect triggered for cardId: $cardId")
        viewModel.fetchLargeCardById(cardId)
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d(
                "InitLargePlayingCard",
                "DisposableEffect onDispose triggered. Clearing selected card."
            )
            viewModel.clearSelectedLargeCard()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        largeCard?.name ?: stringResource(id = R.string.card_detail_title_default)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    if (navController?.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.card_detail_title_default)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> WaitIndicatorView(Modifier.size(100.dp))
                error != null -> ErrorMessageView(
                    text = error ?: stringResource(R.string.error_message_generic)
                )

                largeCard != null -> LargeCardDetailDisplayView(
                    card = largeCard!!,
                    modifier = Modifier.fillMaxSize()
                )

                else -> Text("Nessuna carta da visualizzare.") // Stato per quando non c'è nulla, né errore né caricamento
            }
        }
    }
}

@Composable
fun LargeCardDetailDisplayView(card: LargePlayingCard, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Per contenuti lunghi
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = card.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Immagine della carta (cropped)
        card.cardImages.firstOrNull()?.imageUrlCropped?.let { imageUrl ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.card_image_description, card.id),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adatta la larghezza ma non riempie tutto
                    .padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Tipo e Razza
        Text(
            text = "Type: ${card.type} / ${card.race}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Attributo e Livello (se applicabile)
        card.attribute?.let {
            Text(
                text = "Attribute: $it",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        card.level?.let {
            Text(
                text = "Level/Rank: $it",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // ATK/DEF (se applicabile)
        if (card.atk != null || card.def != null) {
            val atkText = card.atk?.toString() ?: "N/A"
            val defText = card.def?.toString() ?: "N/A"
            Text(text = "ATK: $atkText / DEF: $defText", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Descrizione
        Text(
            text = card.desc,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/*fun LargePlayingCard(card: String?, modifier: Modifier = Modifier, navController: NavHostController? = null) { // cardId rinominato in card, e tipo cambiato in String?
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
        if (card != null) { // Gestisce la nullabilità di card
            Text(text = card, modifier = Modifier.padding(innerPadding))
        } else {
            // Puoi mostrare un messaggio specifico o un Text vuoto se card è null
            Text(text = "ID Carta non disponibile.", modifier = Modifier.padding(innerPadding))
        }
        // SmallCardItemView(card = card,modifier = Modifier.padding(innerPadding), navController = navController)
    }
}
*/

@Composable
fun WaitIndicatorView(modifier: Modifier = Modifier){
    CircularProgressIndicator(
        modifier = modifier, // Il modifier passato viene applicato qui
        color = MaterialTheme.colorScheme.surface, // Colore per migliore visibilità
        strokeWidth = 6.dp // Spessore del tratto per migliore visibilità
    )
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
        textAlign = TextAlign.Justify, // Considera TextAlign.Center se più appropriato
        modifier = Modifier.padding(16.dp)
    )
    }
}

@Composable
fun InitCardsScreenView(
    modifier: Modifier = Modifier,
    cardListViewModel: CardListViewModel = hiltViewModel(),
    navController: NavHostController? = null // Aggiunto per riceverlo e passarlo a CardsScreenView
) {
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
        onSearchQueryChange = { newQuery -> searchQuery = newQuery },
        navController = navController // Passa navController
    )
}

@Composable
private fun CardsScreenView(
    modifier: Modifier = Modifier,
    cards: List<SmallPlayingCard>,
    isLoading: Boolean,
    errorMessage: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    navController: NavHostController? = null // Aggiunto per riceverlo e passarlo a SmallCardsListView
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
               WaitIndicatorView(modifier.align(Alignment.CenterHorizontally).fillMaxSize(0.7f)) // Ora WaitIndicatorView non ha testo e ha uno stile diverso
            }
            else if (errorMessage != null) {
                ErrorMessageView(stringResource(R.string.error_message_generic)+": $errorMessage")
            }
            else if (cards.isEmpty()) {
                Log.d("CardsScreenView", "Displaying 'No cards found or saved' message.")
                ErrorMessageView( if (searchQuery.isNotBlank()) stringResource(R.string.no_cards_found_search) else stringResource(R.string.no_cards_saved))
            }
            else {
                SmallCardsListView(cards = cards, navController = navController) // Passa navController
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
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
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
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
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
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Loading")
@Composable
fun CardsScreenLoadingPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true,
            errorMessage = null,
            searchQuery = "",
            onSearchQueryChange = {}
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
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
            errorMessage = "Failed to load cards. Please try again.",
            searchQuery = "",
            onSearchQueryChange = {}
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - No Cards Saved")
@Composable
fun CardsScreenNoCardsSavedPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchQuery = "", // Stringa di ricerca vuota
            onSearchQueryChange = {}
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - No Cards Found Search")
@Composable
fun CardsScreenNoCardsFoundSearchPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchQuery = "nonExistentCard", // Stringa di ricerca non vuota
            onSearchQueryChange = {}
            // Non passiamo navController nelle preview statiche se non necessario per la UI base
        )
    }
}

@Preview(showBackground = true, name = "LargeCardDetail - Populated")
@Composable
fun LargeCardDetailPreview() {
    YuGiDBTheme {
        LargeCardDetailDisplayView(
            card = LargePlayingCard(
                id = 12345,
                name = "Drago Bianco Occhi Blu",
                type = "Mostro Normale",
                desc = "Questo drago leggendario è un potente motore di distruzione. Praticamente invincibile, sono in pochi ad aver fronteggiato questa creatura ed essere sopravvissuti per raccontarlo.",
                atk = 3000,
                def = 2500,
                level = 8,
                race = "Drago",
                attribute = "LUCE",
                cardSets = emptyList(),
                cardImages = listOf(
                    com.example.yu_gi_db.model.CardImage(
                        id = 1,
                        imageUrl = "https://images.ygoprodeck.com/images/cards/89631139.jpg",
                        imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/89631139.jpg",
                        imageUrlCropped = "https://images.ygoprodeck.com/images/cards_cropped/89631139.jpg"
                    )
                ),
                cardPrices = emptyList(),
                typeline = emptyList(),
                frameType = "Card",
                humanReadableCardType = "Mostro",

            )
        )
    }
}