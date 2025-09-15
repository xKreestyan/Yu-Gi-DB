package com.example.yu_gi_db.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yu_gi_db.R
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.views.navigation.Screen


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    appBarTitle: String,
    navController: NavHostController?,
    content: @Composable (innerPadding: PaddingValues) -> Unit={}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InitStandardTopAppBar(
                title = appBarTitle,
                navController = navController,
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}*/
@Composable
fun InitScaffoldView(
    modifier: Modifier = Modifier,
    appBarTitle: String, // This is the title AppScreen wants for its specific instance
    navController: NavHostController?, // This is the NavController for this specific instance
    content: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    ScaffoldView(
        modifier = modifier,
        content = content,
        topBar = {
            InitStandardTopAppBar(
                title = appBarTitle,
                navController = navController,
            )
        }
    )
}


@Composable
fun ScaffoldView(
    modifier: Modifier = Modifier,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
    topBar: @Composable () -> Unit = { }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            topBar()
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun InitStandardTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController?, // Nullable if used in previews without nav
    title: String // The dynamic title of the current screen
) {
    val appName = stringResource(R.string.app_name)
    // Gracefully handle null NavController for previews or specific scenarios
    val navBackStackEntry by navController?.currentBackStackEntryAsState() ?: return
    val currentRoute = navBackStackEntry?.destination?.route

    val showBackButton = navController.previousBackStackEntry != null

    // Determine if action icons should be shown based on the current route
    val showActions = currentRoute != Screen.SavedCardsScreen.route &&
            currentRoute != Screen.InfoScreen.route

    TopBarView(
        modifier = modifier,
        appName = appName,
        currentScreenTitle = if (title == appName) "" else title, // Don't repeat app name
        showBackButton = showBackButton,
        showFavoriteIcon = showActions,
        showInfoIcon = showActions,
        onBackClick = { navController.navigateUp() },
        onAppNameClick = {
            navController.navigate(Screen.MenuScreen1.route) {
                popUpTo(Screen.MenuScreen1.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }, onFavoriteClick = { navController.navigate(Screen.SavedCardsScreen.route) },
        onInfoClick = { navController.navigate(Screen.InfoScreen.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarView(
    modifier: Modifier = Modifier,
    appName: String = stringResource(R.string.app_name),
    currentScreenTitle: String = "",
    showBackButton: Boolean = true,
    showFavoriteIcon: Boolean = true,
    showInfoIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    onAppNameClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onInfoClick: () -> Unit = {}
)
{
    TopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = appName,
                    modifier = Modifier.clickable(onClick = onAppNameClick)
                )
                if (currentScreenTitle.isNotEmpty() && currentScreenTitle != appName) {
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        Text(currentScreenTitle)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.icon_arrowback) // More specific description
                    )
                }
            }
        },
        actions = {
            Row {
                if (showFavoriteIcon) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = stringResource(R.string.icon_favorite) // Descriptive
                        )
                    }
                }
                if (showInfoIcon) {
                    IconButton(onClick = onInfoClick) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.icon_info)// Descriptive
                        )
                    }
                }
            }
        }
    )
}


// --- Previews ---

@Preview(showBackground = true, name = "TopBarView - Full")
@Composable
fun TopBarViewPreview_Full() {
    YuGiDBTheme {
        TopBarView(
            appName = "Yu-Gi-DB Preview",
            currentScreenTitle = "Awesome Card List",
            showBackButton = true,
            showFavoriteIcon = true,
            showInfoIcon = true
        )
    }
}

@Preview(showBackground = true, name = "ScaffoldView - Basic")
@Composable
fun ScaffoldViewPreview_Basic() {
    YuGiDBTheme {
        ScaffoldView(
            topBar = { TopBarViewPreview_Full() }, // Reuse an existing TopBar preview
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Content Area for ScaffoldView")
                }
            }
        )
    }
}


