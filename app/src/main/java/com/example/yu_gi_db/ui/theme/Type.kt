package com.example.yu_gi_db.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yu_gi_db.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme // Importa MaterialTheme per la preview
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview // Importa Preview
import androidx.compose.ui.unit.dp


// --- 1. DEFINIZIONI DI FONT FAMILY SPECIFICHE PER OGNI FILE FONT ---
// Font "Bold" per titoli principali (700)
val YugiohItcStoneSerifSmallCapsBold = FontFamily(
    Font(R.font.yu_gi_oh_itc_stone_serif_small_caps_bold, FontWeight.Bold)
)

// Font "Semibold" (600)
val StoneSerifSemibold = FontFamily(
    Font(R.font.stone_serif_semibold, FontWeight.SemiBold)
)

// Font "Italic" (normal/400, italic)
val YugiohItcStoneSerifLtItalic = FontFamily(
    Font(R.font.yu_gi_oh_itc_stone_serif_lt_italic, FontWeight.Normal, FontStyle.Italic)
)

// I tre font "Normal" (400), definiti separatamente per poterli scegliere
val YugiohMatrixBook = FontFamily(
    Font(R.font.yu_gi_oh_matrix_book, FontWeight.Normal)
)
val YugiohMatrixRegularSmallCaps1 = FontFamily(
    Font(R.font.yu_gi_oh_matrix_regular_small_caps_1, FontWeight.Normal)
)
val YugiohMatrixRegularSmallCaps2 = FontFamily(
    Font(R.font.yu_gi_oh_matrix_regular_small_caps_2, FontWeight.Normal)
)

// --- 2. DEFINIZIONE DELLA TIPOGRAFIA DELL'APP ---
// Ora puoi assegnare queste FontFamily specifiche ai vari stili di testo.
// Questa è una possibile configurazione, adattala alle tue preferenze di design.
val AppTypography = Typography(
    // Display styles (i più grandi)
    displayLarge = TextStyle(
        fontFamily = YugiohItcStoneSerifSmallCapsBold, // Il tuo Small Caps Bold per i titoli più grandi
        // fontWeight = FontWeight.Bold, // Opzionale se la FontFamily contiene solo quel font/peso
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = YugiohItcStoneSerifSmallCapsBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = YugiohItcStoneSerifSmallCapsBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = YugiohItcStoneSerifSmallCapsBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle( // Ad esempio per i titoli delle sezioni nel menu
        fontFamily = YugiohItcStoneSerifSmallCapsBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle( // Ad esempio per i nomi delle carte
        fontFamily = YugiohItcStoneSerifSmallCapsBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title styles
    titleLarge = TextStyle( // Titoli un po' meno prominenti
        fontFamily = StoneSerifSemibold, // Usiamo il Semibold qui per varietà
        // fontWeight = FontWeight.SemiBold, // Opzionale
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle( // Per Attributi, Tipo Carta, ATK/DEF labels
        fontFamily = StoneSerifSemibold, // O YugiohItcStoneSerifSmallCapsBold se preferisci
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = StoneSerifSemibold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body text styles
    bodyLarge = TextStyle( // Testo descrittivo più grande
        fontFamily = YugiohMatrixBook, // Il tuo font "Matrix Book" per leggibilità
        // fontWeight = FontWeight.Normal, // Opzionale
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle( // Testo principale per gli effetti delle carte
        fontFamily = YugiohMatrixRegularSmallCaps1, // Uno dei tuoi Small Caps normali
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle( // Testo più piccolo, note, copyright, ecc.
        fontFamily = YugiohMatrixRegularSmallCaps2, // L'altro Small Caps normale
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label styles (per pulsanti, testo piccolo e incisivo)
    labelLarge = TextStyle( // Per i pulsanti principali
        fontFamily = YugiohItcStoneSerifSmallCapsBold, // Il tuo Small Caps Bold
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = StoneSerifSemibold, // O un altro Small Caps normale
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle( // Per etichette molto piccole o testo legale
        fontFamily = YugiohMatrixBook, // Il Matrix Book per leggibilità anche se piccolo
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
// ... (tutto il codice di Type.kt che abbiamo appena scritto, fino alla fine di AppTypography) ...

// --- PREVIEW PER LA TIPOGRAFIA DELL'APP ---

@Preview(showBackground = true, name = "Yu-Gi-Oh! Typography Styles", widthDp = 400, heightDp = 1200)
@Composable
fun AppTypographyPreview() {
    // Applica il tuo tema che include AppTypography.
    // Se hai definito YuGiDBTheme (o come si chiama il tuo tema)
    // che usa AppTypography, usalo. Altrimenti, crea un MaterialTheme al volo.
    // Assumendo che tu abbia YuGiDBTheme:
    YuGiDBTheme { // Sostituisci con il nome del tuo tema se diverso
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Per vedere tutti gli stili se sono troppi
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Display Large", style = AppTypography.displayLarge)
                Text("Display Medium", style = AppTypography.displayMedium)
                Text("Display Small", style = AppTypography.displaySmall)
                Spacer(Modifier.height(16.dp))

                Text("Headline Large", style = AppTypography.headlineLarge)
                Text("Headline Medium (Nome Sezione Menu)", style = AppTypography.headlineMedium)
                Text("Headline Small (Nome Carta)", style = AppTypography.headlineSmall)
                Spacer(Modifier.height(16.dp))

                Text("Title Large (Titolo Meno Prominente)", style = AppTypography.titleLarge)
                Text("Title Medium (Label ATK/DEF)", style = AppTypography.titleMedium)
                Text("Title Small", style = AppTypography.titleSmall)
                Spacer(Modifier.height(16.dp))

                Text("Body Large (Testo Descrittivo)", style = AppTypography.bodyLarge)
                Text(
                    "Body Medium (Testo Effetto Carta): Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus.",
                    style = AppTypography.bodyMedium
                )
                Text(
                    "Body Small (Note, Copyright): Sed ut perspiciatis unde omnis iste natus error sit voluptatem.",
                    style = AppTypography.bodySmall
                )
                Spacer(Modifier.height(16.dp))

                Text("Label Large (Pulsanti)", style = AppTypography.labelLarge, modifier = Modifier.background(Color.LightGray.copy(alpha=0.3f)))
                Text("Label Medium", style = AppTypography.labelMedium)
                Text("Label Small (Testo Legale)", style = AppTypography.labelSmall)

                // Esempio per testare un font specifico direttamente (non tramite AppTypography)
                Spacer(Modifier.height(24.dp))
                Text(
                    "Test Diretto: YugiohItcStoneSerifLtItalic",
                    fontFamily = YugiohItcStoneSerifLtItalic, // Usa la FontFamily direttamente
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic // Anche se la FontFamily lo specifica, è buona norma essere espliciti
                )
                Text(
                    "Test Diretto: YugiohMatrixBook",
                    fontFamily = YugiohMatrixBook,
                    fontSize = 18.sp
                )
                Text(
                    "Test Diretto: YugiohMatrixRegularSmallCaps1",
                    fontFamily = YugiohMatrixRegularSmallCaps1,
                    fontSize = 18.sp
                )
                Text(
                    "Test Diretto: YugiohMatrixRegularSmallCaps2",
                    fontFamily = YugiohMatrixRegularSmallCaps2,
                    fontSize = 18.sp
                )
            }
        }
    }
}

