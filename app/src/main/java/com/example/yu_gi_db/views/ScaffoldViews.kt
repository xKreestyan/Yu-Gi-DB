package com.example.yu_gi_db.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background // For preview background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme // To adjust preview text color if needed
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yu_gi_db.R
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.views.navigation.Screen

@Composable
fun InitScaffoldView(
    modifier: Modifier = Modifier,
    appBarTitle: String,
    navController: NavHostController?,
    content: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    ScaffoldView(
        modifier = modifier,
        content = content,
        topBar = {
            InitStandardTopAppBar(
                title = appBarTitle,
                navController = navController,
                // You could expose backgroundImageAlpha here if needed
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
    navController: NavHostController?,
    title: String,
    backgroundImageAlpha: Float = 0.5f // Pass alpha from here if desired
) {
    val appName = stringResource(R.string.app_name)
    val navBackStackEntry by navController?.currentBackStackEntryAsState() ?: return
    val currentRoute = navBackStackEntry?.destination?.route

    val showBackButton = navController.previousBackStackEntry != null
    val showActions = currentRoute != Screen.SavedCardsScreen.route &&
            currentRoute != Screen.InfoScreen.route

    TopBarView(
        modifier = modifier,
        appName = appName,
        currentScreenTitle = if (title == appName) "" else title,
        showBackButton = showBackButton,
        showFavoriteIcon = showActions,
        showInfoIcon = showActions,
        onBackClick = { navController.navigateUp() },
        onAppNameClick = {
            navController.navigate(Screen.MenuScreen1.route) {
                popUpTo(Screen.MenuScreen1.route) { inclusive = true }
                launchSingleTop = true
            }
        },
        onFavoriteClick = { navController.navigate(Screen.SavedCardsScreen.route) },
        onInfoClick = { navController.navigate(Screen.InfoScreen.route) },
        backgroundImageAlpha = backgroundImageAlpha // Pass alpha to TopBarView
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
    onInfoClick: () -> Unit = {},
    backgroundImageAlpha: Float = 0.5f
) {
    // Determine the appropriate content color based on the theme and image
    // For a background image, onSurface is often a good choice,
    // or a hardcoded light color if the image + alpha tends to be dark.
    val contentColor = if (isSystemInDarkTheme()) {
        // If the image is generally dark or made dark by alpha,
        // a light color like onSurface (typically white/light gray in dark theme) or Color.White is good.
        MaterialTheme.colorScheme.onSurface // Or Color.White
    } else {
        // In light theme, if the image is light, onSurface (typically black/dark gray) is good.
        // If the image is dark, Color.White might still be better. Adjust as needed.
        MaterialTheme.colorScheme.onSurface // Or potentially Color.Black or MaterialTheme.colorScheme.primary
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.sfondo_descrizioni),
            contentDescription = stringResource(R.string.background_image_for_topbar),
            modifier = Modifier
                .matchParentSize()
                .alpha(backgroundImageAlpha),
            contentScale = ContentScale.Crop
        )

        TopAppBar(
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
                containerColor = Color.Transparent,
                titleContentColor = contentColor, // Use the determined contentColor
                navigationIconContentColor = contentColor, // Use the determined contentColor
                actionIconContentColor = contentColor // Use the determined contentColor
            ),
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_arrowback)
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
                                contentDescription = stringResource(R.string.icon_favorite)
                            )
                        }
                    }
                    if (showInfoIcon) {
                        IconButton(onClick = onInfoClick) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(R.string.icon_info)
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "TopBarView - Full")
@Composable
fun TopBarViewPreview_Full() {
    YuGiDBTheme {
        TopBarView(
            // To see the effect in preview, give the TopBarView's Box a defined height
            // because it's not in a Scaffold here.
            modifier = Modifier.height(64.dp), // Default TopAppBar height
            appName = "Yu-Gi-DB Preview",
            currentScreenTitle = "Awesome Card List",
            showBackButton = true,
            showFavoriteIcon = true,
            showInfoIcon = true
        )
    }
}

@Preview(showBackground = true, name = "TopBarView - Light Theme")
@Composable
fun TopBarViewPreview_Light() {
    YuGiDBTheme(darkTheme = false) { // Force light theme for this preview
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
            TopBarView(
                modifier = Modifier.height(64.dp),
                appName = "Yu-Gi-DB Preview",
                currentScreenTitle = "Light Theme Text",
                backgroundImageAlpha = 0.5f
            )
        }
    }
}

@Preview(showBackground = true, name = "TopBarView - Dark Theme")
@Composable
fun TopBarViewPreview_Dark() {
    YuGiDBTheme(darkTheme = true) { // Force dark theme for this preview
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
            TopBarView(
                modifier = Modifier.height(64.dp),
                appName = "Yu-Gi-DB Preview",
                currentScreenTitle = "Dark Theme Text",
                backgroundImageAlpha = 0.5f
            )
        }
    }
}

@Preview(showBackground = true, name = "Scaffold - Light Theme")
@Composable
fun ScaffoldViewPreview_Light() {
    YuGiDBTheme(darkTheme = false) {
        ScaffoldView(
            topBar = {
                InitStandardTopAppBar(
                    navController = null,
                    title = "Content (Light)",
                    backgroundImageAlpha = 0.7f
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Content Area")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Scaffold - Dark Theme")
@Composable
fun ScaffoldViewPreview_Dark() {
    YuGiDBTheme(darkTheme = true) {
        ScaffoldView(
            topBar = {
                InitStandardTopAppBar(
                    navController = null,
                    title = "Content (Dark)",
                    backgroundImageAlpha = 0.4f // Different alpha for testing
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Content Area")
                }
            }
        )
    }
}
