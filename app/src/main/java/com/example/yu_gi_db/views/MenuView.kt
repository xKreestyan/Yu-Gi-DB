package com.example.yu_gi_db.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ChainStyle
import androidx.navigation.NavController
import com.example.yu_gi_db.R
import com.example.yu_gi_db.ui.theme.darken
import com.example.yu_gi_db.views.navigation.Screen
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo
import java.util.Locale

@Composable
fun MenuView(navController: NavController? = null) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val constraints = ConstraintSet {
        val titleRef = createRefFor("title")
        val button1Ref = createRefFor("button1")
        val button2Ref = createRefFor("button2")
        val button3Ref = createRefFor("button3")
        val button4Ref = createRefFor("button4")

        val titleTopMargin = if (isLandscape) 5.dp else 32.dp
        val interButtonMargin = if (isLandscape) 15.dp else 50.dp
        val topBottomChainMargin = if (isLandscape) 10.dp else 20.dp
        val buttonFixedWidth = if (isLandscape) 400.dp else 350.dp

        constrain(titleRef) {
            top.linkTo(parent.top, margin = titleTopMargin)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(button1Ref) {
            top.linkTo(titleRef.bottom, margin = topBottomChainMargin) // Margine superiore della catena
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button2Ref) {
            // Nessun vincolo verticale esplicito, gestito dalla catena
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button3Ref) {
            // Nessun vincolo verticale esplicito, gestito dalla catena
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button4Ref) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
            bottom.linkTo(parent.bottom) // << ANCORAGGIO INFERIORE AGGIUNTO (DI NUOVO)
        }

        createVerticalChain(
            button1Ref,
            button2Ref.withChainParams(topMargin = interButtonMargin),
            button3Ref.withChainParams(topMargin = interButtonMargin),
            button4Ref.withChainParams(topMargin = interButtonMargin, bottomMargin = topBottomChainMargin), // Il bottomMargin si applica all'ancoraggio parent.bottom
            chainStyle = ChainStyle.Packed
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.schermata_menu_yugioh),
            contentDescription = stringResource(R.string.Background_menu ),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.layoutId("title")) {
                Text(
                    text = "Yu-Gi-DB",
                    color = Color.Black,
                    style = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.headlineMedium.copy(
                        drawStyle = Stroke(width = 9f)
                    )
                )
                Text(
                    text = "Yu-Gi-DB",
                    color = _root_ide_package_.com.example.yu_gi_db.ui.theme.LightSilver,
                    style = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.headlineMedium
                )
            }

            val buttonFaceColor = _root_ide_package_.com.example.yu_gi_db.ui.theme.DarkSlateBlue
            val buttonFaceBrush = Brush.verticalGradient(
                colors = listOf(
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.RoyalBlueDark.darken(0.6f),
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.MidnightBlue,
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.SapphireBlue,
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.ElectricCyan,
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.SapphireBlue,
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.MidnightBlue,
                    _root_ide_package_.com.example.yu_gi_db.ui.theme.RoyalBlueDark.darken(0.6f)
                )
            )
            val buttonContentColor = _root_ide_package_.com.example.yu_gi_db.ui.theme.RoyalBlueDark.darken(factor = 0.6f)
            val buttonDepth = 8.dp
            val buttonShapeStyle =
                _root_ide_package_.com.example.yu_gi_db.ui.theme.ParallelogramShape(shearFactor = 0.15f)

            _root_ide_package_.com.example.yu_gi_db.ui.theme.YugiohParallelepipedButton(
                text = stringResource(R.string.database),
                onClick = { navController?.navigate(Screen.DataBaseScreen1.route) },
                modifier = Modifier.layoutId("button1"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.labelLarge
            )

            _root_ide_package_.com.example.yu_gi_db.ui.theme.YugiohParallelepipedButton(
                text = stringResource(R.string.favorites),
                onClick = { navController?.navigate(Screen.SavedCardsScreen.route) },
                modifier = Modifier.layoutId("button2"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.labelLarge
            )

            _root_ide_package_.com.example.yu_gi_db.ui.theme.YugiohParallelepipedButton(
                text = stringResource(R.string.rulebook),
                onClick = {
                    val currentLang = Locale.getDefault().language
                    val pdfFileNameInAssets = if (currentLang == "it") "pdf/rulebook_it.pdf" else "pdf/rulebook_en.pdf"
                    val pdfTitle = if (currentLang == "it") "Regolamento Yu-Gi-Oh!" else "Yu-Gi-Oh! Rulebook"

                    try {
                        // 1. Ottieni l'Intent dalla libreria
                        val pdfIntent: Intent = PdfViewerActivity.launchPdfFromPath(
                            context = context,
                            path = pdfFileNameInAssets,
                            pdfTitle = pdfTitle,
                            saveTo = saveTo.ASK_EVERYTIME,
                            fromAssets = true
                        )
                        // 2. Avvia l'Activity usando l'Intent ottenuto
                        context.startActivity(pdfIntent)

                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(context, "PDF viewer not found.", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "PDF opening error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.layoutId("button3"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.labelLarge
            )

            _root_ide_package_.com.example.yu_gi_db.ui.theme.YugiohParallelepipedButton(
                text = stringResource(R.string.options),
                onClick = { navController?.navigate(Screen.InfoScreen.route) },
                modifier = Modifier.layoutId("button4"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = _root_ide_package_.com.example.yu_gi_db.ui.theme.AppTypography.labelLarge
            )
        }
    }
}




@Preview(showBackground = true, name = "Menu Screen Portrait")
@Composable
fun MenuScreenPreviewPortrait() {
    _root_ide_package_.com.example.yu_gi_db.ui.theme.YuGiDBTheme {
        MenuView()
    }
}

@Preview(showBackground = true, name = "Menu Screen Landscape", widthDp = 640, heightDp = 360)
@Composable
fun MenuScreenPreviewLandscape() {
    _root_ide_package_.com.example.yu_gi_db.ui.theme.YuGiDBTheme {
        MenuView()
    }
}
