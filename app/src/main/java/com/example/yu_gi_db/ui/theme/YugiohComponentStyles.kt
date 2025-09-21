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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = AppTypography.headlineSmall,
    textColor: Color = LightSilver
) {
    Box(
        modifier = modifier, // Modifier from the caller (e.g., fillMaxWidth in preview)
        contentAlignment = Alignment.Center // Centers the Card within this Box if Box is larger
    ) {
        Card(
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(3.dp, LightSilver)
            // Card sizes to its content (the Box with image/Row)
        ) {
            Box( // This Box holds the background image and the content (Row) on top.
                // It ensures the content is centered *within* the Card.
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sfondo_descrizioni),
                    contentDescription = stringResource(R.string.card_name_background_description),
                    modifier = Modifier.matchParentSize(), // Image fills the Card
                    contentScale = ContentScale.Crop
                )
                Row( // This Row now holds the text and icon, arranged to the start.
                    // It will wrap its content.
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), // Padding inside the Card
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // Align text and icon to the start of the Row
                ) {
                    Text(
                        text = cardName,
                        style = textStyle,
                        color = textColor,
                        textAlign = TextAlign.Start
                        // Modifier.weight removed
                    )
                    Spacer(Modifier.width(8.dp)) // Space between text and icon
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) stringResource(R.string.isfavorite) else stringResource(R.string.notfavorite),
                        tint = textColor,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onFavoriteClick)
                    )
                }
            }
        }
    }
}

@Composable
fun AttributeFrame(
    modifier: Modifier = Modifier,
    attributeName: String,
    attributeImageResId: Int,
    attributeImageSize: Dp = 32.dp,
    borderColor: Color = LightSilver,
    borderWidth: Dp = 2.dp,
    textStyle: androidx.compose.ui.text.TextStyle = AppTypography.bodyLarge,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ATTRIBUTO",
                modifier = Modifier.weight(0.6f),
                style = textStyle,
                color = LightSilver,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            Column(
                modifier = Modifier.weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = attributeImageResId),
                    contentDescription = imageContentDescription,
                    modifier = Modifier.size(attributeImageSize),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = attributeName,
                    style = textStyle,
                    color = LightSilver,
                    textAlign = TextAlign.Center
                )
            }
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
    var isFav by remember { mutableStateOf(false) } // Stato per la preview interattiva
    YuGiDBTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)){
                YugiohCardNameDisplay(
                    cardName = "Drago Bianco Occhi Blu",
                    isFavorite = isFav,
                    onFavoriteClick = { isFav = !isFav }, // Toggle per la preview
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp) // fillMaxWidth qui per la preview
                )
                YugiohCardNameDisplay(
                    cardName = "Mago Nero",
                    isFavorite = true, // Esempio con preferito statico
                    onFavoriteClick = { }, // Azione vuota per questo esempio statico
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp) // fillMaxWidth qui per la preview
                )
                 YugiohCardNameDisplay(
                    cardName = "Un nome di carta molto lungo per testare il wrap o l'ellipsis",
                    isFavorite = false,
                    onFavoriteClick = { },
                    modifier = Modifier.width(200.dp).heightIn(min = 40.dp) // Esempio con larghezza fissa
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
                modifier = Modifier.size(width = 180.dp, height = 60.dp),
                attributeName = "LUCE",
                attributeImageResId = R.drawable.luce,
                attributeImageSize = 32.dp
            )
        }
    }
}
