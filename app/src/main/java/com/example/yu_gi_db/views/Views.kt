package com.example.yu_gi_db.views



import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.type
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
import coil.decode.ImageDecoderDecoder
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
import kotlin.math.roundToInt

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
                    navController.navigate(Screen.DataBaseScreen1.route) {
                        popUpTo(Screen.DataBaseScreen1.route) {
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
fun WaitIndicatorView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current // Ottieni la configurazione corrente

    // Determina se il dispositivo è in modalità landscape
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Applica modificatori diversi in base all'orientamento
    val imageModifier = if (isLandscape) {
        modifier
            .size(150.dp) // Rimpicciolisci la GIF in landscape
            .offset(y = 50.dp) // Abbassa la GIF in landscape
    } else {
        modifier
            .fillMaxSize(0.7f) // Mantieni le dimensioni originali in portrait
    }

    Box(Modifier.fillMaxSize()) { // Usa Box per centrare se necessario
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.infinito_elettrico)
                .decoderFactory(ImageDecoderDecoder.Factory())
                .build(),
            contentDescription = stringResource(R.string.loading_indicator_description),
            modifier = imageModifier.align(Alignment.Center) // Centra l'immagine nel Box
        )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRangeSlider(
    title: String,
    currentRange: ClosedFloatingPointRange<Float>, // Es: 0f..100f
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f, // Il range totale possibile dello slider
    steps: Int = 0, // Numero di step discreti (0 per continuo)
    enabled: Boolean = true
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "$title: ${currentRange.start.roundToInt()} - ${currentRange.endInclusive.roundToInt()}",
            style = MaterialTheme.typography.titleMedium
        )
        RangeSlider(
            value = currentRange,
            onValueChange = onRangeChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            // Puoi personalizzare i colori qui se necessario
            // colors = RangeSliderDefaults.colors(...)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Se MyRangeSlider o altri componenti lo richiedono
@Composable
fun TextFieldView(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    singleLine: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    searchCriteria: AdvancedSearchCriteria,
    onSearchCriteriaChange: (AdvancedSearchCriteria) -> Unit
) {
    var searchAdvanced by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) { // Il modifier principale è applicato alla Column radice
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(), // Il campo di testo principale riempie la larghezza
            label = label,
            singleLine = singleLine,
            keyboardActions = keyboardActions,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search, // Icona per "filtri" o "impostazioni avanzate".
                    contentDescription = "",
                    modifier = Modifier.clickable { searchAdvanced = !searchAdvanced }
                )
            }
        )

        if (searchAdvanced) {
            Column(modifier = Modifier.padding(top = 16.dp).verticalScroll(rememberScrollState())) {
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

                // ATK Range Slider
                val atkValueRange = 0f..5000f // Intervallo possibile per ATK
                val currentAtkStart = searchCriteria.atkMin?.toFloat() ?: atkValueRange.start
                val currentAtkEnd = searchCriteria.atkMax?.toFloat() ?: atkValueRange.endInclusive

                MyRangeSlider(
                    title =stringResource(R.string.search_bar_label_atk_min_hint)+"/"+stringResource(R.string.search_bar_label_atk_max_hint),
                    currentRange = currentAtkStart..currentAtkEnd,
                    onRangeChange = { newRange ->
                        val newAtkMin =
                            if (newRange.start <= atkValueRange.start) null else newRange.start.roundToInt()
                        val newAtkMax =
                            if (newRange.endInclusive >= atkValueRange.endInclusive) null else newRange.endInclusive.roundToInt()
                        onSearchCriteriaChange(
                            searchCriteria.copy(
                                atkMin = newAtkMin,
                                atkMax = newAtkMax
                            )
                        )
                    },
                    valueRange = atkValueRange,
                    steps = 49 // Slider continuo
                )
                Spacer(modifier = Modifier.height(8.dp))

                // DEF Range Slider
                val defValueRange = 0f..5000f // Intervallo possibile per DEF
                val currentDefStart = searchCriteria.defMin?.toFloat() ?: defValueRange.start
                val currentDefEnd = searchCriteria.defMax?.toFloat() ?: defValueRange.endInclusive

                MyRangeSlider(
                    title =stringResource(R.string.search_bar_label_def_min_hint)+"/"+stringResource(R.string.search_bar_label_def_max_hint),
                    currentRange = currentDefStart..currentDefEnd,
                    onRangeChange = { newRange ->
                        val newDefMin =
                            if (newRange.start <= defValueRange.start) null else newRange.start.roundToInt()
                        val newDefMax =
                            if (newRange.endInclusive >= defValueRange.endInclusive) null else newRange.endInclusive.roundToInt()
                        onSearchCriteriaChange(
                            searchCriteria.copy(
                                defMin = newDefMin,
                                defMax = newDefMax
                            )
                        )
                    },
                    valueRange = defValueRange,
                    steps = 49 // Slider continuo
                )
            }
        }
    }
}

@Composable
fun InitCardsScreenView(
    modifier: Modifier = Modifier,
    initialSearchCriteria: AdvancedSearchCriteria? = null,
    cardListViewModel: CardListViewModel = hiltViewModel(),
    navController: NavHostController? = null ,
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
    // Applica i criteri di ricerca iniziali quando la Composable viene caricata o il parametro cambia
/*
    LaunchedEffect(initialSearchCriteria) {
        if (initialSearchCriteria != null) {
            var vmCriteriaToUpdate = searchCriteria // Inizia con lo stato attuale del VM
            var needsVmUpdate = false

            // Name (String?)
            if (initialSearchCriteria.name != null) {
                if (vmCriteriaToUpdate.name != initialSearchCriteria.name) {
                    vmCriteriaToUpdate = vmCriteriaToUpdate.copy(name = initialSearchCriteria.name)
                    needsVmUpdate = true
                }
                // Resetta il campo 'name' in initialSearchCriteria a stringa vuota, come da tua richiesta specifica
                initialSearchCriteria.name = ""
            }

            // Type (String?)
            if (initialSearchCriteria.type != null) {
                if (vmCriteriaToUpdate.type != initialSearchCriteria.type) {
                    vmCriteriaToUpdate = vmCriteriaToUpdate.copy(type = initialSearchCriteria.type)
                    needsVmUpdate = true
                }
                initialSearchCriteria.type = null // Resetta a null
            }

            // Attribute (String?)
            if (initialSearchCriteria.attribute != null) {
                if (vmCriteriaToUpdate.attribute != initialSearchCriteria.attribute) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(attribute = initialSearchCriteria.attribute)
                    needsVmUpdate = true
                }
                initialSearchCriteria.attribute = null // Resetta a null
            }

            // Level (Int?)
            if (initialSearchCriteria.level != null) {
                if (vmCriteriaToUpdate.level != initialSearchCriteria.level) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(level = initialSearchCriteria.level)
                    needsVmUpdate = true
                }
                initialSearchCriteria.level = null
            }

            // atkMin (Int?)
            if (initialSearchCriteria.atkMin != null) {
                if (vmCriteriaToUpdate.atkMin != initialSearchCriteria.atkMin) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(atkMin = initialSearchCriteria.atkMin)
                    needsVmUpdate = true
                }
                initialSearchCriteria.atkMin = null
            }

            // atkMax (Int?)
            if (initialSearchCriteria.atkMax != null) {
                if (vmCriteriaToUpdate.atkMax != initialSearchCriteria.atkMax) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(atkMax = initialSearchCriteria.atkMax)
                    needsVmUpdate = true
                }
                initialSearchCriteria.atkMax = null
            }

            // defMin (Int?)
            if (initialSearchCriteria.defMin != null) {
                if (vmCriteriaToUpdate.defMin != initialSearchCriteria.defMin) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(defMin = initialSearchCriteria.defMin)
                    needsVmUpdate = true
                }
                initialSearchCriteria.defMin = null
            }

            // defMax (Int?)
            if (initialSearchCriteria.defMax != null) {
                if (vmCriteriaToUpdate.defMax != initialSearchCriteria.defMax) {
                    vmCriteriaToUpdate =
                        vmCriteriaToUpdate.copy(defMax = initialSearchCriteria.defMax)
                    needsVmUpdate = true
                }
                initialSearchCriteria.defMax = null
            }

            if (needsVmUpdate) {
                cardListViewModel.updateAdvancedSearchCriteria(vmCriteriaToUpdate)
            }
        }
    }
*/
    LaunchedEffect(initialSearchCriteria) {
        if (initialSearchCriteria != null) {
            cardListViewModel.updateAdvancedSearchCriteria(initialSearchCriteria)
        }
    }
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
    Column(modifier = modifier.fillMaxSize()) {
        TextFieldView( value = searchCriteria.name ?: "",
            onValueChange = { newValue ->
                onSearchCriteriaChange(searchCriteria.copy(name = newValue.ifBlank { null }))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_bar_label_name_hint)) },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            searchCriteria = searchCriteria,
            onSearchCriteriaChange = { newCriteria ->
                onSearchCriteriaChange(newCriteria)}
        )

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
            navController?.navigate(Screen.CardScreen.createRoute(card.id))
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
private fun ClickableSearchText(
    label: String,
    value: String?,
    navController: NavHostController?,
    searchCriteriaAction: () -> String // Lambda to create the specific search route
) {
    value?.let {
        Row {
            Text("$label: ", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.clickable {
                    navController?.navigate(searchCriteriaAction()) {
                        popUpTo(Screen.MenuScreen1.route) {
                            inclusive = false
                        }
                        // Assicura che la nuova rotta sia l'unica istanza in cima
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun LargeCardItemView(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentCard = card ?: return@Column // Renamed for clarity
        val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
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

        // Type and Race
        Row {
            ClickableSearchText(
                label = "Type",
                value = currentCard.type,
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearch.createRoutetype(type = currentCard.type)
                }
            )
            currentCard.race.let {
                Text(" / ", style = MaterialTheme.typography.titleMedium)
                ClickableSearchText(
                    label = "Race", // Although displayed next to type, Race is a distinct search criterion
                    value = it, // it is currentCard.race
                    navController = navController,
                    searchCriteriaAction = {
                        Screen.DataBaseAdvancedSearch.createRoutetype(type = it) // Assuming race is a type of search
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Attribute
        currentCard.attribute?.let {
            ClickableSearchText(
                label = "Attribute",
                value = it,
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearchAttribute.createRouteAttribute(attribute = it)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Level/Rank
        currentCard.level?.let {
            ClickableSearchText(
                label = "Level",
                value = it.toString(),
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearchLivello.createRouteAttribute(Livello = it)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (currentCard.atk != null || currentCard.def != null) {
            val atkText = currentCard.atk?.toString() ?: "N/A"
            val defText = currentCard.def?.toString() ?: "N/A"
            Text(
                text = "ATK: $atkText / DEF: $defText",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = currentCard.desc,
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
fun InfoSectionView(
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


@Preview(showBackground = true, name = "WaitIndicatorView") 
@Composable
fun WaitIndicatorViewPreview() { 
    YuGiDBTheme {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center){ 
            WaitIndicatorView()
        }
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



@Preview(showBackground = true)
@Composable
fun MyRangeSliderPreview() {
    // Stato per il range di ATK
    var atkRange by remember { mutableStateOf(2000f..8000f) }
    val possibleAtkValueRange = 0f..10000f // Valore minimo e massimo possibile per ATK

    // Stato per il range di DEF (con step)
    var defRange by remember { mutableStateOf(10f..50f) } // Range in decine, ad es. 100-500
    val possibleDefValueRange = 0f..100f // Range in decine (0-1000)
    val defSteps = 9 // (100-0)/10 - 1 = 9 step per avere intervalli di 10

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Filtri Valori Mostro", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        MyRangeSlider(
            title = "ATK Range",
            currentRange = atkRange,
            onRangeChange = { newRange -> atkRange = newRange },
            valueRange = possibleAtkValueRange,
            steps = 0 // Slider continuo per ATK
        )

        Spacer(modifier = Modifier.height(24.dp))

        MyRangeSlider(
            title = "DEF Range (x10)", // Esempio: i valori rappresentano DEF/10
            currentRange = defRange,
            onRangeChange = { newRange -> defRange = newRange },
            valueRange = possibleDefValueRange,
            steps = defSteps // Slider con step per DEF
        )
        Text(
            text = "DEF Effettivo: ${defRange.start.roundToInt() * 10} - ${defRange.endInclusive.roundToInt() * 10}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Esempio disabilitato
        var disabledRange by remember { mutableStateOf(30f..70f) }
        MyRangeSlider(
            title = "Range Disabilitato",
            currentRange = disabledRange,
            onRangeChange = { disabledRange = it },
            valueRange = 0f..100f,
            enabled = false
        )
    }
}
