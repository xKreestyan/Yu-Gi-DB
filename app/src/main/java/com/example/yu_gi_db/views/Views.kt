package com.example.yu_gi_db.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.yu_gi_db.R


@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding -> // Use Scaffold for basic screen structure
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // You can replace this with your actual splash screen content
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Example: Using app icon
                contentDescription = "test1", // Add a string resource for this
               // modifier = Modifier.fillMaxSize(),

            )
            // Or a Text:
             Text(
                text = stringResource(R.string.app_name),
                 style = MaterialTheme.typography.headlineLarge
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
        modifier = modifier,
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text ="test2") // Add a string resource for this
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // AppTheme {
    MainScreen()
    // }
}