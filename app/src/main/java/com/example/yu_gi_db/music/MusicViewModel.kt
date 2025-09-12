package com.example.yu_gi_db.music

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yu_gi_db.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)

    private val _isMusicOn = MutableStateFlow(false)
    val isMusicOn: StateFlow<Boolean> = _isMusicOn

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val KEY_MUSIC_ON = "key_music_on"
    }

    init {
        loadMusicState()
    }

    private fun loadMusicState() {
        val musicShouldBeOn = sharedPreferences.getBoolean(KEY_MUSIC_ON, false)
        _isMusicOn.value = musicShouldBeOn
        if (musicShouldBeOn) {
            startOrResumeMusicPlayback()
        }
        // Nota: in init, se musicShouldBeOn è false, mediaPlayer è null o non avviato,
        // quindi non è necessaria una pausa esplicita qui.
    }

    private fun startOrResumeMusicPlayback() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(application, R.raw.musicyugidb)
            mediaPlayer?.isLooping = true
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun pauseMusicPlayback() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    private fun stopAndReleaseMusicPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setMusicState(isOn: Boolean) {
        viewModelScope.launch {
            sharedPreferences.edit {
                putBoolean(KEY_MUSIC_ON, isOn)
            }
            _isMusicOn.value = isOn

            if (isOn) {
                startOrResumeMusicPlayback()
            } else {
                pauseMusicPlayback()
            }
        }
    }

    fun toggleMusicState() {
        setMusicState(!_isMusicOn.value)
    }

    /**
     * Controlla la preferenza musicale salvata. Se indica 'ON', la riproduzione musicale
     * viene avviata o ripresa. Se indica 'OFF', la riproduzione musicale viene messa in pausa.
     * Lo stato interno della musica (_isMusicOn) viene aggiornato per riflettere questa preferenza.
     * Questa funzione è utile, ad esempio, quando l'app torna in primo piano.
     */
    fun synchronizeStateWithPreference() {
        viewModelScope.launch {
            val musicIsOnAccordingToPrefs = sharedPreferences.getBoolean(KEY_MUSIC_ON, false)

            // Aggiorna prima il StateFlow esposto, in modo che la UI possa reagire
            _isMusicOn.value = musicIsOnAccordingToPrefs

            // Quindi, controlla il MediaPlayer
            if (musicIsOnAccordingToPrefs) {
                startOrResumeMusicPlayback()
            } else {
                pauseMusicPlayback() // Assicura che la musica sia in pausa se la preferenza è OFF
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAndReleaseMusicPlayback()
    }
}
