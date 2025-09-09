package com.example.yu_gi_db.views

import android.content.Intent // Per Intent
import android.util.Log
import androidx.activity.ComponentActivity // Per ComponentActivity
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton // Per RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.yu_gi_db.data.preferences.UserPreferencesRepository // Per le costanti LANGUAGE_*
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.MenuScreen
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.utils.LanguageHelper // Per LanguageHelper
import com.example.yu_gi_db.viewmodels.CardListViewModel
import com.example.yu_gi_db.viewmodels.LanguageSettingsViewModel // Per LanguageSettingsViewModel

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
fun SplashScreen(modifier: Modifier = Modifier,navController: NavHostController? = null) {
    Box{
        BoxWithConstraints(
            modifier = modifier
                .fillMaxHeight()
        ) {
            ImageRotation(R.drawable.yu_gi_oh_schermata_principale_v ,R.drawable.yu_gi_oh_schermata_principale_o,modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = this.maxHeight / 8),
                contentAlignment = Alignment.BottomCenter
            ) {
                WaitIndicatorView(
                    Modifier.size(this@BoxWithConstraints.maxWidth / 5)
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
    AppScreen(
        modifier = modifier,
        appBarTitle = stringResource(id = R.string.zoomed_card_title),
        navController = navController
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.card_image_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun InformationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    languageSettingsViewModel: LanguageSettingsViewModel = hiltViewModel()
) {
    val currentLanguage by languageSettingsViewModel.currentLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val testTitleFromStringResource = stringResource(id = R.string.info_screen_title)

    Log.d(TAG_INFO_SCREEN, "InformationScreen recomposed. ViewModelLang: $currentLanguage. ConfigLocale: ${configuration.locales[0]}. TitleFromStringRes: '$testTitleFromStringResource'")

    AppScreen(
        modifier = modifier,
        appBarTitle = testTitleFromStringResource,
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
            InfoSectionView(
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
                title = stringResource(R.string.language_setting_title)
            ) {
                val languageOptions = listOf(
                    stringResource(R.string.language_italian) to UserPreferencesRepository.LANGUAGE_ITALIAN,
                    stringResource(R.string.language_english) to UserPreferencesRepository.LANGUAGE_ENGLISH,
                    stringResource(R.string.language_system_default) to UserPreferencesRepository.LANGUAGE_SYSTEM
                )

                Column(Modifier.selectableGroup()) {
                    languageOptions.forEach { (displayText, languageCode) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (languageCode == currentLanguage),
                                    onClick = {
                                        if (languageCode != currentLanguage) {
                                            Log.d(TAG_INFO_SCREEN, "Updating to $languageCode. Current: $currentLanguage")
                                            languageSettingsViewModel.updateLanguage(languageCode)
                                            Log.d(TAG_INFO_SCREEN, "Applying $languageCode with Helper")
                                            LanguageHelper.applyAppLanguage(languageCode)

                                            val activity = context as? ComponentActivity
                                            if (activity != null) {
                                                Log.d(TAG_INFO_SCREEN, "Restarting activity for language change...")
                                                val intent = Intent(activity, MainActivity::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                activity.startActivity(intent)
                                                activity.finishAffinity()
                                            } else {
                                                Log.e(TAG_INFO_SCREEN, "Context is not a ComponentActivity, cannot restart.")
                                            }
                                        }
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (languageCode == currentLanguage),
                                onClick = null
                            )
                            Text(
                                text = displayText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            InfoSectionView(
                title = stringResource(R.string.info_section_version_title)
            ) {
                Text(text = stringResource(R.string.version), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionView(
                title = stringResource(R.string.info_section_developer_title)
            ) {
                Text(text = stringResource(R.string.name_and_company), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionView(
                title = stringResource(R.string.info_section_credits_title)
            ) {
                Text(text = stringResource(R.string.info_section_credits_content), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
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
