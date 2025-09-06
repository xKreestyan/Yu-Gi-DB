package com.example.yu_gi_db.views

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController // Importa NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// NOTA: Assicurati che le seguenti stringhe siano definite in strings.xml:
// <string name="search_bar_label_name_hint">Cerca per nome...</string>
// <string name="search_bar_label_type_hint">Tipo (es. Spell Card)</string>
// <string name="search_bar_label_attribute_hint">Attributo (es. LIGHT)</string>
// <string name="search_bar_label_level_hint">Livello/Rango</string>
// <string name="search_bar_label_atk_min_hint">ATK Min</string>
// <string name="search_bar_label_atk_max_hint">ATK Max</string>
// <string name="search_bar_label_def_min_hint">DEF Min</string>
// <string name="search_bar_label_def_max_hint">DEF Max</string>
// <string name="no_cards_in_default_list">Nessuna carta nel set predefinito.</string>

@Composable
fun MyScreenWithAToastButton() {
    val context = LocalContext.current
    Button(onClick = {
        val message = "Questo è un Toast da Compose!"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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

@OptIn(ExperimentalMaterial3Api::class)
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
        title = {Column{
            Text(
                stringResource(R.string.app_name ),
                modifier = Modifier.clickable{
                    navController.navigate(Screen.MainScreen.route) { 
                        popUpTo(Screen.MainScreen.route) { 
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
            if(title!= stringResource(R.string.app_name))
            {Row(Modifier.horizontalScroll(rememberScrollState()) ){
                Text(title)} }}
        },
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
        modifier = modifier, 
        color = MaterialTheme.colorScheme.primaryContainer, 
        strokeWidth = 10.dp 
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
            textAlign = TextAlign.Justify, 
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun optionErrorView(modifier: Modifier = Modifier,
                    isLoading: Boolean=false,
                    isEmpty: Boolean=false,
                    errorMessage: String?=null,
                    isSearchActive: Boolean=true // True if an active search query is not blank
): Boolean
{
    var ret=false

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            WaitIndicatorView(
                modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.7f)
            )}
    }
    else if (errorMessage != null) {
        ErrorMessageView(stringResource(R.string.error_message_generic) + ": $errorMessage")
    }
    else if (isEmpty) {
        Log.d("optionErrorView", "IsEmpty: true. isSearchActive: $isSearchActive")
        ErrorMessageView(
            if (isSearchActive) stringResource(R.string.no_cards_found_search) 
            else stringResource(R.string.no_cards_in_default_list) // Specific message for empty default list
        )
    }
    else{
        ret=true
    }

    return ret
}


@Composable
fun InitCardsScreenView(
    modifier: Modifier = Modifier,
    cardListViewModel: CardListViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    // Stati per la lista di default (LOB)
    val defaultCards by cardListViewModel.smallCards.collectAsStateWithLifecycle()
    val isLoadingInitial by cardListViewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    val initialError by cardListViewModel.initialDataError.collectAsStateWithLifecycle()

    // Stati per la ricerca avanzata
    val searchCriteria by cardListViewModel.searchCriteria.collectAsStateWithLifecycle()
    val advancedSearchResults by cardListViewModel.advancedSearchResults.collectAsStateWithLifecycle()
    val isSearchingAdvanced by cardListViewModel.isSearchingAdvanced.collectAsStateWithLifecycle()
    val advancedSearchError by cardListViewModel.advancedSearchError.collectAsStateWithLifecycle()

    // Log per debug chiavi duplicate
    if (defaultCards.isNotEmpty()) { // Log solo se la lista non è vuota per evitare spam
        Log.d("DEBUG_KEYS_DEFAULT", "Default cards IDs: ${defaultCards.map { it.id }.joinToString()}")
    }
    if (advancedSearchResults.isNotEmpty()) { // Log solo se la lista non è vuota
        Log.d("DEBUG_KEYS_SEARCH", "Search results IDs: ${advancedSearchResults.map { it.id }.joinToString()}")
    }

    val cardsToDisplay: List<SmallPlayingCard>
    val isLoadingDisplay: Boolean
    val errorDisplay: String?

    // Determina quale lista mostrare e gli stati di caricamento/errore associati
    // Se i criteri di ricerca non sono quelli di default (vuoti), allora una ricerca è attiva o è stata tentata.
    if (searchCriteria != AdvancedSearchCriteria()) { 
        cardsToDisplay = advancedSearchResults
        isLoadingDisplay = isSearchingAdvanced
        errorDisplay = advancedSearchError
    } else {
        // Nessuna ricerca attiva, mostra la lista di default (LOB)
        cardsToDisplay = defaultCards
        isLoadingDisplay = isLoadingInitial
        errorDisplay = initialError
    }

    Log.d("InitCardsScreenView", 
        "SearchCriteria: $searchCriteria, Displaying ${cardsToDisplay.size} cards. " +
        "Loading: $isLoadingDisplay, Error: $errorDisplay. "
    )

    CardsScreenView(
        modifier = modifier,
        cards = cardsToDisplay,
        isLoading = isLoadingDisplay,
        errorMessage = errorDisplay,
        searchCriteria = searchCriteria, 
        onSearchCriteriaChange = { newCriteria ->
            cardListViewModel.updateAdvancedSearchCriteria(newCriteria)
        },
        navController = navController
    )
}

@Composable
fun CardsScreenView(
    modifier: Modifier = Modifier,
    cards: List<SmallPlayingCard>,
    isLoading: Boolean,
    errorMessage: String?,
    searchCriteria: AdvancedSearchCriteria, 
    onSearchCriteriaChange: (AdvancedSearchCriteria) -> Unit, 
    navController: NavHostController? = null
){
    Log.d("CardsScreenView", "Render. Cards: ${cards.size}, Loading: $isLoading, Error: $errorMessage, SearchCriteria: $searchCriteria")
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        // Contenitore per i campi di ricerca, scrollabile verticalmente se non ci stanno tutti
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState) // Permette lo scroll dei campi di ricerca
        ) {
            OutlinedTextField(
                value = searchCriteria.name ?: "",
                onValueChange = { newValue -> 
                    onSearchCriteriaChange(searchCriteria.copy(name = newValue.ifBlank { null })) 
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.search_bar_label_name_hint)) }, 
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchCriteria.type ?: "",
                onValueChange = { newValue -> 
                    onSearchCriteriaChange(searchCriteria.copy(type = newValue.ifBlank { null })) 
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.search_bar_label_type_hint)) }, 
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchCriteria.attribute ?: "",
                onValueChange = { newValue -> 
                    onSearchCriteriaChange(searchCriteria.copy(attribute = newValue.ifBlank { null })) 
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.search_bar_label_attribute_hint)) }, 
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchCriteria.level?.toString() ?: "",
                onValueChange = { newValue -> 
                    onSearchCriteriaChange(searchCriteria.copy(level = newValue.toIntOrNull())) 
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.search_bar_label_level_hint)) }, 
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = searchCriteria.atkMin?.toString() ?: "",
                    onValueChange = { newValue -> 
                        onSearchCriteriaChange(searchCriteria.copy(atkMin = newValue.toIntOrNull())) 
                    },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    label = { Text(stringResource(R.string.search_bar_label_atk_min_hint)) }, 
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
                OutlinedTextField(
                    value = searchCriteria.atkMax?.toString() ?: "",
                    onValueChange = { newValue -> 
                        onSearchCriteriaChange(searchCriteria.copy(atkMax = newValue.toIntOrNull())) 
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    label = { Text(stringResource(R.string.search_bar_label_atk_max_hint)) }, 
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = searchCriteria.defMin?.toString() ?: "",
                    onValueChange = { newValue -> 
                        onSearchCriteriaChange(searchCriteria.copy(defMin = newValue.toIntOrNull())) 
                    },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    label = { Text(stringResource(R.string.search_bar_label_def_min_hint)) }, 
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
                OutlinedTextField(
                    value = searchCriteria.defMax?.toString() ?: "",
                    onValueChange = { newValue -> 
                        onSearchCriteriaChange(searchCriteria.copy(defMax = newValue.toIntOrNull())) 
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    label = { Text(stringResource(R.string.search_bar_label_def_max_hint)) }, 
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
            }
        }

        val isSearchActive = searchCriteria != AdvancedSearchCriteria()
        if(optionErrorView(
                modifier = modifier.weight(1f), // Aggiungi weight per riempire lo spazio rimanente
                isLoading = isLoading,
                errorMessage = errorMessage,
                isEmpty = cards.isEmpty(),
                isSearchActive = isSearchActive 
            )
        ) {
            SmallCardsListView(
                cards = cards, 
                navController = navController,
                modifier = Modifier.weight(1f) // Aggiungi weight per riempire lo spazio rimanente
            )
        }
    }
}


@Composable
fun SmallCardsListView(
    cards: List<SmallPlayingCard>,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    Log.d("SmallCardsListView", "Displaying LazyVerticalGrid with ${cards.size} cards.")
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(), // Rimosso fillMaxSize() da qui se il Column padre ha già weight
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { card -> card.id }) { card ->
            SmallCardItemView(modifier = modifier,card = card, navController = navController)
        }
    }
}

@Composable
fun SmallCardItemView(
    card: SmallPlayingCard,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    Card(
        modifier = modifier,
        onClick = {
            navController?.navigate(Screen.CardScreen.createRoute(card.id.toString()))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(0.dp, 280.dp) 
        ) {
            CardUrltoView(
                url = card.imageUrlSmall,
                modifier = modifier.fillMaxSize()
            )
            Card(modifier = Modifier.align(Alignment.BottomStart)
            ){Text(
                text = card.id.toString(), 
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp), 
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )}
        }
    }
}


@Composable
fun LargeCardItemView2(
    modifier: Modifier = Modifier,
    card: LargePlayingCard ?=null,
    navController: NavHostController? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val card = card ?: return@Column
        val firstCardImage: CardImage? = card.cardImages.firstOrNull()
        val imageUrl: String = firstCardImage?.imageUrlSmall ?: "" 

        CardUrltoView(
            imageUrl, 
            modifier = Modifier
                .size(260.dp, 350.dp)
                .clickable(enabled = navController != null && imageUrl.isNotEmpty()) {
                    imageUrl.let { url ->
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
fun LargeCardItemView(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null
) {
    val currentCard = card ?: return
    val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
    val imageUrl: String = firstCardImage?.imageUrlSmall ?: ""

    val detailTextBackgroundColor = Color.Black.copy(alpha =0.65f)
    val detailTextPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    val detailShape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_background),
            contentDescription = stringResource(R.string.card_image_description),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(30.dp,29.dp)) {
            Text(
                text = currentCard.name.uppercase()+"                     ",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .background(
                        detailTextBackgroundColor,
                        detailShape
                    )
                    .fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier.padding(0.dp,413.dp) 
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.horizontalScroll(rememberScrollState())){
                Text(
                    text = "Type: ${currentCard.type} / ${currentCard.race}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .background(detailTextBackgroundColor, detailShape)
                        .padding(detailTextPadding)
                )
                currentCard.attribute?.let {
                    Text(
                        text = "Attribute: $it",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(detailTextBackgroundColor, detailShape)
                            .padding(detailTextPadding)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                currentCard.level?.let {
                    Text(
                        text = "Level/Rank: $it",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(detailTextBackgroundColor, detailShape)
                            .padding(detailTextPadding)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Column(Modifier.size(1000.dp, 68.dp).fillMaxWidth()) { 
                Text(
                    text = currentCard.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    color = Color.White,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .background(
                            detailTextBackgroundColor,
                            detailShape
                        ) 
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            if (currentCard.atk != null || currentCard.def != null) {
                val atkText = currentCard.atk?.toString() ?: "N/A"
                val defText = currentCard.def?.toString() ?: "N/A"
                Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.BottomEnd) {
                    Text(
                        text = "ATK: $atkText / DEF: $defText",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .background(detailTextBackgroundColor, detailShape)
                            .padding(detailTextPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CardUrltoView(url: String,modifier: Modifier = Modifier ){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url) 
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        error = painterResource(R.drawable.ic_launcher_background),
        contentDescription = stringResource(R.string.card_image_description),
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}
@Composable
fun ImageRotation(imageV: Int, imageO: Int, modifier: Modifier = Modifier ){
    val configuration = LocalConfiguration.current
    val imageResource = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        imageO
    } else {
       imageV
    }
    Image(
        painter =painterResource(id = imageResource),
        contentDescription = stringResource(id = R.string.error_message_generic),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
@Composable
fun RotationScreen(screenV: @Composable () -> Unit, screenO: @Composable () -> Unit,modifier: Modifier = Modifier ){
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        screenO()
    } else {
        screenV()
    }
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
// Preview functions

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

@Preview(showBackground = true, name = "WaitIndicatorView") 
@Composable
fun WaitIndicatorViewPreview() { 
    YuGiDBTheme {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center){ 
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

@Preview(showBackground = true, name = "CardsScreen - Populated Default")
@Composable
fun CardsScreenPopulatedDefaultPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg")
            ),
            isLoading = false,
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(name = ""), 
            onSearchCriteriaChange = {},
            navController = null
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Loading Initial")
@Composable
fun CardsScreenLoadingInitialPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true, 
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(),
            onSearchCriteriaChange = {},
            navController = null
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Error Initial")
@Composable
fun CardsScreenErrorInitialPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Failed to load default cards.",
            searchCriteria = AdvancedSearchCriteria(),
            onSearchCriteriaChange = {},
            navController = null
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Searching by Name")
@Composable
fun CardsScreenSearchingByNamePreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(), 
            isLoading = true,    
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(name = "Blue-Eyes"), 
            onSearchCriteriaChange = {},
            navController = null
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - No Results for Name Search")
@Composable
fun CardsScreenNoResultsNameSearchPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null, 
            searchCriteria = AdvancedSearchCriteria(name = "NonExistentCardNameXYZ"), 
            onSearchCriteriaChange = {},
            navController = null
        )
    }
}

@Preview(showBackground = true, name = "LargeCardDetail - Populated")
@Composable
fun LargeCardDetailPreview() {
    YuGiDBTheme {
        InitLargePlayingCardScreen( 
            cardId = 12345 
        )
    }
}

@Preview(showBackground = true, name = "LargeCardItemView2 Preview")
@Composable
fun LargeCardItemView2Preview() {
    YuGiDBTheme {
        //LargeCardItemView2(card = LargePlayingCard(id=1, name="Blue-Eyes White Dragon", type="Dragon", race="Dragon", desc="This legendary dragon is a powerful engine of destruction.", atk=3000, def=2500, level=8, attribute="LIGHT", cardImages=emptyList(), cardSets=emptyList(), cardPrices=emptyList()))
    }
}
