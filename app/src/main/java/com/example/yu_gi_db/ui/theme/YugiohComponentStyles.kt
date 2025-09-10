package com.example.yu_gi_db.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image 
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row 
import androidx.compose.foundation.layout.fillMaxHeight 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size 
import androidx.compose.foundation.layout.width
// androidx.compose.foundation.layout.wrapContentWidth // Non strettamente necessario qui, il comportamento di default di Card in un Box è wrap
import androidx.compose.material3.Card 
import androidx.compose.material3.CardDefaults 
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape 
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale 
import androidx.compose.ui.res.painterResource 
import androidx.compose.ui.res.stringResource 
import androidx.compose.ui.text.style.TextAlign 
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.yu_gi_db.R 


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
    faceColor: Color = SapphireBlue,
    faceBrush: Brush? = null,
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
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = currentDepth, y = currentDepth)
                    .clip(buttonShape)
                    .background(sideColor)
            )
        }

        val faceBackgroundModifier = if (faceBrush != null && enabled) {
            Modifier.background(brush = faceBrush, shape = buttonShape)
        } else {
            Modifier.background(color = if (enabled) faceColor else faceColor.copy(alpha = 0.5f), shape = buttonShape)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .offset(x = currentFaceOffset, y = currentFaceOffset)
                .clip(buttonShape)
                .then(faceBackgroundModifier)
                .border(
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
                style = textStyle
            )
        }
    }
}

@Composable
fun YugiohCardNameDisplay(
    cardName: String,
    modifier: Modifier = Modifier, // Modifier per il Box esterno, controlla allineamento e spazio max
    textStyle: androidx.compose.ui.text.TextStyle = AppTypography.headlineSmall,
    textAlign: TextAlign = TextAlign.Center,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier, // Il modifier del chiamante viene applicato qui
        contentAlignment = Alignment.Center // Centra la Card interna se il Box è più largo
    ) {
        Card(
            // Nessun modifier specifico per la larghezza qui, così si adatta al contenuto.
            // Il modifier .align(Alignment.Center) non è necessario qui perché
            // il Box esterno ha contentAlignment = Alignment.Center.
            shape = RectangleShape, 
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(3.dp, LightSilver)
        ) {
            Box(contentAlignment = Alignment.Center) { // Box interno per sfondo e testo
                Image(
                    painter = painterResource(id = R.drawable.sfondo_descrizioni),
                    contentDescription = stringResource(R.string.card_name_background_description),
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = cardName,
                    style = textStyle,
                    color = LightSilver,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), // Il padding contribuisce alla dimensione della Card
                    textAlign = textAlign
                )
            }
        }
    }
}

@Composable
fun AttributeFrame(
    modifier: Modifier = Modifier,
    attributeName: String,
    attributeImageResId: Int,
    imageWeight: Float = 0.4f,
    textWeight: Float = 0.6f,
    borderColor: Color = LightSilver,
    borderWidth: Dp = 2.dp,
    textStyle: androidx.compose.ui.text.TextStyle = AppTypography.bodyLarge,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    imageContentDescription: String? = stringResource(R.string.attribute_icon_description)
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = attributeImageResId),
                contentDescription = imageContentDescription,
                modifier = Modifier
                    .weight(imageWeight)
                    .fillMaxHeight(),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = attributeName,
                modifier = Modifier
                    .weight(textWeight)
                    .padding(horizontal = 8.dp),
                style = textStyle,
                color = LightSilver, // Qui era textColor, ma la richiesta implicita è LightSilver per il testo di AttributeFrame
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, name = "Yugioh Parallelepiped Button Preview")
@Composable
fun YugiohParallelepipedButtonPreview() {
    YuGiDBTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                YugiohParallelepipedButton(
                    text = "DATABASE",
                    onClick = { },
                    modifier = Modifier.width(280.dp),
                    faceColor = DeepSkyBlueElectric,
                    faceBrush = Brush.verticalGradient(
                        colors = listOf(
                            RoyalBlueDark.darken(0.6f),
                            MidnightBlue,
                            SapphireBlue,
                            ElectricCyan,
                            SapphireBlue,
                            MidnightBlue,
                            RoyalBlueDark.darken(0.6f)
                        )
                    ),
                    contentColor = RoyalBlueDark.darken(factor = 0.6f),
                    depth = 8.dp,
                    buttonShape = ParallelogramShape(shearFactor = 0.15f),
                    textStyle = AppTypography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Yugioh Card Name Display Preview")
@Composable
fun YugiohCardNameDisplayPreview() {
    YuGiDBTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)){
                YugiohCardNameDisplay(
                    cardName = "Drago Bianco Occhi Blu",
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp), 
                    textColor = Color.White 

                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Attribute Frame Preview")
@Composable
fun AttributeFramePreview() {
    YuGiDBTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(16.dp)) {
            AttributeFrame(
                modifier = Modifier.size(width = 150.dp, height = 50.dp), 
                attributeName = "LUCE",
                attributeImageResId = R.drawable.luce,
                textColor = LightSilver // Esplicito per coerenza se AppTypography non lo fa già
            )
        }
    }
}
