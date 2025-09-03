package com.example.yu_gi_db.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.yu_gi_db.R

@Composable
fun MenuScreen() {
    val constraints = ConstraintSet {
        val titleRef = createRefFor("title")
        val playButtonRef = createRefFor("play_button")
        val optionsButtonRef = createRefFor("options_button")
        val exitButtonRef = createRefFor("exit_button")

        constrain(titleRef) {
            top.linkTo(parent.top, margin = 32.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(playButtonRef) {
            top.linkTo(titleRef.bottom, margin = 64.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
        }

        constrain(optionsButtonRef) {
            top.linkTo(playButtonRef.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
        }

        constrain(exitButtonRef) {
            top.linkTo(optionsButtonRef.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
            //bottom.linkTo(parent.bottom, margin = 32.dp) // Option to constrain to bottom
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.schermata_menu_yugioh), // Sostituisci con il nome del tuo file
            contentDescription = "Sfondo del menu",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Modificato a Crop
        )

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Menu Principale",
                modifier = Modifier.layoutId("title")

                // Aggiungi qui lo stile del testo se necessario
            )

            Button(
                onClick = { /* Azione per il pulsante Play */ },
                modifier = Modifier.layoutId("play_button")
            ) {
                Text("Gioca")
            }

            Button(
                onClick = { /* Azione per il pulsante Opzioni */ },
                modifier = Modifier.layoutId("options_button")
            ) {
                Text("Opzioni")
            }

            Button(
                onClick = { /* Azione per il pulsante Esci */ },
                modifier = Modifier.layoutId("exit_button")
            ) {
                Text("Esci")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    // Potresti voler avvolgere MenuScreen con il tuo tema dell'applicazione qui
    // Ad esempio: MyTheme { MenuScreen() }
    MenuScreen()
}

// Eventuali altri stili o Composable che potrebbero esserci già nel file
// ...