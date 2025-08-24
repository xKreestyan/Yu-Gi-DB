package com.example.yu_gi_db.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.SmallPlayingCard
import com.example.yu_gi_db.viewmodels.CardListViewModel

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding -> // Use Scaffold for basic screen structure
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.screen_yu_gi_db),
                contentDescription = stringResource(id = R.string.splash_content_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize() // Example size
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        SavedCardsScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme { // Wrap with Theme for preview
        MainScreen()
    }
}

@Composable
fun SavedCardsScreen(modifier: Modifier = Modifier, cardListViewModel: CardListViewModel = hiltViewModel()) {
    val cards by cardListViewModel.smallCards.collectAsStateWithLifecycle()
    val isLoading by cardListViewModel.isLoadingInitialData.collectAsStateWithLifecycle()
    val error by cardListViewModel.initialDataError.collectAsStateWithLifecycle()

    SavedCardsContent(
        modifier = modifier,
        cards = cards,
        isLoading = isLoading,
        errorMessage = error
    )
}


@Composable
fun CardItemView(card: SmallPlayingCard, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp),
        ) {
        Column(
           horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.imageUrlSmall)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground), // Replace with a generic placeholder
                error = painterResource(R.drawable.ic_launcher_background), // Replace with a generic error image
                contentDescription = "ciao",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(260.dp, 350.dp)// Adjust size as needed
            )
            Text(
                text = "ID: ${card.id}", // You might want to display name or other info if available
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    MaterialTheme {
        CardItemView(
            card = SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg") // Example image
        )
    }
}


@Composable
private fun SavedCardsContent(
    modifier: Modifier = Modifier,
    cards: List<SmallPlayingCard>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.fillMaxSize(0.5f) )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading")

            }
        } else if (errorMessage != null) {
            Text(
                text = "errorMessage{$errorMessage}",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else if (cards.isEmpty()) {
            Text(
                text = "No cards saved",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards, key = { card -> card.id }) { card ->
                    CardItemView(card = card)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Saved Cards Content - Populated")
@Composable
fun SavedCardsContentPopulatedPreview() {
    MaterialTheme {
        SavedCardsContent(
            cards = listOf(
                SmallPlayingCard(id = 1, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/34541863.jpg"),
                SmallPlayingCard(id = 2, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6983839.jpg"),
                SmallPlayingCard(id = 3, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/6973839.jpg"),
                SmallPlayingCard(id = 4, imageUrlSmall = "https://images.ygoprodeck.com/images/cards_small/69838399.jpg")

            ),
            isLoading = false,
            errorMessage = null
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Empty")
@Composable
fun SavedCardsContentEmptyPreview() {
    MaterialTheme {
        SavedCardsContent(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Loading")
@Composable
fun SavedCardsContentLoadingPreview() {
    MaterialTheme {
        SavedCardsContent(
            cards = emptyList(),
            isLoading = true,
            errorMessage = null
        )
    }
}
@Preview(showBackground = true, name = "Saved Cards Content - Error")
@Composable
fun SavedCardsContentErrorPreview() {
    MaterialTheme {
        SavedCardsContent(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Network request failed"
        )
    }
}
