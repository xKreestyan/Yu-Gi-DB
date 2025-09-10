package com.example.yu_gi_db.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.fillMaxHeight 
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.heightIn 
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
import androidx.compose.ui.unit.Dp 
import androidx.compose.ui.unit.dp 
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.CardImage
import com.example.yu_gi_db.model.LargePlayingCard
import com.example.yu_gi_db.ui.theme.LightSilver
import com.example.yu_gi_db.ui.theme.RoyalBlueDark 
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.ui.theme.YugiohCardNameDisplay 
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


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
                modifier = Modifier.constrainAs(frameRef) {
                    top.linkTo(cardNameBoxRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent
                },
                shape = RectangleShape,
                border = BorderStroke(2.dp, LightSilver),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.sfondo_cornice),
                        contentDescription = stringResource(R.string.frame_background_image_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // MODIFICATO QUI
                    )

                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (
                            cardImageRef,
                            attributesColumnRef,
                            descriptionFrameRef
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
                                            URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                        navController?.navigate(Screen.ZoomCardScreen.createRoute(encodedUrl))
                                    }
                                }
                                .constrainAs(cardImageRef) {
                                    top.linkTo(parent.top, margin = 8.dp)
                                    start.linkTo(parent.start, margin = 8.dp)
                                }
                        )

                        Column(
                            modifier = Modifier.constrainAs(attributesColumnRef) {
                                top.linkTo(cardImageRef.top)
                                start.linkTo(cardImageRef.end, margin = 8.dp)
                                end.linkTo(parent.end, margin = 8.dp)
                                width = Dimension.fillToConstraints
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
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

                            if (currentCard.atk != null || currentCard.def != null) {
                                val atkValue = currentCard.atk?.toString() ?: "N/A"
                                val defValue = currentCard.def?.toString() ?: "N/A"
                                Text(
                                    text = "ATK: $atkValue / DEF: $defValue",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        val bottomBarrier = createBottomBarrier(cardImageRef, attributesColumnRef)

                        Card(
                            modifier = Modifier
                                .constrainAs(descriptionFrameRef) {
                                    top.linkTo(bottomBarrier, margin = 8.dp)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                    height = Dimension.preferredWrapContent
                                }
                                .heightIn(max = maxDescriptionHeight),
                            shape = RectangleShape,
                            border = BorderStroke(2.dp, LightSilver),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp) 
                                    .fillMaxWidth()
                                    .fillMaxHeight() 
                                    .verticalScroll(rememberScrollState()) 
                            ) {
                                Text(
                                    text = currentCard.desc,
                                    style = MaterialTheme.typography.bodyMedium,
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
private fun ClickableSearchText(
    label: String,
    value: String?,
    navController: NavHostController?,
    searchCriteriaAction: () -> String,
    modifier: Modifier = Modifier 
) {
    value?.let {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) { 
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
        type = "Mostro Normale",
        desc = "Questo drago leggendario è una potente macchina di distruzione. Praticamente invincibile, sono in pochi ad aver fronteggiato questa creatura ed essere sopravvissuti per raccontarlo.",
        race = "Drago",
        atk = 3000,
        def = 2500,
        level = 8,
        attribute = "LUCE",
        cardImages = listOf(CardImage(1, "", "https://images.ygoprodeck.com/images/cards_small/89631139.jpg", "")),
        typeline = emptyList(), 
        humanReadableCardType = "Dragon/Normal", 
        frameType = "normal", 
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
