package com.example.yu_gi_db.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // <<< FIX DEPRECATED

@Composable
fun InitMusicView(
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel = hiltViewModel() // Replace ActualMusicViewModel with your concrete Hilt ViewModel class
) {
    val isMusicOn by musicViewModel.isMusicOn.collectAsState()

    MusicView(
        modifier = modifier,
        isMusicOn = isMusicOn,
        onToggleMusic = { musicViewModel.toggleMusicState() }
    )
}
@Composable
fun MusicView(
    modifier: Modifier = Modifier,
    isMusicOn: Boolean=false,
    onToggleMusic: () -> Unit={} // Callback for the button click
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isMusicOn) "Music: ON" else "Music: OFF",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onToggleMusic) {
            Text(text = if (isMusicOn) "Turn Music OFF" else "Turn Music ON")
        }
    }
}


@Preview(showBackground = true, name = "InitMusicView - Music OFF")
@Composable
fun InitMusicView_Preview_MusicOff() {
    MaterialTheme {
        MusicView(
        )
    }
}
