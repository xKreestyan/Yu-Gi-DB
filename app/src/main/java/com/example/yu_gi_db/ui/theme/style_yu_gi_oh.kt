package com.example.yu_gi_db.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yu_gi_db.R

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(3.dp),
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.yu_gi_oh_itc_stone_serif_small_caps_bold, weight = FontWeight.Bold, style = FontStyle.Italic))
    )
}

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        YuGiDBTheme {
            Greeting("Android")
        }
    }
