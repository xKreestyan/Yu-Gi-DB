package com.example.yu_gi_db.views

import android.content.Intent // Per Intent
import android.util.Log
import androidx.activity.ComponentActivity // Per ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row // Per Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth // Per fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable // Per selectable
import androidx.compose.foundation.selection.selectableGroup // Per selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton // Per RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration // Per LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role // Per Role.RadioButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel // Per hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Per collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.MainActivity // Per riavviare MainActivity
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.viewmodels.CardListViewModel

private const val TAG_INFO_SCREEN = "InformationScreen"

@Composable
fun InitMainScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    val viewModel = hiltViewModel<CardListViewModel>()
    val isLoadingInitialData by viewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    if (isLoadingInitialData) {
        SplashScreen(modifier = modifier,navController)
    }
    else {
        Navigation()
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier, navController: NavHostController? = null) {
    Box {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
        ) {
            ImageRotation(
                R.drawable.yu_gi_oh_schermata_principale_v,
                R.drawable.yu_gi_oh_schermata_principale_o,
                Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = this.maxHeight / 2 ),
                contentAlignment = Alignment.TopCenter
            ) {
                WaitIndicatorView(
                    Modifier.size(this@BoxWithConstraints.maxWidth / 3)
                )
            }
        }
    }
}
@Composable
fun MenuScreen1(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    MenuScreen(navController)
}

@Composable
fun DataBaseScreen1(modifier: Modifier = Modifier,initialSearchCriteria: AdvancedSearchCriteria? = null, navController: NavHostController? = null) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.app_name),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            initialSearchCriteria =initialSearchCriteria
        )
    }
}


@Composable
fun InitLargePlayingCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    navController: NavHostController?=null,
    viewModel: CardListViewModel = hiltViewModel()
) {
    val largeCard by viewModel.selectedLargeCard.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingLargeCard.collectAsStateWithLifecycle()
    val error by viewModel.largeCardError.collectAsStateWithLifecycle()

    LaunchedEffect(cardId) {
        viewModel.fetchLargeCardById(cardId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedLargeCard()
        }
    }

    AppScreen(
        modifier = modifier,
        appBarTitle = largeCard?.name ?: stringResource(id = R.string.card_detail_title_default),
        navController = navController
    ) { innerPadding ->
        if(optionErrorView(
                modifier = modifier.padding(innerPadding),
                isLoading = isLoading,
                errorMessage = error,
                isEmpty = (largeCard == null)
            ))
        {
            LargeCardItemView(modifier.padding(innerPadding),card = largeCard, navController = navController)
        }
    }
}




@Composable
fun CardZoomScreen(
    url: String,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    val ZOOM_STEP = 0.5f
    val MIN_ZOOM = 1.0f
    val MAX_ZOOM = 5.0f
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.zoomed_card_title),
        navController = navController
    ) { innerPadding ->
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        fun updateZoom(newScale: Float) {
            val coercedScale = newScale.coerceIn(MIN_ZOOM, MAX_ZOOM)
            if (scale != coercedScale) {
                if (coercedScale == MIN_ZOOM) {
                    offset = Offset.Zero
                }
                scale = coercedScale
            }
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            val newScaleTarget = if (scale > MIN_ZOOM) MIN_ZOOM else 2f
                            updateZoom(newScaleTarget)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        if (scale > MIN_ZOOM) {
                            val newPotentialOffset = offset.plus(pan)
                            val maxAllowedTranslateX = (this.size.width * (scale ) / 2f).coerceAtLeast(0f)
                            val maxAllowedTranslateY = (this.size.height * (scale - 1) / 2f).coerceAtLeast(0f)
                            offset = Offset(
                                x = newPotentialOffset.x.coerceIn(-maxAllowedTranslateX, maxAllowedTranslateX),
                                y = newPotentialOffset.y.coerceIn(-maxAllowedTranslateY, maxAllowedTranslateY)
                            )
                        }
                    }
                }
        ) {
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
                    onClick = { updateZoom(scale + ZOOM_STEP) },
                    modifier = Modifier.graphicsLayer(shadowElevation = 8f, shape = MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), MaterialTheme.shapes.small)

                ) {
                    Text("+", Modifier.scale(5f))
                }
                IconButton(
                    onClick = { updateZoom(scale - ZOOM_STEP) },
                    modifier = Modifier.graphicsLayer(shadowElevation = 8f, shape = MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), MaterialTheme.shapes.small)
                ) {
                    Text("-", Modifier.scale(5f))
                }
            }
        }
    }
}

@Composable
fun InformationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.info_screen_title),
        navController = navController,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoSectionView( // Assicurati che sia definita e importata
                title = stringResource(R.string.info_section_about_title)
            ) {
                Text(
                    text = stringResource(R.string.info_section_about_content),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionView(
                title = stringResource(R.string.info_section_version_title)
            ) {
                Text(
                    text = stringResource(R.string.version),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionView(
                title = stringResource(R.string.info_section_developer_title)
            ) {
                Text(
                    text = stringResource(R.string.name_and_company),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionView(
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

@Composable
fun SavedCardsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
) {
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.saved_cards_title),
        navController = navController
    ) { innerPadding ->
        InitCardsScreenView(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            initialSearchCriteria = AdvancedSearchCriteria(isFavorite = true)
        )
    }
}

/*----------------------------------------------------------------------------------------------------*/
// Preview functions

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    YuGiDBTheme {
        SplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DataBaseScreen1Preview() {
    YuGiDBTheme {
        DataBaseScreen1()
    }
}

@Preview(showBackground = true)
@Composable
fun CardZoomScreenPreview() {
    YuGiDBTheme {
        CardZoomScreen(url = "")
    }
}

@Preview(showBackground = true, name = "InfoScreenView Preview")
@Composable
fun InfoScreenViewPreview() {
    YuGiDBTheme {
        InformationScreen()
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - Empty")
@Composable
fun SavedCardsScreenEmptyPreview() {
    YuGiDBTheme {
        SavedCardsScreen(navController = null)
    }
}

@Preview(showBackground = true, name = "SavedCardsScreen - With Data")
@Composable
fun SavedCardsScreenWithDataPreview() {
    YuGiDBTheme {
        SavedCardsScreen(navController = null)
    }
}
