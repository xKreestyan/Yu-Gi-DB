package com.example.yu_gi_db.views

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.AdvancedSearchViewModel
import com.example.yu_gi_db.viewmodels.CardListViewModel
import com.example.yu_gi_db.viewmodels.FavoritesViewModel
import com.example.yu_gi_db.views.navigation.Screen
import kotlin.Boolean
import kotlin.math.roundToInt


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
    onSearchCriteriaChange: (AdvancedSearchCriteria) -> Unit,
    searchAdvancedBoolean: Boolean=false
) {
    var searchAdvanced by remember { mutableStateOf(searchAdvancedBoolean) }
    val focusManager = LocalFocusManager.current

    val cardTypes = mapOf(
        stringResource(R.string.search_any_type) to null,
        stringResource(R.string.card_type_api_spell) to "Spell Card",
        stringResource(R.string.card_type_api_trap) to "Trap Card",
        stringResource(R.string.card_type_api_normal_monster) to "Normal Monster",
        stringResource(R.string.card_type_api_effect_monster) to "Effect Monster",
        stringResource(R.string.card_type_api_flip_effect_monster) to "Flip Effect Monster",
        stringResource(R.string.card_type_api_fusion_monster) to "Fusion Monster",
        stringResource(R.string.card_type_api_ritual_monster) to "Ritual Monster",
        stringResource(R.string.card_type_api_synchro_monster) to "Synchro Monster",
        stringResource(R.string.card_type_api_xyz_monster) to "XYZ Monster",
        stringResource(R.string.card_type_api_pendulum_effect_monster) to "Pendulum Effect Monster",
        stringResource(R.string.card_type_api_link_monster) to "Link Monster",
        stringResource(R.string.card_type_api_token) to "Token"
    )
    val cardAttributes = mapOf(
        stringResource(R.string.search_any_attribute) to null,
        stringResource(R.string.attribute_dark) to "DARK",
        stringResource(R.string.attribute_light) to "LIGHT",
        stringResource(R.string.attribute_earth) to "EARTH",
        stringResource(R.string.attribute_water) to "WATER",
        stringResource(R.string.attribute_fire) to "FIRE",
        stringResource(R.string.attribute_wind) to "WIND",
        stringResource(R.string.attribute_divine) to "DIVINE"
    )

    val atkValueRange = 0f..5000f
    val currentAtkStart = searchCriteria.atkMin?.toFloat() ?: atkValueRange.start
    val currentAtkEnd = searchCriteria.atkMax?.toFloat() ?: atkValueRange.endInclusive

    val defValueRange = 0f..5000f
    val currentDefStart = searchCriteria.defMin?.toFloat() ?: defValueRange.start
    val currentDefEnd = searchCriteria.defMax?.toFloat() ?: defValueRange.endInclusive


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
                        contentDescription = stringResource(R.string.reset_search_options),
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
                    //.fillMaxHeight(0.5f) // Rimuovi o adatta questa riga
                    .fillMaxHeight(if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.8f else 0.5f) // Adatta l'altezza massima
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
                //set

                Spacer(modifier = Modifier.height(8.dp)) // O 16.dp se vuoi più spazio prima di questa sezione

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Aggiunge spazio uniforme tra i campi
                )
                {
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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), // Aggiunge spazio sopra e sotto il divisore
                    thickness = 1.dp, // Spessore della linea (opzionale, default è sottile)
                    color = MaterialTheme.colorScheme.outlineVariant // Colore (opzionale, default dal tema)
                )
                // ATK Range Slider
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
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), // Aggiunge spazio sopra e sotto il divisore
                    thickness = 1.dp, // Spessore della linea (opzionale, default è sottile)
                    color = MaterialTheme.colorScheme.outlineVariant // Colore (opzionale, default dal tema)
                )

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
    advancedSearchViewModel: AdvancedSearchViewModel = hiltViewModel(), // <-- NUOVA INIEZIONE
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    cardListViewModel: CardListViewModel = hiltViewModel(),
    navController: NavHostController? = null, // NavController rimane qui per la navigazione
) {
    // Stati per la lista di default (LOB)
    val defaultCards by cardListViewModel.smallCards.collectAsStateWithLifecycle()
    val isLoadingInitial by cardListViewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    val initialError by cardListViewModel.initialDataError.collectAsStateWithLifecycle()

    // Stati per la ricerca avanzata
    val currentSearchCriteria by advancedSearchViewModel.searchCriteria.collectAsStateWithLifecycle() // Rinominato per chiarezza
    val advancedSearchResults by advancedSearchViewModel.advancedSearchResults.collectAsStateWithLifecycle()
    val isSearchingAdvanced by advancedSearchViewModel.isSearchingAdvanced.collectAsStateWithLifecycle()
    val advancedSearchError by advancedSearchViewModel.advancedSearchError.collectAsStateWithLifecycle()

    // Effetto per applicare i criteri di ricerca iniziali, se presenti
    LaunchedEffect(initialSearchCriteria, currentSearchCriteria) { // Aggiunto currentSearchCriteria come key
        if (initialSearchCriteria != null && initialSearchCriteria != currentSearchCriteria) {
            advancedSearchViewModel.updateAdvancedSearchCriteria(initialSearchCriteria)
        }
    }

    // Determina quali dati visualizzare in base ai criteri di ricerca
    val isAdvancedSearchActive = currentSearchCriteria != AdvancedSearchCriteria()

    val cardsToDisplay = if (isAdvancedSearchActive) advancedSearchResults else defaultCards
    val isLoadingDisplay = if (isAdvancedSearchActive) isSearchingAdvanced else isLoadingInitial
    val errorDisplay = if (isAdvancedSearchActive) advancedSearchError else initialError

    // Log per il debug (considera di rimuoverli o condizionarli per le build di produzione)
    LaunchedEffect(cardsToDisplay, isLoadingDisplay, errorDisplay, currentSearchCriteria) {
        Log.d(
            "InitCardsScreenView",
            "SearchCriteria: $currentSearchCriteria, Displaying ${cardsToDisplay.size} cards. " +
                    "Loading: $isLoadingDisplay, Error: $errorDisplay."
        )
    }
    LaunchedEffect(defaultCards) {
        if (defaultCards.isNotEmpty()) {
            Log.d("DEBUG_KEYS_DEFAULT", "Default cards IDs: ${defaultCards.map { it.id }.joinToString()}")
        }
    }
    LaunchedEffect(advancedSearchResults) {
        if (advancedSearchResults.isNotEmpty()) {
            Log.d("DEBUG_KEYS_SEARCH", "Search results IDs: ${advancedSearchResults.map { it.id }.joinToString()}")
        }
    }
    // CardsScreenView
    CardsScreenView(
        modifier = modifier,
        cards = cardsToDisplay,
        isLoading = false, //isLoading //se non vuoi gestire lo stato di caricamento
        errorMessage = errorDisplay,
        searchCriteria = currentSearchCriteria,
        onSearchCriteriaChange = { newCriteria ->
            advancedSearchViewModel.updateAdvancedSearchCriteria(newCriteria)
        },
        onCardItemClick = { cardId ->
            navController?.navigate(Screen.CardScreen.createRoute(cardId))
        },
        onToggleFavorite = { cardId ->
            favoritesViewModel.toggleFavoriteStatus(cardId)
        }
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
    onCardItemClick: (cardId: Int) -> Unit = {}, // Callback per il click sull'item
    onToggleFavorite: (cardId: Int) -> Unit = {}, // NUOVA callback per i preferiti
    searchAdvancedBoolean: Boolean=false
    ){
    Log.d("CardsScreenView", "Render. Cards: ${cards.size}, Loading: $isLoading, Error: $errorMessage, SearchCriteria: $searchCriteria")
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        TextFieldView(
            value = searchCriteria.name ?: "",
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
                onSearchCriteriaChange(newCriteria)
            },
            searchAdvancedBoolean = searchAdvancedBoolean
        )

        val isSearchActive = searchCriteria != AdvancedSearchCriteria()
        if (optionErrorView(
                modifier = Modifier.weight(1f), // Corretto: applica weight a questo specifico Composable
                isLoading = isLoading, // Questo isLoading è specifico per optionErrorView
                errorMessage = errorMessage,
                isEmpty = cards.isEmpty(),
                isSearchActive = isSearchActive
            )
        ) {
            SmallCardsListView(
                cards = cards,
                modifier = Modifier.weight(1f), // Questo è corretto per la Column interna
                onCardItemClick = { cardId ->
                    onCardItemClick(cardId)
                },
                onCardItemToggleFavorite = { cardId ->
                    onToggleFavorite(cardId)
                }
            )
        }
    }
}


@Composable
fun SmallCardsListView(
    cards: List<SmallPlayingCard>,
    modifier: Modifier = Modifier, // Modifier per la LazyVerticalGrid
    onCardItemClick: (cardId: Int) -> Unit = {}, // Callback per il click sull'item
    onCardItemToggleFavorite: (cardId: Int) -> Unit = {} // Callback per il toggle del preferito
) {
    Log.d("SmallCardsListView", "Displaying LazyVerticalGrid with ${cards.size} cards.")
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(), // Applica il modifier alla griglia
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { card -> card.id }) { card ->
            SmallCardItemView(
                card = card,
                onCardClick = {
                    onCardItemClick(card.id) // Chiama la callback passata
                },
                onToggleFavorite = {
                    onCardItemToggleFavorite(card.id) // Chiama la callback passata
                }
            )
        }
    }
}

@Composable
fun SmallCardItemView(
    modifier: Modifier = Modifier, // Modifier per l'intera SmallCardItemView
    card: SmallPlayingCard,
    onCardClick: () -> Unit={},
    onToggleFavorite: () -> Unit={},
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // Esempio: occupa tutta la larghezza disponibile
            .height(280.dp) // Esempio: altezza fissa per la carta
            .clickable(onClick = onCardClick) // Azione di click principale
    ) {
        Box(
            modifier = Modifier.fillMaxSize() // Il Box riempie la Card
        ) {
            CardUrltoView( // Supponendo che questa sia la tua Composable per l'immagine
                url = card.imageUrlSmall,
                modifier = Modifier.fillMaxSize() // L'immagine riempie il Box
            )

            // Overlay per l'icona preferito e l'ID
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart) // Allinea in basso a sinistra nel Box
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)) // Sfondo semi-trasparente
                    .padding(horizontal = 8.dp, vertical = 4.dp),verticalAlignment = Alignment.CenterVertically
            ) {
                val iconImage = if (card.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                val contentDesc = if (card.isFavorite) stringResource(R.string.isfavorite) else stringResource(R.string.notfavorite)

                IconButton(
                    onClick = onToggleFavorite, // Azione per il toggle del preferito
                    modifier = Modifier.size(32.dp) // IconButton per un touch target adeguato
                ) {
                    Icon(
                        imageVector = iconImage,
                        contentDescription = contentDesc,
                        modifier = Modifier.size(18.dp) // Dimensione effettiva dell'icona
                    )
                }

                Text(
                    text = card.id.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f), // Occupa lo spazio rimanente per evitare sovrapposizioni
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
/*
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

 */
// --- Preview ---


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



@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "SearchCriteriaDropdown - Vuoto", showBackground = true)
@Composable
fun PreviewSearchCriteriaDropdownVuoto() {
    YuGiDBTheme {
        val tipiCarta = mapOf("Qualsiasi" to null, "Mostro" to "Monster", "Magia" to "Spell")
        var tipoSelezionato by remember { mutableStateOf<String?>(null) }
        SearchCriteriaDropdown(
            label = "Tipo di Carta",
            selectedValueDisplay = tipiCarta.entries.find { it.value == tipoSelezionato }?.key ?: "Qualsiasi",
            items = tipiCarta,
            onItemSelected = { tipoSelezionato = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "TextFieldView - Base", showBackground = true)
@Composable
fun PreviewTextFieldViewBase() {
    YuGiDBTheme {
        var testoRicerca by remember { mutableStateOf("Drago Bianco") }
        var criteri by remember { mutableStateOf(AdvancedSearchCriteria()) }
        TextFieldView(
            value = testoRicerca,
            onValueChange = { testoRicerca = it },
            label = { Text("Nome Carta") },
            searchCriteria = criteri,
            onSearchCriteriaChange = { criteri = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "TextFieldView - Avanzata Aperta", showBackground = true, locale = "it")
@Composable
fun PreviewTextFieldViewAvanzata() {
    YuGiDBTheme {
        var testoRicerca by remember { mutableStateOf("") }
        var criteri by remember { mutableStateOf(AdvancedSearchCriteria(type = "Spell Card", isFavorite = true)) }
        Column(Modifier.padding(16.dp)) {
            TextFieldView(
                value = testoRicerca,
                onValueChange = { testoRicerca = it },
                label = { Text(stringResource(R.string.search_bar_label_name_hint)) }, // Assicurati che R.string esista
                searchCriteria = criteri,
                onSearchCriteriaChange = { criteri = it },
                searchAdvancedBoolean= true,
            )
            }
    }
}

@Preview(name = "SmallCardItemView - Non Preferito", showBackground = true)
@Composable
fun PreviewSmallCardItemViewNonPreferito() {
    YuGiDBTheme {
        SmallCardItemView(
            card = SmallPlayingCard(id = 123, name = "Drago Bianco Occhi Blu", imageUrlSmall = "url_piccolo_esempio", isFavorite = false),
            onCardClick = {},
            onToggleFavorite = {},
            modifier = Modifier.width(180.dp).padding(8.dp) // Simula la dimensione in una griglia
        )
    }
}


@Preview(name = "SmallCardsListView - Con Carte", showBackground = true)
@Composable
fun PreviewSmallCardsListViewConCarte() {
    YuGiDBTheme {
        val carteEsempio = listOf(
            SmallPlayingCard(id = 1, name = "Carta 1", imageUrlSmall = "url1", isFavorite = false),
            SmallPlayingCard(id = 2, name = "Carta 2", imageUrlSmall = "url2", isFavorite = true),
            SmallPlayingCard(id = 3, name = "Carta 3 Molto Lunga da Scrivere per Vedere Ellipsis", imageUrlSmall = "url3", isFavorite = false),
            SmallPlayingCard(id = 4, name = "Carta 4", imageUrlSmall = "url4", isFavorite = true)
        )
        SmallCardsListView(
            cards = carteEsempio,
            onCardItemClick = {},
            onCardItemToggleFavorite = {}
        )
    }
}



@Preview(name = "CardsScreenView - Base", showBackground = true, locale="it")
@Composable
fun PreviewCardsScreenViewBase(modifier: Modifier = Modifier) {
    YuGiDBTheme {
        val carteEsempio = listOf(
            SmallPlayingCard(id = 1, name = "Exodia il Proibito", imageUrlSmall = "url_exodia", isFavorite = true),
            SmallPlayingCard(id = 2, name = "Kuriboh", imageUrlSmall = "url_kuriboh", isFavorite = false)
        )
        var criteri by remember { mutableStateOf(AdvancedSearchCriteria(name = "Test")) }
        CardsScreenView(
            modifier = modifier,
            cards = carteEsempio,
            isLoading = false,
            errorMessage = null,
            searchCriteria = criteri,
            onSearchCriteriaChange = { criteri = it },
            onCardItemClick = {},
            onToggleFavorite = {}
        )
    }
}

@Preview(name = "CardsScreenView - Caricamento", showBackground = true, locale="it")
@Composable
fun PreviewCardsScreenViewCaricamento() {
    YuGiDBTheme {
        var criteri by remember { mutableStateOf(AdvancedSearchCriteria()) }
        CardsScreenView(
            cards = emptyList(),
            isLoading = true, // Simula caricamento
            errorMessage = null,
            searchCriteria = criteri,
            onSearchCriteriaChange = { criteri = it },
            onCardItemClick = {},
            onToggleFavorite = {}
        )
    }
}

@Preview(name = "CardsScreenView - Errore", showBackground = true, locale="it")
@Composable
fun PreviewCardsScreenViewErrore() {
    YuGiDBTheme {
        var criteri by remember { mutableStateOf(AdvancedSearchCriteria(name = "Ricerca Fallita")) }
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Errore durante il caricamento delle carte. Riprova più tardi.",
            searchCriteria = criteri,
            onSearchCriteriaChange = { criteri = it },
            onCardItemClick = {},
            onToggleFavorite = {}
        )
    }
}

