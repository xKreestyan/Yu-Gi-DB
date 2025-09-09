package com.example.yu_gi_db.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // Import per Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer // Import per Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth // Import per fillMaxWidth
import androidx.compose.foundation.layout.height // Import per height (Spacer)
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.ui.theme.RoyalBlueDark // Import colore per la cornice
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.ui.theme.YugiohCardNameDisplay 
import com.example.yu_gi_db.views.CardUrltoView
import com.example.yu_gi_db.views.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun LargeCradUI(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null
) {
    val currentCard = card ?: return

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.largecard_portrait),
            contentDescription = stringResource(R.string.background_image_description),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ConstraintLayout( // ConstraintLayout Principale
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

            // Nuova Card per la cornice
            Card(
                modifier = Modifier.constrainAs(frameRef) {
                    top.linkTo(cardNameBoxRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent // L'altezza si adatterà al contenuto
                },
                shape = RectangleShape,
                border = BorderStroke(2.dp, RoyalBlueDark),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                // ConstraintLayout Annidato per il contenuto della cornice
                ConstraintLayout(
                    modifier = Modifier
                        .padding(8.dp) // Padding interno della cornice
                        .fillMaxWidth() // Il CL annidato riempie la Card-cornice
                ) {
                    val (
                        cardImageRef, 
                        attributesColumnRef, 
                        descriptionTextRef
                    ) = createRefs()

                    val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
                    val imageUrl: String = firstCardImage?.imageUrlSmall ?: ""

                    // Immagine della carta (in alto a sinistra nella cornice)
                    CardUrltoView(
                        url = imageUrl,
                        modifier = Modifier
                            .size(150.dp, 202.dp) // Dimensioni ridotte per fare spazio agli attributi
                            .clickable(enabled = navController != null && imageUrl.isNotEmpty()) {
                                imageUrl.let { url ->
                                    val encodedUrl =
                                        URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController?.navigate(Screen.ZoomCardScreen.createRoute(encodedUrl))
                                }
                            }
                            .constrainAs(cardImageRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                    )

                    // Colonna per gli attributi (a destra dell'immagine)
                    Column(
                        modifier = Modifier.constrainAs(attributesColumnRef) {
                            top.linkTo(cardImageRef.top)
                            start.linkTo(cardImageRef.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints // La colonna riempie lo spazio rimanente
                            // L'altezza si adatterà al contenuto della colonna
                        }
                    ) {
                        // Tipo e Razza
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            // modifier = Modifier.fillMaxWidth() // Opzionale se vuoi che il Row si espanda
                        ) {
                            ClickableSearchText(
                                label = stringResource(R.string.card_label_type),
                                value = currentCard.type,
                                navController = navController,
                                searchCriteriaAction = {
                                    Screen.DataBaseAdvancedSearch.createRouteForType(type = currentCard.type)
                                }
                            )
                            if (currentCard.race.isNotEmpty()) {
                                Text(" / ", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = currentCard.race,
                                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))

                        // Attributo
                        if (currentCard.attribute != null) {
                            ClickableSearchText(
                                label = stringResource(R.string.card_label_attribute),
                                value = currentCard.attribute,
                                navController = navController,
                                searchCriteriaAction = { 
                                    Screen.DataBaseAdvancedSearch.createRouteForAttribute(attribute = currentCard.attribute) 
                                }
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        // Livello
                        if (currentCard.level != null) {
                            ClickableSearchText(
                                label = stringResource(R.string.card_label_level),
                                value = currentCard.level.toString(),
                                navController = navController,
                                searchCriteriaAction = { 
                                    Screen.DataBaseAdvancedSearch.createRouteForLevel(level = currentCard.level) 
                                }
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        // ATK/DEF
                        if (currentCard.atk != null || currentCard.def != null) {
                            val atkValue = currentCard.atk?.toString() ?: "N/A"
                            val defValue = currentCard.def?.toString() ?: "N/A"
                            Text(
                                text = "ATK: $atkValue / DEF: $defValue",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } // Fine Column attributi

                    // Barrier per posizionare la descrizione sotto l'immagine e gli attributi
                    val bottomBarrier = createBottomBarrier(cardImageRef, attributesColumnRef)

                    // Descrizione della carta (sotto immagine e attributi)
                    Text(
                        text = currentCard.desc,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.constrainAs(descriptionTextRef) {
                            top.linkTo(bottomBarrier, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            // L'altezza si adatterà al testo
                        }
                    )
                } // Fine ConstraintLayout Annidato
            } // Fine Card cornice
        } // Fine ConstraintLayout Principale
    }
}

@Composable
private fun ClickableSearchText(
    label: String,
    value: String?,
    navController: NavHostController?,
    searchCriteriaAction: () -> String,
    modifier: Modifier = Modifier // Aggiunto modifier per flessibilità
) {
    value?.let {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) { // Aggiunto alignment
            Text("$label: ", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.clickable {
                    navController?.navigate(searchCriteriaAction()) {
                        popUpTo(Screen.MenuScreen1.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "LargeCardUI Preview")
@Composable
fun LargeCardUIPreview() {
    val sampleCard = LargePlayingCard(
        id = 12345,
        name = "Drago Bianco Occhi Blu",
        typeline = listOf("Mostro Normale"),
        type = "Mostro Normale",
        humanReadableCardType = "Dragon/Normal",
        frameType = "normal",
        desc = "Questo drago leggendario è una potente macchina di distruzione. Praticamente invincibile, sono in pochi ad aver fronteggiato questa creatura ed essere sopravvissuti per raccontarlo.",
        race = "Drago",
        atk = 3000,
        def = 2500,
        level = 8,
        attribute = "LUCE",
        cardImages = listOf(
            CardImage(
                id = 1,
                imageUrl = "https://images.ygoprodeck.com/images/cards/89631139.jpg",
                imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/89631139.jpg",
                imageUrlCropped = "https://images.ygoprodeck.com/images/cards_cropped/89631139.jpg"
            )
        ),
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
