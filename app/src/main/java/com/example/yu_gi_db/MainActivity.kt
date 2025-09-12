package com.example.yu_gi_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yu_gi_db.music.MusicViewModel
import com.example.yu_gi_db.views.InitMainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { InitMainScreen()
            val musicViewModel: MusicViewModel = hiltViewModel() // o viewModel()
              musicViewModel.synchronizeStateWithPreference()

        }
    }
}

/*
* Cose da fare:
* Toast
* Selezione iniziale dei set da scaricare (facoltativo)
* Documentazione finale (priorità)
* Rimuovere warning (priorità)
* */
