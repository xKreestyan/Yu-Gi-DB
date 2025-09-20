package com.example.yu_gi_db.views

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.CardSet
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.ui.theme.AppTypography
import com.example.yu_gi_db.ui.theme.LightSilver
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.ui.theme.YugiohCardNameDisplay
import com.example.yu_gi_db.viewmodels.CardDetailViewModel
import com.example.yu_gi_db.viewmodels.FavoritesViewModel
import com.example.yu_gi_db.views.navigation.Screen
import java.net.URLEncoder

// Costanti per il layout degli attributi
private val TEXT_START_END_PADDING = 8.dp
private val LABEL_TEXT_WIDTH = 75.dp
private val SPACE_AROUND_SEPARATOR = 4.dp
private val SEPARATOR_LINE_THICKNESS = 2.dp
private val ATTRIBUTE_SEPARATOR_LINE_OFFSET = TEXT_START_END_PADDING + LABEL_TEXT_WIDTH + SPACE_AROUND_SEPARATOR
private val VALUE_AREA_START_PADDING = SPACE_AROUND_SEPARATOR + SEPARATOR_LINE_THICKNESS + SPACE_AROUND_SEPARATOR



@Composable
fun InitLargeCardView(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController? = null,
    viewModel: CardDetailViewModel = hiltViewModel()
) {
    // Osserva gli stati necessari dal ViewModel
    val largeCard by viewModel.selectedLargeCard.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingLargeCard.collectAsStateWithLifecycle()
    val error by viewModel.largeCardError.collectAsStateWithLifecycle()

    // Effetto per caricare i dati quando cardId cambia o alla prima composizione
    LaunchedEffect(cardId) {
        viewModel.fetchLargeCardById(cardId)
    }
    // Effetto per pulire i dati quando la schermata viene rimossa dalla composizione
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedLargeCard()
        }
    }
    LargeCardView(
        modifier = modifier,
        largeCard = largeCard, // largeCard contiene già il nome e altri dettagli
        isLoading = isLoading,
        error = error,
        navController = navController
    )
}

@Composable
fun LargeCardView(
    modifier: Modifier = Modifier,
    largeCard: LargePlayingCard?,
    isLoading: Boolean,
    error: String?,
    navController: NavHostController?,
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
    ) {
    // La logica di optionErrorView gestisce la visualizzazione di caricamento/errore/vuoto
    if (optionErrorView(
            modifier = modifier, // Il modifier (con padding) viene passato qui
            isLoading = isLoading,
            errorMessage = error,
            isEmpty = (largeCard == null && !isLoading && error == null) // Condizione di isEmpty più precisa
        )
    ) {
        var boolFavorites by remember { mutableStateOf( largeCard?.isFavorite ?: false) }
        largeCard?.let { cardData ->
            LargeCardUI(
                card = cardData,
                modifier = modifier.fillMaxSize(), // o un modifier più specifico se necessario
                navController = navController,
                onLevelClick = { level ->
                    navController?.navigate(Screen.DataBaseAdvancedSearch.createRouteForLevel(level = level))
                },
                onFavoriteToggle = {
                    boolFavorites = !boolFavorites
                    favoritesViewModel.toggleFavoriteStatus(cardData.id)
                },
                isFavorite =boolFavorites
            )
        }
    }
}

@Composable
fun CardZoomView(
    url: String,
    modifier: Modifier = Modifier,
) {
    val zoomStep = 0.5f
    val minZoom = 1.0f
    val maxZoom = 5.0f

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    fun updateZoom(newScale: Float) {
        val coercedScale = newScale.coerceIn(minZoom, maxZoom)
        if (scale != coercedScale) {
            if (coercedScale == minZoom) {
                offset = Offset.Zero
            }
            scale = coercedScale
        }
    }
    Box(
        modifier =  modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        val newScaleTarget = if (scale > minZoom) minZoom else 2f
                        updateZoom(newScaleTarget)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    if (scale > minZoom) {
                        val newPotentialOffset = offset.plus(pan)
                        val maxAllowedTranslateX =
                            (this.size.width * (scale) / 2f).coerceAtLeast(0f)
                        val maxAllowedTranslateY =
                            (this.size.height * (scale - 1) / 2f).coerceAtLeast(0f)
                        offset = Offset(
                            x = newPotentialOffset.x.coerceIn(
                                -maxAllowedTranslateX,
                                maxAllowedTranslateX
                            ),
                            y = newPotentialOffset.y.coerceIn(
                                -maxAllowedTranslateY,
                                maxAllowedTranslateY
                            )
                        )
                    }
                }
            }
    )
    {
        // Box contenente l'immagine, che permette di applicare lo zoom e il pan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentAlignment = Alignment.Center
        ) {
            CardUrltoView(
                url = url,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Pulsanti di Zoom sovrapposti
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd) // Posiziona i pulsanti in basso a destra
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { updateZoom(scale + zoomStep) },
                modifier = Modifier
                    .graphicsLayer(shadowElevation = 8f, shape = MaterialTheme.shapes.small)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        MaterialTheme.shapes.small
                    )

            ) {
                Text("+", Modifier.scale(5f))
            }
            IconButton(
                onClick = { updateZoom(scale - zoomStep) },
                modifier = Modifier
                    .graphicsLayer(shadowElevation = 8f, shape = MaterialTheme.shapes.small)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        MaterialTheme.shapes.small
                    )
            ) {
                Text("-", Modifier.scale(5f))
            }
        }
    }
}


@Composable
private fun ClickableValueText(
    text: String,
    onClick: (() -> Unit)? = null,
    textStyle: ComposeTextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        style = textStyle.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign ?: TextAlign.Start,
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        }
    )
}

@Composable
fun LargeCardUI(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null,
    maxDescriptionHeight: Dp = 200.dp,
    onLevelClick: ((Int) -> Unit)? = null,
    isFavorite: Boolean = false,
    onFavoriteToggle: (() -> Unit)? = null
) {
    val currentCard = card ?: return
    val configuration = LocalConfiguration.current

    // Determina il tipo di carta basandosi su stringhe API standard (inglese)
    val cardApiType = currentCard.type

    val isSpell = cardApiType.equals("Spell Card", ignoreCase = true)
    val isTrap = cardApiType.equals("Trap Card", ignoreCase = true)

    val monsterApiTypes = listOf(
        "Normal Monster", "Effect Monster", "Flip Effect Monster",
        "Fusion Monster", "Ritual Monster", "Synchro Monster",
        "XYZ Monster", "Pendulum Effect Monster", "Link Monster", "Token"
    )
    val isMonster = monsterApiTypes.any { apiType ->
        cardApiType.equals(apiType, ignoreCase = true)
    }

    val backgroundImageRes = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        R.drawable.largecard_landscape
    } else {
        R.drawable.largecard_portrait
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = stringResource(R.string.background_image_description),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Scroll interno a LargeCardUI
                .padding(16.dp)
        ) {
            val (cardNameBoxRef, mainCardVisualFrameRef, cardSetsSectionRef, cardPricesSectionRef) = createRefs()

            YugiohCardNameDisplay(
                cardName = currentCard.name,
                isFavorite = isFavorite,
                onFavoriteClick = { onFavoriteToggle?.invoke() },
                modifier = Modifier.constrainAs(cardNameBoxRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.wrapContent
                }
            )

            Card(
                modifier = Modifier
                    .constrainAs(mainCardVisualFrameRef) {
                        top.linkTo(cardNameBoxRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.preferredWrapContent
                        height = Dimension.preferredWrapContent
                    }
                    .widthIn(min = 320.dp, max = 360.dp),
                shape = RectangleShape,
                border = BorderStroke(2.dp, LightSilver),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.sfondo_cornice), // Sfondo cornice rimane invariato
                        contentDescription = stringResource(R.string.frame_background_image_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (
                            cardImageRef,
                            attributesColumnRef,
                            descriptionFrameRef,
                            lineaVerticaleRef,
                            lineaOrizzontaleRef,
                            attributeLabelValueSeparatorRef,
                            newSeparatorRef
                        ) = createRefs()


                        val guidelineOffsetDp = if (isMonster) {
                            52.dp + SEPARATOR_LINE_THICKNESS // Mostro: sotto la prima riga e il suo divisore (26dp + 2dp = 28dp)
                        } else {
                            0.dp // Magia/Trappola: all'inizio della colonna attributi
                        }
                        val dynamicAttributeSeparatorTopGuideline = createGuidelineFromTop(offset = guidelineOffsetDp)

                        val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
                        val imageUrl: String = firstCardImage?.imageUrlSmall ?: ""

                        CardUrltoView(
                            url = imageUrl,
                            modifier = Modifier
                                .size(150.dp, 202.dp)
                                .clickable(enabled = navController != null && imageUrl.isNotEmpty()) {
                                    imageUrl.let { url ->
                                        val encodedUrl =
                                            URLEncoder.encode(url, "UTF-8")
                                        navController?.navigate(Screen.ZoomCardScreen.createRoute(encodedUrl))
                                    }
                                }
                                .constrainAs(cardImageRef) {
                                    top.linkTo(parent.top, margin = 8.dp)
                                    start.linkTo(parent.start, margin = 8.dp)
                                }
                        )

                        Box(
                            modifier = Modifier
                                .constrainAs(lineaOrizzontaleRef) {
                                    top.linkTo(cardImageRef.bottom, margin = 8.dp)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    height = Dimension.value(SEPARATOR_LINE_THICKNESS)
                                    width = Dimension.fillToConstraints
                                }
                                .background(LightSilver)
                        )

                        Box(
                            modifier = Modifier
                                .constrainAs(lineaVerticaleRef) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(lineaOrizzontaleRef.top)
                                    start.linkTo(cardImageRef.end, margin = 8.dp)
                                    height = Dimension.fillToConstraints
                                    width = Dimension.value(SEPARATOR_LINE_THICKNESS)
                                }
                                .background(LightSilver)
                        )

                        Box(
                            modifier = Modifier
                                .constrainAs(attributeLabelValueSeparatorRef) {
                                    top.linkTo(dynamicAttributeSeparatorTopGuideline) // Vincolato alla guideline dinamica
                                    bottom.linkTo(lineaOrizzontaleRef.top)
                                    start.linkTo(
                                        lineaVerticaleRef.end,
                                        margin = ATTRIBUTE_SEPARATOR_LINE_OFFSET
                                    )
                                    height = Dimension.fillToConstraints
                                    width = Dimension.value(SEPARATOR_LINE_THICKNESS)
                                }
                                .background(LightSilver)
                        )

                        Column(
                            modifier = Modifier.constrainAs(attributesColumnRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(lineaOrizzontaleRef.top)
                                start.linkTo(lineaVerticaleRef.end)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                        ) {
                            val slotMinHeight = 24.dp
                            // Riga Tipo / Stirpe (Sempre visibile)
                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = TEXT_START_END_PADDING),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val typeDisplayRowText = currentCard.type + (if (currentCard.race.isNotEmpty()) " / ${currentCard.race}" else "")
                                    ClickableValueText(
                                        text = typeDisplayRowText,
                                        onClick = {
                                            navController?.navigate(Screen.DataBaseAdvancedSearch.createRouteForType(type = currentCard.type))
                                        },
                                        color = LightSilver,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            AttributeDivider()
                            // Riga Attributo (Sempre visibile, mostra icona Magia/Trappola o attributo Mostro)
                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                AttributeLabel(stringResource(R.string.card_label_attribute))
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = VALUE_AREA_START_PADDING, end = TEXT_START_END_PADDING)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val attributeValue = currentCard.attribute
                                    var iconResIdToDisplay: Int? = null
                                    var textToDisplayForAttribute: String? = attributeValue

                                    if (isSpell) {
                                        iconResIdToDisplay = R.drawable.spell
                                        textToDisplayForAttribute = stringResource(R.string.card_type_spell)
                                    } else if (isTrap) {
                                        iconResIdToDisplay = R.drawable.trap
                                        textToDisplayForAttribute = stringResource(R.string.card_type_trap)
                                    } else if (attributeValue != null) { // Monster
                                        iconResIdToDisplay = when (attributeValue.uppercase()) {
                                            "LIGHT" -> R.drawable.luce
                                            "DARK" -> R.drawable.oscurita
                                            "WIND" -> R.drawable.vento
                                            "FIRE" -> R.drawable.fuoco
                                            "WATER" -> R.drawable.acqua
                                            "EARTH" -> R.drawable.terra
                                            // Mappature italiane aggiunte correttamente
                                            "LUCE" -> R.drawable.luce
                                            "OSCURITÀ" -> R.drawable.oscurita
                                            "VENTO" -> R.drawable.vento
                                            "FUOCO" -> R.drawable.fuoco
                                            "ACQUA" -> R.drawable.acqua
                                            "TERRA" -> R.drawable.terra
                                            else -> null // Default per attributi non mappati (es. "DIVINE")
                                        }
                                    }

                                    if (iconResIdToDisplay != null) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = if (isMonster && attributeValue != null) {
                                                Modifier.clickable {
                                                    navController?.navigate(Screen.DataBaseAdvancedSearch.createRouteForAttribute(attribute = attributeValue))
                                                }
                                            } else Modifier
                                        ) {
                                            Image(
                                                painter = painterResource(id = iconResIdToDisplay),
                                                contentDescription = stringResource(R.string.attribute_icon_content_description, textToDisplayForAttribute ?: currentCard.type),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            textToDisplayForAttribute?.let {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else if (textToDisplayForAttribute != null) { // Mostro con attributo senza icona (es. DIVINE) o testo per Magia/Trappola
                                        ClickableValueText(
                                            text = textToDisplayForAttribute,
                                            onClick = {
                                                if (isMonster) navController?.navigate(Screen.DataBaseAdvancedSearch.createRouteForAttribute(attribute = textToDisplayForAttribute))
                                            },
                                            textStyle = MaterialTheme.typography.bodyLarge,
                                            color = LightSilver,
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = "-",
                                            style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            if (isMonster) {
                                AttributeDivider()
                                AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                    AttributeLabel(stringResource(R.string.card_label_level))
                                    currentCard.level?.let { levelValue ->
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = VALUE_AREA_START_PADDING, end = TEXT_START_END_PADDING)
                                                .fillMaxWidth()
                                                .clickable(enabled = onLevelClick != null) {
                                                    onLevelClick?.invoke(levelValue)
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = levelValue.toString(), style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Image(painter = painterResource(id = R.drawable.level), contentDescription = stringResource(R.string.level_icon_content_description), modifier = Modifier.size(18.dp))
                                        }
                                    } ?: Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = VALUE_AREA_START_PADDING, end = TEXT_START_END_PADDING)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "-", style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver))
                                    }
                                }
                                AttributeDivider()
                                AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                    AttributeLabel(stringResource(R.string.card_label_atk_def))
                                    AttributeValues(modifier = Modifier.weight(1f)) {
                                        val atkValue = currentCard.atk?.toString() ?: "N/A"
                                        val defValue = currentCard.def?.toString() ?: "N/A"
                                        Text(text = "$atkValue / $defValue", style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            } // Fine blocco if(isMonster)
                        }

                        // Nuovo HorizontalDivider con riferimento newSeparatorRef
                        HorizontalDivider(
                            modifier = Modifier.constrainAs(newSeparatorRef) {
                                top.linkTo(lineaOrizzontaleRef.bottom, margin = 4.dp) // Vincolato sotto lineaOrizzontaleRef
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                            thickness = 1.dp,
                            color = LightSilver.copy(alpha = 0.5f)
                        )

                        Card(
                            modifier = Modifier
                                .constrainAs(descriptionFrameRef) {
                                    top.linkTo(newSeparatorRef.bottom, margin = 8.dp) // Aggiornato per vincolare a newSeparatorRef
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                    height = Dimension.preferredWrapContent
                                }
                                .heightIn(max = maxDescriptionHeight),
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = stringResource(R.string.card_description),
                                    style = MaterialTheme.typography.titleSmall.copy(color = LightSilver, fontSize = 20.sp),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentCard.desc,
                                    style = AppTypography.bodyMedium.copy(color = LightSilver, fontSize = 20.sp),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            if (currentCard.cardSets.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .constrainAs(cardSetsSectionRef) {
                            top.linkTo(mainCardVisualFrameRef.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 8.dp),
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, LightSilver.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = stringResource(R.string.card_section_title_appears_in),
                            style = MaterialTheme.typography.titleMedium.copy(color = LightSilver, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        currentCard.cardSets.forEachIndexed { index, cardSet ->
                            CardSetItemView(cardSet)
                            if (index < currentCard.cardSets.size - 1) {
                                HorizontalDivider(color = LightSilver.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            val firstPriceInfo = currentCard.cardPrices.firstOrNull()
            if (firstPriceInfo != null) {
                Card(
                    modifier = Modifier
                        .constrainAs(cardPricesSectionRef) {
                            top.linkTo(if (currentCard.cardSets.isNotEmpty()) cardSetsSectionRef.bottom else mainCardVisualFrameRef.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 8.dp),
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, LightSilver.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = stringResource(R.string.card_section_title_market_prices),
                            style = MaterialTheme.typography.titleMedium.copy(color = LightSilver, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        PriceItemView(label = stringResource(R.string.price_label_cardmarket), value = firstPriceInfo.cardmarketPrice)
                        PriceItemView(label = stringResource(R.string.price_label_tcgplayer), value = firstPriceInfo.tcgplayerPrice)
                        PriceItemView(label = stringResource(R.string.price_label_ebay), value = firstPriceInfo.ebayPrice)
                        PriceItemView(label = stringResource(R.string.price_label_amazon), value = firstPriceInfo.amazonPrice)
                        PriceItemView(label = stringResource(R.string.price_label_coolstuffinc), value = firstPriceInfo.coolstuffincPrice)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSetItemView(cardSet: CardSet) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = cardSet.setName,
            style = AppTypography.bodyLarge.copy(color = LightSilver, fontWeight = FontWeight.SemiBold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "${stringResource(R.string.set_item_label_code)} ${cardSet.setCode}",
                style = AppTypography.bodyMedium.copy(color = LightSilver.copy(alpha = 0.8f))
            )
            Text(
                text = "${stringResource(R.string.set_item_label_rarity)} ${cardSet.setRarity} ${cardSet.setRarityCode ?: ""}",
                style = AppTypography.bodyMedium.copy(color = LightSilver.copy(alpha = 0.8f)),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = "${stringResource(R.string.set_item_label_price)} ${cardSet.setPrice}",
            style = AppTypography.bodyMedium.copy(color = LightSilver.copy(alpha = 0.8f))
        )
    }
}

@Composable
private fun PriceItemView(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium.copy(color = LightSilver, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(color = LightSilver),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun AttributeSlotRow(
    modifier: Modifier = Modifier,
    minHeight: Dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight)
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun AttributeLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver),
        modifier = Modifier
            .padding(start = TEXT_START_END_PADDING)
            .width(LABEL_TEXT_WIDTH),
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AttributeValues(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .padding(start = VALUE_AREA_START_PADDING, end = TEXT_START_END_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        content()
    }
}

@Composable
private fun AttributeDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = SEPARATOR_LINE_THICKNESS,
        color = LightSilver
    )
}

@Preview(showBackground = true, name = "LargeCardUI Previews - All Types")
@Composable
fun LargeCardUIPreview() {
    var isFavoriteState by remember { mutableStateOf(false) }
    val monsterCard = LargePlayingCard(
        id = 33396948,
        name = "Exodia il Proibito",
        race = "Incantatore",
        desc = "Se hai \"Gamba Dx del Proibito\", \"Gamba Sx del Proibito\", \"Braccio Dx del Proibito\" e \"Braccio Sx del Proibito\" in aggiunta a questa carta nella tua mano, vinci il Duello.",
        type = "Effect Monster", // CORRETTO: Standard API Type
        atk = 1000,
        def = 1000,
        level = 3,
        attribute = "OSCURITÀ", // Mantenuto in italiano, ora dovrebbe funzionare
        isFavorite = isFavoriteState,
        cardImages = listOf(CardImage(33396948, "", "https://images.ygoprodeck.com/images/cards_small/33396948.jpg", "")),
        typeline = listOf("Incantatore", "Effetto"),
        humanReadableCardType = "[Incantatore/Effetto]",
        frameType = "effect",
        cardSets = emptyList(),
        cardPrices = emptyList()
    )
    val spellCard = monsterCard.copy(
        id = 46986414, name = "Raigeki", type = "Spell Card", race = "Normale", attribute = null, // CORRETTO: Standard API Type
        atk = null, def = null, level = null, frameType = "spell",
        typeline = listOf("Magia", "Normale"),
        desc = "Distruggi tutti i mostri controllati dal tuo avversario."
    )
    val trapCard = monsterCard.copy(
        id = 41408750, name = "Forza Riflessa", type = "Trap Card", race = "Normale", attribute = null, // CORRETTO: Standard API Type
        atk = null, def = null, level = null, frameType = "trap",
        typeline = listOf("Trappola", "Normale"),
        desc = "Quando un mostro dell'avversario dichiara un attacco: distruggi tutti i mostri in Posizione di Attacco controllati dal tuo avversario."
    )

    YuGiDBTheme {
        // RIMOSSO .verticalScroll(rememberScrollState()) da questa Column
        Column(Modifier.padding(8.dp)) {
            Text("Anteprima Mostro:", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            LargeCardUI(
                card = monsterCard,
                navController = null,
                onLevelClick = { println("Level clicked: $it") },
                isFavorite = isFavoriteState,
                onFavoriteToggle = { isFavoriteState = !isFavoriteState }
            )
            Spacer(Modifier.height(16.dp))
            Text("Anteprima Magia:", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            LargeCardUI(card = spellCard, navController = null)
            Spacer(Modifier.height(16.dp))
            Text("Anteprima Trappola:", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            LargeCardUI(card = trapCard, navController = null)
        }
    }
}

@Preview(showBackground = true, name = "LargeCardUI Preview - Spell Card")
@Composable
fun LargeCardUISpellPreview() {
    val spellCard = LargePlayingCard(
        id = 46986414,
        name = "Raigeki",
        type = "Spell Card", // CORRETTO: Standard API Type
        race = "Normale", // Tipo di icona per Magie/Trappole
        desc = "Distruggi tutti i mostri controllati dal tuo avversario.",
        atk = null,
        def = null,
        level = null,
        attribute = null, // Le Magie non hanno attributo elementale
        isFavorite = false,
        cardImages = listOf(CardImage(46986414, "", "https://images.ygoprodeck.com/images/cards_small/46986414.jpg", "")),
        typeline = listOf("Magia", "Normale"),
        humanReadableCardType = "[Carta Magia]",
        frameType = "spell",
        cardSets = emptyList(),
        cardPrices = emptyList()
    )

    YuGiDBTheme {
        LargeCardUI(card = spellCard, navController = null)
    }
}