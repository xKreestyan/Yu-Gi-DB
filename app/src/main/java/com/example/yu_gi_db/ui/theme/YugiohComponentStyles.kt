package com.example.yu_gi_db.ui.theme // O com.example.yu_gi_db.ui.common se preferisci

// Importa i tuoi colori personalizzati da Color.kt
import com.example.yu_gi_db.ui.theme.SapphireBlue
import com.example.yu_gi_db.ui.theme.ElectricCyan
import com.example.yu_gi_db.ui.theme.MidnightBlue
import com.example.yu_gi_db.ui.theme.DarkSlateBlue // Aggiunto per la preview, se necessario
import com.example.yu_gi_db.ui.theme.YuGiDBTheme // Per le Preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme // Per textStyle
import androidx.compose.material3.Surface // Per la Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush // <-- IMPORTANTE: Aggiunto/Verificato
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
// import androidx.compose.ui.graphics.RectangleShape // Non più di default
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
// androidx.compose.ui.unit.sp // Non usato direttamente se textStyle lo gestisce

// --- SHAPE PERSONALIZZATA PER PARALLELOGRAMMA ---
/**
 * Una Shape che disegna un parallelogramma.
 * I lati superiore e inferiore sono orizzontali.
 * I lati sinistro e destro sono inclinati.
 *
 * @param shearFactor Un valore che determina l'inclinazione.
 *                    0f -> rettangolo.
 *                    Valori positivi -> inclinato verso destra in alto / sinistra in basso.
 *                    Valori negativi -> inclinato verso sinistra in alto / destra in basso.
 */
class ParallelogramShape(private val shearFactor: Float = 0.2f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * shearFactor.coerceAtLeast(0f), 0f)
            lineTo(size.width * (1f + shearFactor.coerceAtMost(0f)), 0f)
            lineTo(size.width * (1f - shearFactor.coerceAtLeast(0f)), size.height)
            lineTo(size.width * shearFactor.coerceAtMost(0f), size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Alternativa per un parallelogramma con shear applicato in modo più classico
 * dove l'offset orizzontale dipende dall'altezza.
 */
class SkewedParallelogramShape(private val horizontalShearPx: Float = 20f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(horizontalShearPx.coerceAtLeast(0f), 0f) // Top-left
            lineTo(size.width + horizontalShearPx.coerceAtMost(0f), 0f) // Top-right
            lineTo(size.width - horizontalShearPx.coerceAtLeast(0f), size.height) // Bottom-right
            lineTo(-horizontalShearPx.coerceAtMost(0f), size.height) // Bottom-left
            close()
        }
        return Outline.Generic(path)
    }
}


// Funzione helper per scurire un colore
fun Color.darken(factor: Float = 0.3f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[2] *= (1f - factor)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

@Composable
fun YugiohParallelepipedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    faceColor: Color = SapphireBlue,     // Mantenuto per fallback e bordo
    faceBrush: Brush? = null,            // << PARAMETRO AGGIUNTO
    sideColor: Color = faceColor.darken(),
    contentColor: Color = Color.White,
    depth: Dp = 5.dp,
    buttonShape: Shape = ParallelogramShape(shearFactor = 0.25f),
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelLarge
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentDepth = if (isPressed) depth / 2 else depth
    val currentFaceOffset = if (isPressed) depth - currentDepth else 0.dp

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = if (isPressed) 0.98f else 1f
                scaleY = if (isPressed) 0.98f else 1f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // --- LATO DEL PARALLELEPIPEDO ---
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = currentDepth, y = currentDepth)
                    .clip(buttonShape)
                    .background(sideColor)
            )
        }

        // --- FACCIA DEL PULSANTE ---
        // Nuova logica per lo sfondo della faccia che usa faceBrush se disponibile
        val faceBackgroundModifier = if (faceBrush != null && enabled) { // Applicare brush solo se abilitato
            Modifier.background(brush = faceBrush, shape = buttonShape)
        } else {
            Modifier.background(color = if (enabled) faceColor else faceColor.copy(alpha = 0.5f), shape = buttonShape)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .offset(x = currentFaceOffset, y = currentFaceOffset)
                .clip(buttonShape)             // 1. Clip prima
                .then(faceBackgroundModifier) // 2. Applica sfondo (colore o brush)
                .border(                        // 3. Poi il bordo
                    BorderStroke(
                        1.dp,
                        if (enabled) faceColor.darken(0.1f) else Color.Transparent
                    ),
                    shape = buttonShape
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        )  {
            Text(
                text = text.uppercase(),
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.7f),
                style = textStyle,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Preview per testare il tuo pulsante parallelepipedo con bordi diagonali e gradienti ---
@Preview(showBackground = true, name = "Yugioh Parallelepiped Diagonal Buttons")
@Composable
fun YugiohParallelepipedDiagonalButtonPreview() {
    YuGiDBTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp) // Aumentato lo spazio per visibilità
            ) {
                // Esempio 1: Gradiente Verticale Semplice
                YugiohParallelepipedButton(
                    text = "GRADIENTE VERTICALE",
                    onClick = { },
                    modifier = Modifier.width(280.dp),
                    faceColor = DarkSlateBlue, // Colore base per bordo/fallback
                    faceBrush = Brush.verticalGradient(
                        colors = listOf(
                            DarkSlateBlue.copy(alpha = 0.7f),
                            DarkSlateBlue,
                            DarkSlateBlue.darken(0.15f)
                        )
                    ),
                    contentColor = Color.LightGray, // Modificato per leggibilità su DarkSlateBlue
                    depth = 8.dp, // Aumentata profondità per questo esempio
                    buttonShape = ParallelogramShape(shearFactor = 0.15f)
                )

                // Esempio 2: Gradiente Orizzontale
                YugiohParallelepipedButton(
                    text = "GRADIENTE ORIZZONTALE",
                    onClick = { },
                    modifier = Modifier.width(280.dp),
                    faceColor = ElectricCyan,
                    faceBrush = Brush.horizontalGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.6f),
                            ElectricCyan,
                            ElectricCyan.darken(0.2f)
                        )
                    ),
                    contentColor = Color.Black,
                    depth = 4.dp,
                    buttonShape = ParallelogramShape(shearFactor = -0.20f)
                )

                // Esempio 3: SKEWED PIXELS con colore solido (faceBrush = null)
                YugiohParallelepipedButton(
                    text = "SKEWED PIXELS (SOLIDO)",
                    onClick = { },
                    modifier = Modifier.width(280.dp),
                    faceColor = MidnightBlue, // faceBrush è null di default
                    contentColor = Color.White,
                    depth = 5.dp,
                    buttonShape = SkewedParallelogramShape(horizontalShearPx = 35f)
                )

                // Esempio 4: RETTANGOLO con Gradiente Radiale
                YugiohParallelepipedButton(
                    text = "RETTANGOLO RADIALE",
                    onClick = { },
                    modifier = Modifier.width(280.dp),
                    faceColor = Color.DarkGray,
                    faceBrush = Brush.radialGradient(
                        colors = listOf(
                            Color.DarkGray.copy(alpha = 0.5f),
                            Color.DarkGray,
                            Color.DarkGray.darken(0.3f)
                        )
                    ),
                    contentColor = Color.White,
                    depth = 3.dp,
                    buttonShape = ParallelogramShape(shearFactor = 0f) // shearFactor = 0f per un rettangolo
                )
            }
        }
    }
}

