package com.example.yu_gi_db.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box // Import per Box
import androidx.compose.foundation.layout.Column // Import per Column (per impilare sfondi)
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth // Import per fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale // Import per ContentScale
import androidx.compose.ui.res.painterResource // Import per painterResource
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
import com.example.yu_gi_db.model.CardSet
import com.example.yu_gi_db.model.CardPrice
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun LargeCradUI(
    modifier: Modifier = Modifier,
    card: LargePlayingCard? = null,
    navController: NavHostController? = null
) {
    val currentCard = card ?: return // Esce se la carta è nulla

    Box(modifier = modifier.fillMaxSize()) { // Box come contenitore radice
        // Colonna per contenere le immagini di sfondo impilate, allineata in basso
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Allinea l'intera colonna di sfondi in basso
        ) {
            // Immagine di sfondo SUPERIORE (nuova)
            Image(
                painter = painterResource(id = R.drawable.sfondo_large), // Nuova immagine di sfondo
                contentDescription = stringResource(R.string.top_background_image_description), // TODO: Aggiungere stringa di risorsa
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            // Immagine di sfondo INFERIORE (esistente)
            Image(
                painter = painterResource(id = R.drawable.sfondo_large_card_v),
                contentDescription = stringResource(R.string.background_image_description),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }

        // ConstraintLayout per i contenuti, sopra lo sfondo
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val (
                cardImageRef,
                typeRaceRowRef,
                attributeTextRef,
                levelTextRef,
                atkDefTextRef,
                descriptionTextRef
            ) = createRefs()

            val firstCardImage: CardImage? = currentCard.cardImages.firstOrNull()
            val imageUrl: String = firstCardImage?.imageUrlSmall ?: ""

            CardUrltoView(
                url = imageUrl,
                modifier = Modifier
                    .size(220.dp, 296.dp)
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.constrainAs(typeRaceRowRef) {
                    top.linkTo(cardImageRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.wrapContent
                }
            ) {
                ClickableSearchText(
                    label = stringResource(R.string.card_label_type),
                    value = currentCard.type,
                    navController = navController,
                    searchCriteriaAction = {
                        Screen.DataBaseAdvancedSearchType.createRoute(type = currentCard.type)
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

            if (currentCard.attribute != null) {
                ClickableSearchText(
                    label = stringResource(R.string.card_label_attribute),
                    value = currentCard.attribute,
                    navController = navController,
                    searchCriteriaAction = { Screen.DataBaseAdvancedSearchAttribute.createRoute(attribute = currentCard.attribute!!) },
                    modifier = Modifier.constrainAs(attributeTextRef) {
                        top.linkTo(typeRaceRowRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.wrapContent
                    }
                )
            }

            if (currentCard.level != null) {
                ClickableSearchText(
                    label = stringResource(R.string.card_label_level),
                    value = currentCard.level.toString(),
                    navController = navController,
                    searchCriteriaAction = { Screen.DataBaseAdvancedSearchLivello.createRoute(Livello = currentCard.level!!) },
                    modifier = Modifier.constrainAs(levelTextRef) {
                        val topAnchor = if (currentCard.attribute != null) attributeTextRef.bottom else typeRaceRowRef.bottom
                        top.linkTo(topAnchor, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.wrapContent
                    }
                )
            }

            if (currentCard.atk != null || currentCard.def != null) {
                val atkValue = currentCard.atk?.toString() ?: "N/A"
                val defValue = currentCard.def?.toString() ?: "N/A"
                Text(
                    text = "ATK: $atkValue / DEF: $defValue",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.constrainAs(atkDefTextRef) {
                        val topAnchor = when {
                            currentCard.level != null -> levelTextRef.bottom
                            currentCard.attribute != null -> attributeTextRef.bottom
                            else -> typeRaceRowRef.bottom
                        }
                        val topMargin = if (currentCard.level == null && currentCard.attribute == null) 16.dp else 8.dp
                        top.linkTo(topAnchor, margin = topMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.wrapContent
                    }
                )
            }

            Text(
                text = currentCard.desc,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.constrainAs(descriptionTextRef) {
                    val topAnchor = when {
                        (currentCard.atk != null || currentCard.def != null) -> atkDefTextRef.bottom
                        currentCard.level != null -> levelTextRef.bottom
                        currentCard.attribute != null -> attributeTextRef.bottom
                        else -> typeRaceRowRef.bottom
                    }
                    val topMargin = when {
                        (currentCard.atk != null || currentCard.def != null) -> 16.dp
                        currentCard.level != null -> 8.dp
                        currentCard.attribute != null -> 8.dp
                        else -> 16.dp
                    }
                    top.linkTo(topAnchor, margin = topMargin)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}

@Composable
private fun ClickableSearchText(
    label: String,
    value: String?,
    navController: NavHostController?,
    searchCriteriaAction: () -> String,
    modifier: Modifier = Modifier
) {
    value?.let {
        Row(modifier = modifier) {
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

