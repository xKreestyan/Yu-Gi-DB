package com.example.yu_gi_db.views

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.yu_gi_db.R
import com.example.yu_gi_db.model.AdvancedSearchCriteria
import com.example.yu_gi_db.ui.theme.YuGiDBTheme


/*
@Composable
fun MyScreenWithAToastButton() {
    val context = LocalContext.current
    Button(onClick = {
        val message = "Questo è un Toast da Compose!"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }) {
        Text("Mostra Toast")
    }
}
*/
@Composable
fun WaitIndicatorView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // Usa Box per centrare se necessario
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.optimized_refined)
                .decoderFactory(ImageDecoderDecoder.Factory())
                .build(),
            contentDescription = stringResource(R.string.loading_indicator_description),
            modifier = modifier.align(Alignment.Center) // Centra l'immagine nel Box
        )
    }
}

@Composable
fun ErrorMessageView(text: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()

    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify,

            modifier = Modifier
                .padding(30.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                )
        )
    }
}

@Composable
fun optionErrorView(modifier: Modifier = Modifier,
                    isLoading: Boolean=false,
                    isEmpty: Boolean=false,
                    errorMessage: String?=null,
                    isSearchActive: Boolean=true // True if an active search query is not blank
): Boolean
{
    var ret=false
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            WaitIndicatorView(
                modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.7f)
            )}
    }
    else if (errorMessage != null) {
        ErrorMessageView(stringResource(R.string.error_message_generic) + ": $errorMessage")
    }
    else if (isEmpty) {
        Log.d("optionErrorView", "IsEmpty: true. isSearchActive: $isSearchActive")
        ErrorMessageView(
            if (isSearchActive) stringResource(R.string.no_cards_found_search)
            else stringResource(R.string.no_cards_in_default_list) // Specific message for empty default list
        )
    }
    else{
        ret=true
    }

    return ret
}



@Composable
fun CardUrltoView(url: String,modifier: Modifier = Modifier ){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.cardback),
        error = painterResource(R.drawable.cardback),
        contentDescription = stringResource(R.string.card_image_description),
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}
@Composable
fun ImageRotation(imageV: Int, imageO: Int, modifier: Modifier = Modifier ){
    val configuration = LocalConfiguration.current
    val imageResource = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        imageO
    } else {
        imageV
    }
    Image(
        painter =painterResource(id = imageResource),
        contentDescription = stringResource(id = R.string.error_message_generic),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

/*------------------------------------------------------------*/
// Preview functions



@Preview(showBackground = true, name = "WaitIndicatorView")
@Composable
fun WaitIndicatorViewPreview() {
    YuGiDBTheme {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center){
            WaitIndicatorView()
        }
    }
}



@Preview(showBackground = true, name = "CardsScreen - Loading Initial")
@Composable
fun CardsScreenLoadingInitialPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true,
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(),
            onSearchCriteriaChange = {},
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Error Initial")
@Composable
fun CardsScreenErrorInitialPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = "Failed to load default cards.",
            searchCriteria = AdvancedSearchCriteria(),
            onSearchCriteriaChange = {},
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - Searching by Name")
@Composable
fun CardsScreenSearchingByNamePreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = true,
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(name = "Blue-Eyes"),
            onSearchCriteriaChange = {},
        )
    }
}

@Preview(showBackground = true, name = "CardsScreen - No Results for Name Search")
@Composable
fun CardsScreenNoResultsNameSearchPreview() {
    YuGiDBTheme {
        CardsScreenView(
            cards = emptyList(),
            isLoading = false,
            errorMessage = null,
            searchCriteria = AdvancedSearchCriteria(name = "NonExistentCardNameXYZ"),
            onSearchCriteriaChange = {},
        )
    }
}