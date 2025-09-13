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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    navController.navigate(Screen.MenuScreen1.route) {
                        popUpTo(Screen.MenuScreen1.route) {
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
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = stringResource(R.string.card_detail_title_default)
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.InfoScreen.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
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

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // Usa Box per centrare se necessario
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.optimized_refined)
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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchCriteriaDropdown(
    label: String,
    selectedValueDisplay: String,
    items: Map<String, T?>, // Mappa di visualizzazione -> valore effettivo
    onItemSelected: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current // Se serve ancora per i dropdown

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValueDisplay,
            onValueChange = {}, // Non modificabile direttamente
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor() // Importante per ExposedDropdownMenuBox
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { (displayValue, actualValue) ->
                DropdownMenuItem(
                    text = { Text(displayValue) },
                    onClick = {
                        onItemSelected(actualValue)
                        expanded = false
                        focusManager.clearFocus() // Se applicabile
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldView(
    value: String, // General search term, often maps to 'name'
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

    val cardTypes = mapOf(
        stringResource(R.string.search_any_type) to null,
        "Carte Magia (Spell Card)" to "Spell Card",
        "Carte Trappola (Trap Card)" to "Trap Card",
        "Mostri Normali (Normal Monster)" to "Normal Monster",
        "Mostri Effetto (Effect Monster)" to "Effect Monster",
        "Mostri Effetto Scoperta (Flip Effect Monster)" to "Flip Effect Monster",
        "Mostri Fusione (Fusion Monster)" to "Fusion Monster",
        "Mostri Rituale (Ritual Monster)" to "Ritual Monster",
        "Mostri Synchro (Synchro Monster)" to "Synchro Monster",
        "Mostri XYZ (XYZ Monster)" to "XYZ Monster",
        "Mostri Pendulum Effetto (Pendulum Effect Monster)" to "Pendulum Effect Monster",
        "Mostri Link (Link Monster)" to "Link Monster",
        "Token" to "Token"
    )
    val cardAttributes = mapOf(
        stringResource(R.string.search_any_attribute) to null,
        "OSCURITÀ (DARK)" to "DARK",
        "LUCE (LIGHT)" to "LIGHT",
        "TERRA (EARTH)" to "EARTH",
        "ACQUA (WATER)" to "WATER",
        "FUOCO (FIRE)" to "FIRE",
        "VENTO (WIND)" to "WIND",
        "DIVINO (DIVINE)" to "DIVINE"
    )
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = label,
            singleLine = singleLine,
            keyboardActions = keyboardActions,
            trailingIcon = {
                val icon = if (searchAdvanced) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                val contentDesc = if (searchAdvanced) stringResource(R.string.search_options_collapse) else stringResource(R.string.search_options_expand)
                Row {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = contentDesc,
                        modifier = Modifier
                            .clickable {
                                onSearchCriteriaChange(AdvancedSearchCriteria(isFavorite =searchCriteria.isFavorite )) // Usa la callback!

                            }
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDesc,
                        modifier = Modifier.clickable { searchAdvanced = !searchAdvanced }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        )

        if (searchAdvanced) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxHeight(0.5f)
                    .verticalScroll(rememberScrollState())
            ) {
                // ... (dentro il tuo Composable TextFieldView, nella sezione if (searchAdvanced))

                Row(
                    modifier = Modifier.fillMaxWidth(), // La Row occupa l'intera larghezza
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Spazio tra i figli della Row
                )
                {
                    // Dropdown per TYPE
                    SearchCriteriaDropdown(
                        label = stringResource(R.string.search_bar_label_type_hint),
                        selectedValueDisplay = cardTypes.entries.find { it.value == searchCriteria.type }?.key
                            ?: stringResource(R.string.search_any_type),
                        items = cardTypes,
                        onItemSelected = { selectedType ->
                            onSearchCriteriaChange(searchCriteria.copy(type = selectedType))
                        },
                        modifier = Modifier.weight(1f) // Occupa metà dello spazio disponibile
                    )

                    // Dropdown per ATTRIBUTE
                    SearchCriteriaDropdown(
                        label = stringResource(R.string.search_bar_label_attribute_hint),
                        selectedValueDisplay = cardAttributes.entries.find { it.value == searchCriteria.attribute }?.key
                            ?: stringResource(R.string.search_any_attribute),
                        items = cardAttributes,
                        onItemSelected = { selectedAttribute ->
                            onSearchCriteriaChange(searchCriteria.copy(attribute = selectedAttribute))
                        },
                        modifier = Modifier.weight(1f) // Occupa l'altra metà dello spazio disponibile
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Aggiunge spazio uniforme tra i campi
                )
                {
                    // OutlinedTextField per LEVEL
                    OutlinedTextField(
                        value = searchCriteria.level?.toString() ?: "",
                        onValueChange = { newValue ->
                            // Considera di limitare la lunghezza o i valori qui se necessario
                            val newLevel = newValue.filter { it.isDigit() }.take(2) // Permette solo cifre, max 2
                            onSearchCriteriaChange(searchCriteria.copy(level = newLevel.toIntOrNull()))
                        },
                        label = { Text(stringResource(R.string.search_bar_label_level_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.weight(1f) // Occupa metà dello spazio disponibile
                    )

                    // OutlinedTextField per ID Carta (idQuery)
                    OutlinedTextField(
                        value = searchCriteria.idQuery ?: "",
                        onValueChange = { newValue ->
                            onSearchCriteriaChange(searchCriteria.copy(idQuery = newValue.takeIf { it.isNotBlank() }))
                        },
                        label = { Text(stringResource(R.string.search_id)) },
                        singleLine = true,
                        // keyboardOptions: considera se vuoi limitare a numeri o permettere testo per ricerche parziali di ID
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // O KeyboardType.NumberPassword se sono solo numeri
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.weight(1f) // Occupa l'altra metà dello spazio disponibile
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp), // Aggiunge spazio sopra e sotto il divisore
                    thickness = 1.dp, // Spessore della linea (opzionale, default è sottile)
                    color = MaterialTheme.colorScheme.outlineVariant // Colore (opzionale, default dal tema)
                )
                 // ATK Range Slider
                val atkValueRange = 0f..5000f
                val currentAtkStart = searchCriteria.atkMin?.toFloat() ?: atkValueRange.start
                val currentAtkEnd = searchCriteria.atkMax?.toFloat() ?: atkValueRange.endInclusive
                MyRangeSlider(
                    title = stringResource(R.string.search_bar_label_atk_min_hint) + "/" + stringResource(R.string.search_bar_label_atk_max_hint),
                    currentRange = currentAtkStart..currentAtkEnd,
                    onRangeChange = { newRange ->
                        val newAtkMin = if (newRange.start <= atkValueRange.start) null else newRange.start.roundToInt()
                        val newAtkMax = if (newRange.endInclusive >= atkValueRange.endInclusive) null else newRange.endInclusive.roundToInt()
                        onSearchCriteriaChange(searchCriteria.copy(atkMin = newAtkMin, atkMax = newAtkMax))
                    },
                    valueRange = atkValueRange,
                    steps = (atkValueRange.endInclusive - atkValueRange.start).toInt() / 50 -1 //  steps per intervalli di 50
                )
                // DEF Range Slider
                val defValueRange = 0f..5000f
                val currentDefStart = searchCriteria.defMin?.toFloat() ?: defValueRange.start
                val currentDefEnd = searchCriteria.defMax?.toFloat() ?: defValueRange.endInclusive
                MyRangeSlider(
                    title = stringResource(R.string.search_bar_label_def_min_hint) + "/" + stringResource(R.string.search_bar_label_def_max_hint),
                    currentRange = currentDefStart..currentDefEnd,
                    onRangeChange = { newRange ->
                        val newDefMin = if (newRange.start <= defValueRange.start) null else newRange.start.roundToInt()
                        val newDefMax = if (newRange.endInclusive >= defValueRange.endInclusive) null else newRange.endInclusive.roundToInt()
                        onSearchCriteriaChange(searchCriteria.copy(defMin = newDefMin, defMax = newDefMax))
                    },
                    valueRange = defValueRange,
                    steps = (defValueRange.endInclusive - defValueRange.start).toInt() / 50 - 1 // steps per intervalli di 50
                )
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp), // Aggiunge spazio sopra e sotto il divisore
                    thickness = 1.dp, // Spessore della linea (opzionale, default è sottile)
                    color = MaterialTheme.colorScheme.outlineVariant // Colore (opzionale, default dal tema)
                )
                // ... (dentro il tuo Composable TextFieldView, nella sezione if (searchAdvanced))
// ... (Codice per i RangeSlider ATK/DEF e il Divider precedente)

                Spacer(modifier = Modifier.height(8.dp)) // O 16.dp se vuoi più spazio prima di questa sezione

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Aggiunge spazio uniforme tra i campi
                ) {
                    // OutlinedTextField per Nome Set (setNameQuery)
                    OutlinedTextField(
                        value = searchCriteria.setNameQuery ?: "",
                        onValueChange = { newValue ->
                            onSearchCriteriaChange(
                                searchCriteria.copy(
                                    setNameQuery = newValue.takeIf { it.isNotBlank() },
                                    atkMin = currentAtkStart.toInt()
                                    )
                            )
                        },
                        label = { Text(stringResource(R.string.search_label_set_name)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.weight(1f) // Occupa metà dello spazio disponibile
                    )

                    // OutlinedTextField per Codice Set (setCodeQuery)
                    OutlinedTextField(
                        value = searchCriteria.setCodeQuery ?: "",
                        onValueChange = { newValue ->
                            onSearchCriteriaChange(
                                searchCriteria.copy(
                                    setCodeQuery = newValue.takeIf { it.isNotBlank() },
                                            atkMin =currentAtkStart.toInt()
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.search_label_set_code)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.weight(1f) // Occupa l'altra metà dello spazio disponibile
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Spazio prima della sezione "Preferiti"




                // Checkbox per Preferiti (isFavorite)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = searchCriteria.isFavorite ?: false, // Se null, mostra come non selezionato
                        onCheckedChange = { isChecked ->
                            onSearchCriteriaChange(searchCriteria.copy(isFavorite = if (isChecked) true else null))
                        }
                    )
                    Text(
                        text = stringResource(R.string.search_label_favorites_only), // Crea R.string.search_label_favorites_only
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
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

    if (searchCriteria != AdvancedSearchCriteria()) {
        cardsToDisplay = advancedSearchResults
        isLoadingDisplay = isSearchingAdvanced
        errorDisplay = advancedSearchError
    } else {
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
                modifier = modifier.weight(1f),
                isLoading = false,
                errorMessage = errorMessage,
                isEmpty = cards.isEmpty(),
                isSearchActive = isSearchActive
            )
        ) {
            SmallCardsListView(
                cards = cards,
                navController = navController,
                modifier = Modifier.weight(1f)
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
        modifier = modifier.fillMaxSize(),
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
    navController: NavHostController? = null,
    cardListViewModel: CardListViewModel ?= hiltViewModel()
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
            )
            {Row{
                val icon =
                    if (card.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                val contentDesc =
                    if (card.isFavorite) stringResource(R.string.isfavorite) else stringResource(R.string.notfavorite)
                Icon(
                    imageVector = icon,
                    contentDescription = contentDesc, // Descrizione per l'accessibilità
                    modifier = Modifier
                        .size(15.dp, 15.dp)
                        .clickable { cardListViewModel?.toggleFavoriteStatus(card.id) }
                )
                Text(
                    text = card.id.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }

            }

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
    navController: NavHostController? = null,
    cardListViewModel: CardListViewModel ?= hiltViewModel()

) {
    LargeCardUI(
        card = card,
        modifier = modifier,
        navController = navController
    )
    /*Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
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
                        navController?.navigate(Screen.ZoomCardScreen.createRoute(encodedUrl))
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Type and Race
        Row(verticalAlignment = Alignment.CenterVertically) {
            ClickableSearchText(
                label = stringResource(R.string.card_label_type),
                value = currentCard.type,
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearch.createRouteForType(type = currentCard.type)
                }
            )
            currentCard.race.let {
                Text(" / ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                /* ClickableSearchText(
                   label = "", // La label "Race" è implicita dalla posizione
                   value = it,
                   navController = navController,
                   searchCriteriaAction = {
                       Screen.DataBaseAdvancedSearchType.createRoute(type = it) // Ricerca per razza come tipo
                   }
               )*/

                var favoriteBoolean by remember { mutableStateOf(card.isFavorite) }
                val icon =
                    if (favoriteBoolean) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                val contentDesc =
                    if (favoriteBoolean) stringResource(R.string.isfavorite) else stringResource(R.string.notfavorite)
                Icon(
                    imageVector = icon,
                    contentDescription = contentDesc, // Descrizione per l'accessibilità
                    modifier = Modifier
                        .clickable { cardListViewModel?.toggleFavoriteStatus(card.id)
                            favoriteBoolean=!favoriteBoolean
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Attribute
        currentCard.attribute?.let {
            ClickableSearchText(
                label = stringResource(R.string.card_label_attribute),
                value = it,
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearch.createRouteForAttribute(attribute = it)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Level/Rank
        currentCard.level?.let {
            ClickableSearchText(
                label = stringResource(R.string.card_label_level),
                value = it.toString(),
                navController = navController,
                searchCriteriaAction = {
                    Screen.DataBaseAdvancedSearch.createRouteForLevel(level = it)
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
    }*/
}
@Composable
fun CardUrltoView(url: String,modifier: Modifier = Modifier ){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.cardback),
        error = painterResource(R.drawable.cardback),
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
            card = SmallPlayingCard(id = 1, name = "A Cell Breeding Device", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
            cardListViewModel=null
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
                SmallPlayingCard(id = 1, name = "A Cell Breeding Device", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, name = "Tornado Dragon", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg")
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
