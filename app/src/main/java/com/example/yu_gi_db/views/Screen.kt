package com.example.yu_gi_db.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
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
import coil.compose.SubcomposeAsyncImage
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
fun InitMainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    val viewModel = hiltViewModel<CardListViewModel>()
    val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    if (isLoadingInitialData) {
        SplashScreen(modifier = modifier,navController)
    }
    else {
        MainScreen(modifier = modifier,navController)
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    Scaffold { innerPadding ->
        BoxWithConstraints(
            // Modificato per usare BoxWithConstraints
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
        topBar = {StandardTopAppBar(
            title = stringResource(id = R.string.app_name),
            navController = navController
        )}

    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController // Passa navController se InitCardsScreenView lo accetta
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitLargePlayingCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController?=null, // Aggiunto NavController
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
            StandardTopAppBar(
                title =largeCard?.name ?: stringResource(id = R.string.card_detail_title_default),
                navController = navController,
            )
        }
    ) {innerPadding ->
        OptionErrorView(modifier = modifier,
            isLoading = isLoading,
            errorMessage = error,
            isEmpty = largeCard == null,
            elseFun ={
                Column(
                    modifier = modifier.fillMaxSize().padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()), // Per contenuti lunghi
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    val card = largeCard ?: return@Column
                    val firstCardImage: CardImage? = card.cardImages.firstOrNull()
                    val smallImageUrl: String =
                        firstCardImage?.imageUrlSmall ?: "" // Per la visualizzazione in questa schermata
                    CardUrltoView(
                        smallImageUrl,
                        modifier = Modifier
                            .size(260.dp, 350.dp)
                            .clickable(enabled = navController != null && smallImageUrl != "") { // Rendi cliccabile
                                smallImageUrl.let { url ->
                                    // Codifica l'URL prima di passarlo come argomento di navigazione
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    // Assicurati che la route "cardZoom/{imageUrl}" sia definita nel tuo NavHost
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
                        Text(
                            text = "ATK: $atkText / DEF: $defText",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Descrizione
                    Text(
                        text = card.desc,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }

            }
        )

    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardZoomScreen(
    url: String, // URL dell'immagine da visualizzare
    modifier: Modifier = Modifier,
    navController: NavHostController? = null // Per la navigazione indietro tramite StandardTopAppBar
    // In alternativa, potresti passare una lambda: onNavigateBack: () -> Unit
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Assumendo che tu abbia una Composable StandardTopAppBar
            // Se la tua StandardTopAppBar non gestisce da sola l'icona di navigazione
            // basata sulla presenza del navController, potresti doverla configurare qui.
            StandardTopAppBar(
                title = stringResource(id = R.string.zoomed_card_title), // Esempio: "Zoom Immagine"
                navController = navController
                // iconButtonBool = true // o come vuoi configurarla
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier // Corretto: Inizia con Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url) // Usa l'immagine piccola per la visualizzazione
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.card_image_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
            )

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class) // Necessario per Scaffold, TopAppBar, ecc. da Material 3
@Composable
fun InformationScreen(
    modifier: Modifier = Modifier, // Il modifier per la Scaffold stessa (opzionale)
    navController: NavHostController? = null
) {
    Scaffold(
        modifier = modifier.fillMaxSize(), // La Scaffold occupa tutto lo spazio
        topBar = {
            StandardTopAppBar(
                title = stringResource(id = R.string.info_screen_title), // Titolo per questa schermata specifica
                navController = navController,
                iconButtonBool =false
            )
        }
    ) { innerPadding -> // innerPadding è fornito da Scaffold per gestire lo spazio della TopAppBar

        // Il contenuto precedente ora va qui, con il padding applicato
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // APPLICA QUI L'INNER PADDING!
                .verticalScroll(rememberScrollState())
                .padding(16.dp), // Padding aggiuntivo per il contenuto interno della colonna
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Non serve più il Text del titolo principale qui, perché è gestito dalla StandardTopAppBar
            // Se vuoi un titolo DIVERSO sotto la TopAppBar, puoi aggiungerlo.
            // Text(
            // text = stringResource(R.string.info_screen_title),
            // style = MaterialTheme.typography.headlineMedium,
            // textAlign = TextAlign.Center,
            // modifier = Modifier.padding(bottom = 24.dp)
            // )

            // Sezione 1: Descrizione dell'App
            InfoSection(
                title = stringResource(R.string.info_section_about_title)
            ) {
                Text(
                    text = stringResource(R.string.info_section_about_content),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione 2: Versione dell'App
            InfoSection(
                title = stringResource(R.string.info_section_version_title)
            ) {
                Text(
                    text = "1.0.0 (Build 1)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione 3: Sviluppatore
            InfoSection(
                title = stringResource(R.string.info_section_developer_title)
            ) {
                Text(
                    text = "Il Tuo Nome / Nome Azienda",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione 4: Ringraziamenti o Fonti Dati
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

/*----------------------------------------------------------------------------------------------------*/


@Preview(showBackground = true)
@Composable
fun CardZoomScreenPreview() {
    YuGiDBTheme { // Aggiunto Theme per coerenza con altre preview
        CardZoomScreen("")
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


@Preview(showBackground = true, name = "InfoScreenView Preview")
@Composable
fun InfoScreenViewPreview() {
    YuGiDBTheme { // Applica il tuo tema per la preview
        // Passa StandardScreenModel se vuoi vedere anche la TopAppBar nella preview
        // StandardScreenModel { modifier, _ -> InfoScreenView(modifier = modifier) }
        Surface { // Surface per avere uno sfondo predefinito se non usi StandardScreenModel
            InformationScreen()
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
        InitLargePlayingCardScreen(
            cardId = 12345,
            // Passa navController se necessario

        )
    }
}