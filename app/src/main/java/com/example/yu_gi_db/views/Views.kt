package com.example.yu_gi_db.views


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController // Importa NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
@Composable
fun MyScreenWithAToastButton() {
    // 1. Ottieni il Context corrente
    val context = LocalContext.current

    Button(onClick = {
        // 2. Crea e mostra il Toast quando il bottone viene cliccato
        val message = "Questo è un Toast da Compose!"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Puoi usare Toast.LENGTH_LONG per una durata maggiore
    }) {
        Text("Mostra Toast")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    appBarTitle: String,
    navController: NavHostController?,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StandardTopAppBar(
                title = appBarTitle,
                navController = navController,

            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Aggiungi se non già presente e usi componenti Material 3
@Composable
fun StandardTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    title: String="",
) {

    val navBackStackEntry by (navController ?: return).currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    TopAppBar(
        modifier = modifier,
        title = { Row{
            Column(Modifier.verticalScroll(rememberScrollState())){

                Text(stringResource(R.string.app_name) ,
                    modifier = Modifier.clickable{
                        navController.navigate(Screen.InitMainScreen.route) { // Inizia la lambda per NavOptionsBuilder
                                popUpTo(Screen.InitMainScreen.route) { // Usa la route corretta di MainScreen
                                    inclusive = true
                                }
                                launchSingleTop = true
                        }
                    }
                )
                if(title!= stringResource(R.string.app_name))
                {Text(title)} }
        } },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {

            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.card_detail_title_default)
                    )
                }
            }



        },
        actions = {
            Row {

                if (currentRoute!=Screen.SavedCardsScreen.route && currentRoute!=Screen.InfoScreen.route
                ) {
                    IconButton(onClick = {
                        navController.navigate(Screen.SavedCardsScreen.route)

                    })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.card_detail_title_default)
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.InfoScreen.route)

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.card_detail_title_default)
                        )
                    }
                }

            }


        }
    )

}

@Composable
fun WaitIndicatorView(modifier: Modifier = Modifier){
    CircularProgressIndicator(
        modifier = modifier, // Il modifier passato viene applicato qui
        color = MaterialTheme.colorScheme.primaryContainer, // Colore per migliore visibilità
        strokeWidth = 10.dp // Spessore del tratto per migliore visibilità
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
fun optionErrorView(modifier: Modifier = Modifier,
                    isLoading: Boolean=false,
                    isEmpty: Boolean=false,
                    errorMessage: String?=null,
                    isNotBlank: Boolean=true
                    ): Boolean
{
    var ret=false
    Box{
        if (isLoading) {
            WaitIndicatorView(
                modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.7f)
            ) // Ora WaitIndicatorView non ha testo e ha uno stile diverso
        }
        else if (errorMessage != null) {
            ErrorMessageView(stringResource(R.string.error_message_generic) + ": $errorMessage")
        }
        else if (isEmpty) {
            Log.d("CardsScreenView", "Displaying 'No cards found or saved' message.")
            ErrorMessageView(
                if (isNotBlank) stringResource(R.string.no_cards_found_search) else stringResource(
                    R.string.no_cards_saved
                )
            )
        }
        else{
            ret=true
        }
    }
    return ret
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
fun CardsScreenView(
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
        if(optionErrorView(modifier = modifier,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isEmpty = cards.isEmpty(),
            isNotBlank = searchQuery.isNotBlank()
                            )
        ) {
            SmallCardsListView(cards = cards, navController = navController) // Passa navController
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
            CardUrltoView(card.imageUrlSmall,modifier = Modifier.size(260.dp, 350.dp))

            Text(
                text = card.id.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun LargeCardItemView(
    modifier: Modifier = Modifier,
    card: LargePlayingCard ?=null,
    navController: NavHostController? = null // Aggiunto NavController
) {
    Column(
        modifier = modifier // Rimosso padding(innerPadding) da qui perché già gestito da AppScreen e optionErrorView
            .fillMaxSize()
            .padding(16.dp) // Padding per il contenuto interno
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val card = card ?: return@Column
        val firstCardImage: CardImage? = card.cardImages.firstOrNull()
        val smallImageUrl: String = firstCardImage?.imageUrlSmall ?: ""

        CardUrltoView(
            smallImageUrl,
            modifier = Modifier
                .size(260.dp, 350.dp)
                .clickable(enabled = navController != null && smallImageUrl.isNotEmpty()) {
                    smallImageUrl.let { url ->
                        val encodedUrl =
                            URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController?.navigate("cardZoom/$encodedUrl")
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Type: ${card.type} / ${card.race}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

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

        if (card.atk != null || card.def != null) {
            val atkText = card.atk?.toString() ?: "N/A"
            val defText = card.def?.toString() ?: "N/A"
            Text(
                text = "ATK: $atkText / DEF: $defText",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = card.desc,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
    }
}
@Composable
fun CardUrltoView(url: String,modifier: Modifier = Modifier ){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground), // Considera placeholder specifici
        error = painterResource(R.drawable.ic_launcher_background), // Considera error placeholder specifici
        contentDescription = stringResource(R.string.card_image_description),
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}



@Composable
fun InfoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(content = content)
    }
}

/*------------------------------------------------------------*/

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
        Box(modifier = Modifier, contentAlignment = Alignment.Center){ // Box per dare una dimensione
           WaitIndicatorView()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview2() {
    YuGiDBTheme {
     StandardTopAppBar(modifier = Modifier.fillMaxSize(),title = "Preview Title")
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
        InitLargePlayingCardScreen(
            cardId = 12345,
            // Passa navController se necessario

        )
    }
}