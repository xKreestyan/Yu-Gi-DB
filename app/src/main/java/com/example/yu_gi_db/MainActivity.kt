package com.example.yu_gi_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yu_gi_db.ui.theme.YuGiDBTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

import com.example.yu_gi_db.views.SplashScreen
import com.example.yu_gi_db.views.MainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var splash = rememberSaveable { mutableStateOf(true) }
            YuGiDBTheme {
                LaunchedEffect(Unit) {
                    delay(2500)
                    splash.value = false
                }
                if (splash.value) {
                   SplashScreen()
                }
                else {
                    MainScreen()

                }


                }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YuGiDBTheme {
        Greeting("Android")
    }
}