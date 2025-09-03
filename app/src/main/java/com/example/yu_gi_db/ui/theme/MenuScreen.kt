package com.example.yu_gi_db.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke // << IMPORT AGGIUNTO PER IL CONTORNO
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ChainStyle
import com.example.yu_gi_db.R
// AppTypography e LightSilver dovrebbero essere disponibili dal package com.example.yu_gi_db.ui.theme
// import com.example.yu_gi_db.ui.theme.AppTypography; // Se non lo sono già
// import com.example.yu_gi_db.ui.theme.LightSilver; // Se non lo sono già

@Composable
fun MenuScreen() {
    val constraints = ConstraintSet {
        val titleRef = createRefFor("title")
        val button1Ref = createRefFor("button1") // DATABASE
        val button2Ref = createRefFor("button2") // CARTE
        val button3Ref = createRefFor("button3") // OPZIONI
        val button4Ref = createRefFor("button4") // ESCI

        constrain(titleRef) {
            top.linkTo(parent.top, margin = 32.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        val interButtonMargin = 50.dp
        val topBottomChainMargin = 20.dp
        val buttonFixedWidth = 350.dp

        constrain(button1Ref) {
            top.linkTo(titleRef.bottom, margin = topBottomChainMargin)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button2Ref) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button3Ref) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        constrain(button4Ref) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.value(buttonFixedWidth)
        }

        createVerticalChain(button1Ref, button2Ref.withChainParams(topMargin = interButtonMargin), button3Ref.withChainParams(topMargin = interButtonMargin), button4Ref.withChainParams(topMargin = interButtonMargin, bottomMargin = topBottomChainMargin), chainStyle = ChainStyle.Packed)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.schermata_menu_yugioh),
            contentDescription = "Sfondo del menu",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.layoutId("title")) { // layoutId applicato al Box contenitore
                // Testo per il contorno (dietro)
                Text(
                    text = "Yu-Gi-DB",
                    color = Color.Black, // Colore del contorno
                    style = AppTypography.headlineMedium.copy(
                        drawStyle = Stroke(width = 9f) // Applica lo stroke
                    )
                )
                // Testo principale (davanti)
                Text(
                    text = "Yu-Gi-DB",
                    color = LightSilver, // Colore originale del testo
                    style = AppTypography.headlineMedium
                )
            }

            val buttonFaceColor = DarkSlateBlue
            val buttonFaceBrush = Brush.verticalGradient(
                colors = listOf(
                    RoyalBlueDark.darken(0.6f),
                    MidnightBlue,
                    SapphireBlue,
                    ElectricCyan,
                    SapphireBlue,
                    MidnightBlue,
                    RoyalBlueDark.darken(0.6f)
                )
            )
            val buttonContentColor = RoyalBlueDark.darken(factor = 0.6f)
            val buttonDepth = 8.dp
            val buttonShapeStyle = ParallelogramShape(shearFactor = 0.15f)

            YugiohParallelepipedButton(
                text = "DATABASE",
                onClick = { /* Azione per DATABASE */ },
                modifier = Modifier.layoutId("button1"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = AppTypography.labelLarge
            )

            YugiohParallelepipedButton(
                text = "PREFERITI",
                onClick = { /* Azione per CARTE */ },
                modifier = Modifier.layoutId("button2"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = AppTypography.labelLarge
            )

            YugiohParallelepipedButton(
                text = "REGOLAMENTO",
                onClick = { /* Azione per OPZIONI */ },
                modifier = Modifier.layoutId("button3"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = AppTypography.labelLarge
            )

            YugiohParallelepipedButton(
                text = "OPZIONI",
                onClick = { /* Azione per ESCI */ },
                modifier = Modifier.layoutId("button4"),
                faceColor = buttonFaceColor,
                faceBrush = buttonFaceBrush,
                contentColor = buttonContentColor,
                depth = buttonDepth,
                buttonShape = buttonShapeStyle,
                textStyle = AppTypography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    YuGiDBTheme {
        MenuScreen()
    }
}
