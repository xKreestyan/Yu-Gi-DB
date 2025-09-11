package com.example.yu_gi_db.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement // Import necessario
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope // Import necessario per RowScope
import androidx.compose.foundation.layout.Spacer // Import per Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.ui.theme.AppTypography // NUOVO IMPORT
import com.example.yu_gi_db.ui.theme.LightSilver
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.ui.theme.YugiohCardNameDisplay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.unit.times
// Costanti per il layout degli attributi
private val TEXT_START_END_PADDING = 8.dp
private val LABEL_TEXT_WIDTH = 75.dp
private val SPACE_AROUND_SEPARATOR = 4.dp
private val SEPARATOR_LINE_THICKNESS = 2.dp
private val ATTRIBUTE_SEPARATOR_LINE_OFFSET = TEXT_START_END_PADDING + LABEL_TEXT_WIDTH + SPACE_AROUND_SEPARATOR
private val VALUE_AREA_START_PADDING = SPACE_AROUND_SEPARATOR + SEPARATOR_LINE_THICKNESS + SPACE_AROUND_SEPARATOR

@Composable
private fun ClickableValueText(
    text: String,
    navController: NavHostController?,
    searchScreenRoute: String?,
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
        modifier = if (navController != null && searchScreenRoute != null) {
            Modifier.clickable {
                navController.navigate(searchScreenRoute) {
                    // Considerare popUpTo e launchSingleTop se necessario per la navigazione
                }
            }
        } else {
            Modifier
        }
    )
}


@Composable
fun LargeCradUI(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null,
    maxDescriptionHeight: Dp = 200.dp
) {
    val currentCard = card ?: return

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.largecard_portrait),
            contentDescription = stringResource(R.string.background_image_description),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val (cardNameBoxRef, frameRef) = createRefs()

            YugiohCardNameDisplay(
                cardName = currentCard.name,
                modifier = Modifier.constrainAs(cardNameBoxRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.wrapContent
                }
            )

            Card(
                modifier = Modifier
                    .constrainAs(frameRef) {
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
                        painter = painterResource(id = R.drawable.sfondo_cornice),
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
                            lineaVerticaleRef, // A destra dell'immagine
                            lineaOrizzontaleRef, // Principale orizzontale
                            attributeLabelValueSeparatorRef // Nuova linea per Etichetta/Valore
                        ) = createRefs()

                        val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
                        val imageUrl: String = firstCardImage?.imageUrlSmall ?: ""

                        CardUrltoView(
                            url = imageUrl,
                            modifier = Modifier
                                .size(150.dp, 202.dp)
                                .clickable(enabled = navController != null && imageUrl.isNotEmpty()) {
                                    imageUrl.let { url ->
                                        val encodedUrl =
                                            URLEncoder.encode(
                                                url,
                                                StandardCharsets.UTF_8.toString()
                                            )
                                        navController?.navigate(
                                            Screen.ZoomCardScreen.createRoute(
                                                encodedUrl
                                            )
                                        )
                                    }
                                }
                                .constrainAs(cardImageRef) {
                                    top.linkTo(
                                        parent.top,
                                        margin = 8.dp
                                    ) // Immagine con margine superiore
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

                        val attributeSlotsAreaHeight = 202.dp + 8.dp + 8.dp
                        val totalDividersHeightInColumn = 3 * SEPARATOR_LINE_THICKNESS
                        val heightPerWeightedSlot = (attributeSlotsAreaHeight - totalDividersHeightInColumn) / 4

                        Box(
                            modifier = Modifier
                                .constrainAs(attributeLabelValueSeparatorRef) {
                                    top.linkTo(parent.top, margin = heightPerWeightedSlot)
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

                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = TEXT_START_END_PADDING), 
                                    horizontalArrangement = Arrangement.Center, 
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val combinedText = currentCard.type + if (currentCard.race.isNotEmpty()) " / ${currentCard.race}" else ""
                                    ClickableValueText(
                                        text = combinedText,
                                        navController = navController,
                                        searchScreenRoute = Screen.DataBaseAdvancedSearch.createRouteForType(type = currentCard.type),
                                        color = LightSilver, 
                                        maxLines = 3, 
                                        overflow = TextOverflow.Ellipsis, 
                                        textAlign = TextAlign.Center 
                                    )
                                }
                            }

                            AttributeDivider()

                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                AttributeLabel(stringResource(R.string.card_label_attribute)) 

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = VALUE_AREA_START_PADDING,
                                            end = TEXT_START_END_PADDING
                                        )
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center, 
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    currentCard.attribute?.let { attributeValue ->
                                        if (attributeValue.equals("OSCURITÀ", ignoreCase = true)) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally 
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.oscurita), 
                                                    contentDescription = stringResource(R.string.attribute_icon_content_description, attributeValue),
                                                    modifier = Modifier.size(24.dp) 
                                                )
                                                Spacer(modifier = Modifier.height(2.dp)) 
                                                Text(
                                                    text = attributeValue,
                                                    style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        } else {
                                            ClickableValueText(
                                                text = attributeValue,
                                                navController = navController,
                                                searchScreenRoute = Screen.DataBaseAdvancedSearch.createRouteForAttribute(attribute = attributeValue)
                                            )
                                        }
                                    }
                                }
                            }

                            AttributeDivider()

                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                AttributeLabel(stringResource(R.string.card_label_level)) 
                                currentCard.level?.let { levelValue ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(
                                                start = VALUE_AREA_START_PADDING,
                                                end = TEXT_START_END_PADDING
                                            )
                                            .fillMaxWidth(), 
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center 
                                    ) {
                                        Text(
                                            text = levelValue.toString(),
                                            style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.level),
                                            contentDescription = stringResource(R.string.level_icon_content_description),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            AttributeDivider()

                            AttributeSlotRow(modifier = Modifier.weight(1f), minHeight = slotMinHeight) {
                                AttributeLabel(stringResource(R.string.card_label_atk_def))
                                AttributeValues(modifier = Modifier.weight(1f)) {
                                    if (currentCard.atk != null || currentCard.def != null) {
                                        val atkValue = currentCard.atk?.toString() ?: "N/A"
                                        val defValue = currentCard.def?.toString() ?: "N/A"
                                        Text(
                                            text = "$atkValue / $defValue",
                                            style = MaterialTheme.typography.bodyLarge.copy(color = LightSilver),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .constrainAs(descriptionFrameRef) {
                                    top.linkTo(lineaOrizzontaleRef.bottom, margin = 8.dp)
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
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = LightSilver,
                                        fontSize = 20.sp
                                    ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentCard.desc,
                                    style = AppTypography.bodyMedium.copy(
                                        color = LightSilver,
                                        fontSize = 20.sp
                                    ),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
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


@Preview(showBackground = true, name = "LargeCardUI Preview")
@Composable
fun LargeCardUIPreview() {
    val sampleCard = LargePlayingCard(
        id = 33396948, 
        name = "Exodia il Proibito",
        race = "Effetto",
        desc = "Se hai \"Gamba Dx del Proibito\", \"Gamba Sx del Proibito\", \"Braccio Dx del Proibito\" e \"Braccio Sx del Proibito\" in aggiunta a questa carta nella tua mano, vinci il Duello.",
        type = "Incantatore",
        atk = 1000,
        def = 1000,
        level = 3,
        attribute = "OSCURITÀ",
        cardImages = listOf(CardImage(1, "", "https://images.ygoprodeck.com/images/cards_small/33396948.jpg", "")),
        typeline = emptyList(),
        humanReadableCardType = "Spellcaster/Effect",
        frameType = "effect",
        cardSets = emptyList(),
        cardPrices = emptyList()
    )

    YuGiDBTheme {
        LargeCradUI(
            card = sampleCard,
            navController = null
        )
    }
}
