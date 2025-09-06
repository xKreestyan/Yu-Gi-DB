package com.example.yu_gi_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font // Import per Font
import androidx.compose.ui.text.font.FontFamily // Assicurati che questo import sia presente
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight // Import per FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import com.example.yu_gi_db.views.InitMainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { InitMainScreen() }
    }
}

/*
* Cose da fare:
* Ricerca tramite ID
* Compressione foto per la lista di smallcards (migliorerà le prestazioni)
* Lingue
* Toast
* Selezione iniziale dei set
* Carte preferite
* Documentazione finale
* Rimuovere warning
* */
