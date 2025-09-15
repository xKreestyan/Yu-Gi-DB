package com.example.yu_gi_db.views

import androidx.compose.foundation.layout.Arrangement // Per LazyVerticalGrid
import androidx.compose.foundation.layout.PaddingValues // Per LazyVerticalGrid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells // Per LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Per LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Per LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api // Aggiunto per Scaffold
import androidx.compose.material3.Scaffold // Aggiunto per Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable 
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
/*
@OptIn(ExperimentalMaterial3Api::class) // Aggiunto per Scaffold e TopAppBar
@Composable
fun DatabaseScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    var searchNameQuery by rememberSaveable { mutableStateOf("") }
    var advancedCriteria by remember { mutableStateOf(AdvancedSearchCriteria()) }
    val focusManager = LocalFocusManager.current

    val exampleCards by rememberSaveable {
        mutableStateOf(
            listOf(
                SmallPlayingCard(id = 1, name = "A Cell Breeding Device", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, name = "Dark Magician", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/46986414.jpg"),
                SmallPlayingCard(id = 3, name = "Exodia the Forbidden One", imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/33396948.jpg")
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(), // Applica il modifier passato allo Scaffold
        topBar = {
            StandardTopAppBar(
                title = stringResource(id = R.string.new_database_screen_title),
                navController = navController
            )
        }
    ) { innerPadding -> // Questo innerPadding è fornito dallo Scaffold di DatabaseScreen
        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding) // Applica il padding per evitare sovrapposizioni con la TopAppBar
                .fillMaxSize()
        ) {
            val (searchBar, resultsGrid) = createRefs()

            TextFieldView(
                value = searchNameQuery,
                onValueChange = { newNameQuery ->
                    searchNameQuery = newNameQuery
                    advancedCriteria = advancedCriteria.copy(name = newNameQuery.ifBlank { null })
                    // Triggerare la ricerca: cardViewModel.search(advancedCriteria)
                },
                modifier = Modifier.constrainAs(searchBar) {
                    top.linkTo(parent.top, margin = 8.dp) // La searchBar ora si ancora al top del ConstraintLayout
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
                label = { Text(stringResource(R.string.search_bar_label_name_hint)) },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    // Triggerare la ricerca: cardViewModel.search(advancedCriteria)
                }),
                searchCriteria = advancedCriteria,
                onSearchCriteriaChange = { newCriteria ->
                    advancedCriteria = newCriteria
                    searchNameQuery = newCriteria.name ?: ""
                    // Triggerare la ricerca: cardViewModel.search(advancedCriteria)
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(180.dp),
                modifier = Modifier.constrainAs(resultsGrid) {
                    top.linkTo(searchBar.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exampleCards, key = { card -> card.id }) { card ->
                    SmallCardItemView(
                        card = card,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "DatabaseScreen Preview")
@Composable
fun DatabaseScreenPreview() {
    YuGiDBTheme {
        AppScreen(
            appBarTitle = stringResource(id = R.string.app_name),
            navController = null
        ) { innerPadding ->
            DatabaseScreen(navController = null)
        }
    }
}
*/